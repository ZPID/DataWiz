package de.zpid.datawiz.service;

import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import de.zpid.datawiz.dao.FileDAO;
import de.zpid.datawiz.dao.ProjectDAO;
import de.zpid.datawiz.dao.RecordDAO;
import de.zpid.datawiz.dao.StudyConstructDAO;
import de.zpid.datawiz.dao.StudyDAO;
import de.zpid.datawiz.dao.StudyInstrumentDAO;
import de.zpid.datawiz.dao.StudyListTypesDAO;
import de.zpid.datawiz.dto.FileDTO;
import de.zpid.datawiz.dto.RecordCompareDTO;
import de.zpid.datawiz.dto.RecordDTO;
import de.zpid.datawiz.dto.UserDTO;
import de.zpid.datawiz.enumeration.DWFieldTypes;
import de.zpid.datawiz.enumeration.DataWizErrorCodes;
import de.zpid.datawiz.enumeration.MinioResult;
import de.zpid.datawiz.enumeration.Roles;
import de.zpid.datawiz.enumeration.VariableStatus;
import de.zpid.datawiz.exceptions.DataWizSystemException;
import de.zpid.datawiz.form.StudyForm;
import de.zpid.datawiz.util.MinioUtil;
import de.zpid.datawiz.util.RegexUtil;
import de.zpid.spss.dto.SPSSValueLabelDTO;
import de.zpid.spss.dto.SPSSVarDTO;
import de.zpid.spss.util.SPSSMeasLevel;
import de.zpid.spss.util.SPSSMissing;
import de.zpid.spss.util.SPSSMonths;
import de.zpid.spss.util.SPSSVarTypes;

@Service
public class RecordService {

	private static Logger log = LogManager.getLogger(RecordService.class);

	@Autowired
	private ImportService importService;
	@Autowired
	private ProjectDAO projectDAO;
	@Autowired
	private StudyDAO studyDAO;
	@Autowired
	private StudyListTypesDAO studyListTypesDAO;
	@Autowired
	private StudyConstructDAO studyConstructDAO;
	@Autowired
	private StudyInstrumentDAO studyInstrumentDAO;
	@Autowired
	private RecordDAO recordDAO;
	@Autowired
	private ClassPathXmlApplicationContext applicationContext;
	@Autowired
	private PlatformTransactionManager txManager;
	@Autowired
	private MinioUtil minioUtil;
	@Autowired
	private FileDAO fileDAO;
	@Autowired
	private MessageSource messageSource;

	final static private int VAR_LABEL_MAX_LENGTH = 250;
	final static private int VAR_NAME_MAX_LENGTH = 50;

	/**
	 * @param pid
	 * @param studyId
	 * @param recordId
	 * @param versionId
	 * @param redirectAttributes
	 * @param subpage
	 * @param sForm
	 * @throws Exception
	 */
	public StudyForm setStudyform(final Optional<Long> pid, final Optional<Long> studyId, final Optional<Long> recordId, final Optional<Long> versionId,
	    final Optional<String> subpage, final List<String> parsingErrors) throws Exception {
		if (!pid.isPresent())
			throw new DataWizSystemException(messageSource.getMessage("logging.pid.not.present", null, Locale.ENGLISH),
			    DataWizErrorCodes.MISSING_PID_ERROR);
		if (!studyId.isPresent())
			throw new DataWizSystemException(messageSource.getMessage("logging.studyid.not.present", null, Locale.ENGLISH),
			    DataWizErrorCodes.MISSING_STUDYID_ERROR);
		StudyForm sForm = (StudyForm) applicationContext.getBean("StudyForm");
		sForm.setProject(projectDAO.findById(pid.get()));
		if (sForm.getProject() == null) {
			throw new DataWizSystemException(messageSource.getMessage("logging.project.not.found", new Object[] { pid.get() }, Locale.ENGLISH),
			    DataWizErrorCodes.PROJECT_NOT_AVAILABLE);
		}
		sForm.setStudy(studyDAO.findById(studyId.get(), pid.get(), true, false));
		if (sForm.getStudy() == null)
			throw new DataWizSystemException(messageSource.getMessage("logging.study.not.found", new Object[] { studyId.get() }, Locale.ENGLISH),
			    DataWizErrorCodes.STUDY_NOT_AVAILABLE);
		if (recordId.isPresent()) {
			RecordDTO rec = (recordDAO.findRecordWithID(recordId.get(), (versionId.isPresent() && versionId.get() > 0 ? versionId.get() : 0)));
			if (rec != null) {
				if (!subpage.isPresent()) {
					sForm.setRecords(recordDAO.findRecordVersionList(recordId.get()));
				} else if (subpage.get().equals("codebook")) {
					sForm.getStudy().setConstructs(studyConstructDAO.findAllByStudy(studyId.get()));
					sForm.getStudy().setMeasOcc(studyListTypesDAO.findAllByStudyAndType(studyId.get(), DWFieldTypes.MEASOCCNAME));
					sForm.getStudy().setInstruments(studyInstrumentDAO.findAllByStudy(studyId.get(), true));
					rec.setVariables(recordDAO.findVariablesByVersionID(rec.getVersionId()));
					setCodebook(parsingErrors, rec, false);
				} else {
					rec.setVariables(recordDAO.findVariablesByVersionID(rec.getVersionId()));
					setDataMatrix(parsingErrors, rec, false);
				}
			} else {
				throw new DataWizSystemException(messageSource.getMessage("logging.record.not.found", new Object[] { recordId.get() }, Locale.ENGLISH),
				    DataWizErrorCodes.RECORD_NOT_AVAILABLE);
			}
			sForm.setRecord(rec);
		}
		return sForm;
	}

	public void setCodebook(final List<String> parsingErrors, final RecordDTO rec, final boolean isSPSS) throws Exception {
		rec.setAttributes(recordDAO.findRecordAttributes(rec.getVersionId(), true));
		if (rec.getVariables() != null && rec.getVariables().size() > 0) {
			for (SPSSVarDTO var : rec.getVariables()) {
				var.setAttributes(recordDAO.findVariableAttributes(var.getId(), false));
				importService.sortVariableAttributes(var);
				var.setValues(recordDAO.findVariableValues(var.getId(), false));
				// SET DATE VALUES TO VIEW DATE (d.M.yyyy)
				if (!isSPSS) {
					if (RecordDTO.simplifyVarTypes(var.getType()).equals(SPSSVarTypes.SPSS_FMT_DATE)) {
						var.getValues().parallelStream().forEach(value -> {
							String viewDate = null;
							viewDate = parseDateToViewTime(value.getValue(), var.getType(), parsingErrors, var, "value-label", false);
							if (viewDate != null)
								value.setValue(viewDate);
						});
						String missing;
						missing = parseDateToViewTime(var.getMissingVal1(), var.getType(), parsingErrors, var, "missingVal1", true);
						if (missing != null)
							var.setMissingVal1(missing);
						missing = parseDateToViewTime(var.getMissingVal2(), var.getType(), parsingErrors, var, "missingVal2", true);
						if (missing != null)
							var.setMissingVal2(missing);
						missing = parseDateToViewTime(var.getMissingVal3(), var.getType(), parsingErrors, var, "missingVal3", true);
						if (missing != null)
							var.setMissingVal3(missing);
					}
				}
			}
		}
	}

	public void setDataMatrix(final List<String> parsingErrors, final RecordDTO rec, final boolean isSPSS) throws Exception {
		rec.setDataMatrixJson(recordDAO.findMatrixByVersionId(rec.getVersionId()));
		if (rec.getDataMatrixJson() != null && !rec.getDataMatrixJson().isEmpty()) {
			if (rec.getDataMatrixJson() != null && !rec.getDataMatrixJson().isEmpty())
				rec.setDataMatrix(new Gson().fromJson(rec.getDataMatrixJson(), new TypeToken<List<List<Object>>>() {
				}.getType()));
		}
		if (!isSPSS && rec.getVariables() != null && rec.getVariables().size() > 0 && rec.getDataMatrix() != null && !rec.getDataMatrix().isEmpty()) {
			int varPosition = 0;
			for (SPSSVarDTO var : rec.getVariables()) {
				int sd = varPosition++;
				rec.getDataMatrix().parallelStream().forEach(row -> {
					if (RecordDTO.simplifyVarTypes(var.getType()).equals(SPSSVarTypes.SPSS_FMT_DATE) && row.get(sd) != null
					    && !String.valueOf(row.get(sd)).isEmpty()) {
						String viewDate = null;
						viewDate = parseDateToViewTime(String.valueOf(row.get(sd)), var.getType(), parsingErrors, var, "matrix-row-" + sd, false);
						if (viewDate != null && !viewDate.isEmpty()) {
							row.set(sd, viewDate);
						}
					} else if (RecordDTO.simplifyVarTypes(var.getType()).equals(SPSSVarTypes.SPSS_FMT_F) && row.get(sd) != null) {
						if (!String.valueOf(row.get(sd)).isEmpty()) {
							BigDecimal dec = new BigDecimal(String.valueOf(row.get(sd)));
							dec.stripTrailingZeros();
							if (var.getDecimals() <= 0) {
								row.set(sd, Math.round(dec.doubleValue()));
							} else {
								row.set(sd, dec);
							}
						} else {
							row.set(sd, null);
						}
					}
				});
			}
		}
	}

	/**
	 * @param valueString
	 * @return
	 * @throws DataWizSystemException
	 */
	private String parseDateToViewTime(String valueString, SPSSVarTypes varType, final List<String> parsingErrors, final SPSSVarDTO var,
	    final String type, boolean missing) {
		String viewDate = null;
		if (valueString != null && !valueString.isEmpty()) {
			try {
				LocalDate date;
				if (varType.equals(SPSSVarTypes.SPSS_FMT_DATE_TIME)) {
					String[] dt = valueString.trim().split(" ");
					date = LocalDate.parse(dt[0].trim(), DateTimeFormatter.ofPattern("M/d/yyyy"));
					viewDate = ((date.getDayOfMonth() <= 9 ? "0" + date.getDayOfMonth() : date.getDayOfMonth()) + "."
					    + (date.getMonthValue() <= 9 ? "0" + date.getMonthValue() : date.getMonthValue()) + "." + date.getYear() + " " + dt[1].trim()).trim();
				} else if (varType.equals(SPSSVarTypes.SPSS_FMT_DATE) || varType.equals(SPSSVarTypes.SPSS_FMT_ADATE)
				    || varType.equals(SPSSVarTypes.SPSS_FMT_JDATE) || varType.equals(SPSSVarTypes.SPSS_FMT_EDATE)
				    || varType.equals(SPSSVarTypes.SPSS_FMT_SDATE)) {
					date = LocalDate.parse(valueString.trim(), DateTimeFormatter.ofPattern("M/d/yyyy"));
					viewDate = ((date.getDayOfMonth() <= 9 ? "0" + date.getDayOfMonth() : date.getDayOfMonth()) + "."
					    + (date.getMonthValue() <= 9 ? "0" + date.getMonthValue() : date.getMonthValue()) + "." + date.getYear()).trim();
				}
			} catch (Exception e) {
				log.warn(
				    "DBS Warning in parseDateToViewTime: corrupt {} date found in database: value [{}] of variable [id:{}] doesn't match the correct dateformat",
				    () -> type, () -> valueString, () -> var.getId());
				parsingErrors.add(messageSource.getMessage("record.dbs.date.corrupt",
				    new Object[] { valueString,
				        messageSource.getMessage(missing ? "dataset.import.report.codebook.missings" : "dataset.import.report.codebook.values", null,
				            LocaleContextHolder.getLocale()),
				        var.getName() },
				    LocaleContextHolder.getLocale()));
			}
		}
		return viewDate;
	}

	/**
	 * @param valueString
	 * @return
	 * @throws DataWizSystemException
	 */
	private String parseDateToDBTime(String valueString, SPSSVarTypes varType, final Set<String> parsingErrors, final SPSSVarDTO var, final String type,
	    boolean missing) {
		String viewDate = null;
		try {
			LocalDate date;
			if (varType.equals(SPSSVarTypes.SPSS_FMT_DATE_TIME)) {
				String[] dt = valueString.trim().split(" ");
				date = LocalDate.parse(dt[0].trim(), DateTimeFormatter.ofPattern("d.M.yyyy"));
				viewDate = ((date.getMonthValue() <= 9 ? "0" + date.getMonthValue() : date.getMonthValue()) + "/"
				    + (date.getDayOfMonth() <= 9 ? "0" + date.getDayOfMonth() : date.getDayOfMonth()) + "/" + date.getYear() + " " + dt[1].trim()).trim();
			} else if (varType.equals(SPSSVarTypes.SPSS_FMT_DATE) || varType.equals(SPSSVarTypes.SPSS_FMT_ADATE)
			    || varType.equals(SPSSVarTypes.SPSS_FMT_JDATE) || varType.equals(SPSSVarTypes.SPSS_FMT_EDATE)
			    || varType.equals(SPSSVarTypes.SPSS_FMT_SDATE)) {
				date = LocalDate.parse(valueString.trim(), DateTimeFormatter.ofPattern("d.M.yyyy"));
				viewDate = ((date.getMonthValue() <= 9 ? "0" + date.getMonthValue() : date.getMonthValue()) + "/"
				    + (date.getDayOfMonth() <= 9 ? "0" + date.getDayOfMonth() : date.getDayOfMonth()) + "/" + date.getYear()).trim();
			}
		} catch (Exception e) {
			log.debug("corrupt {} date found in form: value [{}] of variable [id:{}] doesn't match the correct dateformat", () -> type, () -> valueString,
			    () -> var.getId());
			parsingErrors.add(messageSource.getMessage("record.dbs.date.corrupt",
			    new Object[] { valueString,
			        messageSource.getMessage(missing ? "dataset.import.report.codebook.missings" : "dataset.import.report.codebook.values", null,
			            LocaleContextHolder.getLocale()),
			        var.getName() },
			    LocaleContextHolder.getLocale()));
		}
		return viewDate;
	}

	/**
	 * @param studyId
	 * @param recordId
	 * @param sForm
	 * @param user
	 * @throws Exception
	 */
	public void insertOrUpdateRecordMetadata(final Optional<Long> studyId, final Optional<Long> recordId, StudyForm sForm, final UserDTO user)
	    throws Exception {
		if (!recordId.isPresent() || sForm.getRecord().getId() <= 0) {
			sForm.getRecord().setCreatedBy(user.getEmail());
			sForm.getRecord().setStudyId(studyId.get());
			recordDAO.insertRecordMetaData(sForm.getRecord());
		} else {
			recordDAO.updateRecordMetaData(sForm.getRecord());
		}
	}

	/**
	 * @param varVal
	 * @param sForm
	 */
	public void setVariableValues(SPSSVarDTO varVal, StudyForm sForm) {
		Iterator<SPSSValueLabelDTO> itt = varVal.getValues().iterator();
		while (itt.hasNext()) {
			SPSSValueLabelDTO val = itt.next();
			if ((val.getLabel() == null || val.getLabel().trim().isEmpty()) && (val.getValue() == null || val.getValue().trim().isEmpty())) {
				itt.remove();
			}
		}
		if (varVal.getId() > 0) {
			for (SPSSVarDTO var : sForm.getRecord().getVariables()) {
				if (var.getId() == varVal.getId()) {
					var.setValues(varVal.getValues());
					break;
				}
			}
		} else if (varVal.getId() == -1) {
			sForm.getRecord().getVariables().parallelStream().forEach(var -> {
				List<SPSSValueLabelDTO> global = new ArrayList<>();
				boolean setGlobal = false;
				for (SPSSValueLabelDTO val : varVal.getValues()) {
					SPSSValueLabelDTO newVal = new SPSSValueLabelDTO();
					newVal.setLabel(val.getLabel());
					newVal.setValue(val.getValue());
					SPSSVarTypes type = SPSSVarTypes.fromInt((int) val.getId());
					if (type.equals(SPSSVarTypes.SPSS_FMT_A) && RecordDTO.simplifyVarTypes(var.getType()).equals(SPSSVarTypes.SPSS_FMT_A)) {
						setGlobal = true;
						global.add(newVal);
					} else if (type.equals(SPSSVarTypes.SPSS_FMT_F) && RecordDTO.simplifyVarTypes(var.getType()).equals(SPSSVarTypes.SPSS_FMT_F)) {
						setGlobal = true;
						global.add(newVal);
					} else if (type.equals(SPSSVarTypes.SPSS_FMT_DATE) && var.getType().equals(SPSSVarTypes.SPSS_FMT_DATE)) {
						setGlobal = true;
						global.add(newVal);
					}
				}
				if (setGlobal)
					var.setValues(global);
			});
		}
	}

	/**
	 * @param sForm
	 * @param redirectAttributes
	 * @param currentVersion
	 * @throws DataWizSystemException
	 */
	public String compareAndSaveCodebook(final RecordDTO currentVersion, final Set<String> parsingErrors, final String changelog)
	    throws DataWizSystemException {
		RecordDTO lastVersion;
		String msg = null;
		TransactionStatus status = txManager.getTransaction(new DefaultTransactionDefinition());
		try {
			lastVersion = recordDAO.findRecordWithID(currentVersion.getId(), 0);
			lastVersion.setVariables(recordDAO.findVariablesByVersionID(lastVersion.getVersionId()));
			lastVersion.setAttributes(recordDAO.findRecordAttributes(lastVersion.getVersionId(), false));
			currentVersion.setDataMatrixJson(recordDAO.findMatrixByVersionId(currentVersion.getVersionId()));
			if (lastVersion.getVariables() != null && lastVersion.getVariables().size() > 0) {
				for (SPSSVarDTO var : lastVersion.getVariables()) {
					var.setAttributes(recordDAO.findVariableAttributes(var.getId(), false));
					importService.sortVariableAttributes(var);
					var.setValues(recordDAO.findVariableValues(var.getId(), false));
				}
			}
			if (currentVersion != null && lastVersion != null && currentVersion.getVariables() != null && lastVersion.getVariables() != null
			    && !currentVersion.getVariables().equals(lastVersion.getVariables())) {
				currentVersion.setChangeLog(changelog);
				recordDAO.insertCodeBookMetaData(currentVersion);
				recordDAO.insertAttributes(currentVersion.getAttributes(), currentVersion.getVersionId(), 0);
				int i = 0;
				for (SPSSVarDTO var : currentVersion.getVariables()) {
					long varId = var.getId();
					SPSSVarDTO dbvar = null;
					if (lastVersion != null && lastVersion.getVariables() != null && lastVersion.getVariables().size() > i) {
						dbvar = lastVersion.getVariables().get(i++);
						dbvar.setId(0);
						var.setId(0);
					}
					if (dbvar != null && var.equals(dbvar)) {
						recordDAO.insertVariableVersionRelation(varId, currentVersion.getVersionId(), var.getPosition(),
						    messageSource.getMessage("import.check.EQUAL", null, LocaleContextHolder.getLocale()));
					} else {
						varId = recordDAO.insertVariable(var);
						recordDAO.insertVariableVersionRelation(varId, currentVersion.getVersionId(), var.getPosition(),
						    messageSource.getMessage("import.check.NEW_VAR", null, LocaleContextHolder.getLocale()));
						if (var.getAttributes() != null) {
							removeEmptyDWAttributes(var.getDw_attributes());
							var.getAttributes().addAll(var.getDw_attributes());
							recordDAO.insertAttributes(var.getAttributes(), currentVersion.getVersionId(), varId);
						}
						if (var.getValues() != null)
							recordDAO.insertVarLabels(var.getValues(), varId);
					}
				}
				recordDAO.insertMatrix(currentVersion);
				txManager.commit(status);
				msg = "record.codebook.saved";
			} else if (currentVersion == null || lastVersion == null || currentVersion.getVariables() == null || lastVersion.getVariables() == null) {
				msg = "record.codebook.data.corrupt";
			} else if (currentVersion.getVariables().equals(lastVersion.getVariables())) {
				msg = "record.codebook.versions.equal";
			}
		} catch (Exception e) {
			txManager.rollback(status);
			throw new DataWizSystemException(messageSource.getMessage("logging.database.error", new Object[] { e.getMessage() }, Locale.ENGLISH),
			    DataWizErrorCodes.DATABASE_ERROR, e);
		}
		return msg;
	}

	/**
	 * @param pid
	 * @param studyId
	 * @param recordId
	 * @param user
	 * @param ret
	 * @return
	 * @throws DataWizSystemException
	 * @throws NoSuchMessageException
	 */
	public void deleteRecord(final Optional<Long> pid, final Optional<Long> studyId, final Optional<Long> recordId, final UserDTO user,
	    boolean singleCommit) throws DataWizSystemException {
		log.trace("Entering  deleteRecord for [recordId: {}; studyId {}; projectId {}] user[id: {}; email: {}]", () -> recordId.get(),
		    () -> studyId.get(), () -> pid.get(), () -> user.getId(), () -> user.getEmail());
		TransactionStatus status = null;
		if (singleCommit)
			status = txManager.getTransaction(new DefaultTransactionDefinition());
		try {
			RecordDTO record = recordDAO.findRecordWithID(recordId.get(), 0);
			if (record != null && (user.hasRole(Roles.ADMIN) || user.hasRole(Roles.PROJECT_ADMIN, pid.get(), false)
			    || ((user.hasRole(Roles.PROJECT_WRITER, pid.get(), false) || user.hasRole(Roles.DS_WRITER, studyId.get(), true))
			        && record.getCreatedBy().trim().equals(user.getEmail().trim())))) {
				List<RecordDTO> versions = recordDAO.findRecordVersionList(recordId.get());
				if (versions != null) {
					for (RecordDTO rec : versions) {
						rec.setVariables(recordDAO.findVariablesByVersionID(rec.getVersionId()));
					}
				}
				recordDAO.deleteRecord(recordId.get());
				if (versions != null) {
					for (RecordDTO rec : versions) {
						if (rec.getVariables() != null) {
							for (SPSSVarDTO var : rec.getVariables()) {
								recordDAO.deleteVariable(var.getId());
							}
						}
					}
				}
				MinioResult res = minioUtil.cleanAndRemoveBucket(pid.get(), studyId.get(), recordId.get());
				if (res.equals(MinioResult.OK)) {
					if (singleCommit)
						txManager.commit(status);
					log.trace("Method deleteRecord completed");
				} else {
					log.warn("Record [id: {}] not delteted: Transaction was rolled back!");
					throw new DataWizSystemException(messageSource.getMessage("logging.minio.delete.error", new Object[] { res }, Locale.ENGLISH),
					    DataWizErrorCodes.MINIO_DELETE_ERROR);
				}
			} else {
				log.warn("User [email:{}; id: {}] tried to delete Record [projectId: {}; studyId: {}; recordId: {}]", () -> user.getEmail(),
				    () -> user.getId(), () -> pid, () -> studyId, () -> recordId);
				throw new DataWizSystemException(
				    messageSource.getMessage("logging.user.permitted", new Object[] { user.getEmail(), "record", recordId.get() }, Locale.ENGLISH),
				    DataWizErrorCodes.USER_ACCESS_RECORD_PERMITTED);
			}
		} catch (Exception e) {
			if (singleCommit)
				txManager.rollback(status);
			if (e instanceof DataWizSystemException) {
				log.warn("DeleteRecord DataWizSystemException {}:", () -> ((DataWizSystemException) e).getErrorCode(), () -> e);
				throw new DataWizSystemException(
				    messageSource.getMessage("logging.record.delete.error", new Object[] { user.getEmail(), "record", recordId.get() }, Locale.ENGLISH),
				    DataWizErrorCodes.RECORD_DELETE_ERROR);
			} else {
				log.warn("DeleteRecord Database-Exception:", () -> e);
				throw new DataWizSystemException(messageSource.getMessage("logging.database.error", new Object[] { e.getMessage() }, Locale.ENGLISH),
				    DataWizErrorCodes.DATABASE_ERROR, e);
			}
		}
	}

	/**
	 * @param model
	 * @param sForm
	 * @param parsingErrors
	 */
	public String validateCodeBook(StudyForm sForm) {
		Set<String> parsingErrors = new HashSet<String>();
		Set<String> parsingWarnings = new HashSet<String>();
		try {
			validateAndPrepareCodebookForm(sForm.getRecord(), parsingErrors, parsingWarnings, null, true);
			sForm.setWarnings(parsingWarnings);
		} catch (DataWizSystemException e) {
			log.debug("Parsing Exception during saveCodebook - Code{}; Message: {}", () -> e.getErrorCode(), () -> e.getMessage());
		}
		String ret = "";
		if (parsingErrors != null)
			ret = setMessageString(parsingErrors.parallelStream().collect(Collectors.toList()));
		return ret;
	}

	/**
	 * @param currentVersion
	 * @param parsingErrors
	 * @throws DataWizSystemException
	 */
	public void validateAndPrepareCodebookForm(final RecordDTO currentVersion, final Set<String> parsingErrors, final Set<String> parsingWarnings,
	    final String changelog, final boolean onlyValidation) throws DataWizSystemException {
		boolean missingChangelog = false;
		if (!onlyValidation) {
			if (changelog == null || changelog.trim().isEmpty()) {
				parsingErrors.add(messageSource.getMessage("record.codebook.changelog.missing", null, LocaleContextHolder.getLocale()));
				missingChangelog = true;
			}
		}
		if (!missingChangelog) {
			Set<String> names = new HashSet<String>();
			if (currentVersion != null && currentVersion.getVariables() != null)
				currentVersion.getVariables().parallelStream().forEach((var) -> {
					if (var.getName() == null || var.getName().isEmpty()) {
						parsingErrors.add(messageSource.getMessage("record.name.empty", new Object[] { var.getPosition() }, LocaleContextHolder.getLocale()));
					} else if (var.getName().getBytes(Charset.forName("UTF-8")).length > VAR_NAME_MAX_LENGTH) {
						parsingErrors.add(messageSource.getMessage("record.name.too.long", new Object[] { var.getPosition(), var.getName(), VAR_NAME_MAX_LENGTH },
						    LocaleContextHolder.getLocale()));
					} else if (!names.add(var.getName().toUpperCase())) {
						parsingErrors.add(
						    messageSource.getMessage("record.name.equal", new Object[] { var.getPosition(), var.getName() }, LocaleContextHolder.getLocale()));
					} else if (!Pattern.compile(RegexUtil.VAR_NAME_REGEX).matcher(var.getName()).find()) {
						parsingErrors.add(
						    messageSource.getMessage("record.name.invalid", new Object[] { var.getPosition(), var.getName() }, LocaleContextHolder.getLocale()));
					}
					if (var.getLabel() != null && !var.getLabel().isEmpty()
					    && var.getLabel().getBytes(Charset.forName("UTF-8")).length > VAR_LABEL_MAX_LENGTH) {
						parsingErrors.add(messageSource.getMessage("record.label.too.long",
						    new Object[] { var.getPosition(), var.getName(), VAR_LABEL_MAX_LENGTH }, LocaleContextHolder.getLocale()));
					}
					if (var.getDecimals() > 16) {
						parsingWarnings.add(messageSource.getMessage("record.decimals.too.long", new Object[] { var.getPosition(), var.getName(), 16 },
						    LocaleContextHolder.getLocale()));
					}
					for (SPSSValueLabelDTO val : var.getValues()) {
						validateValueorMissingField(parsingErrors, onlyValidation, var, val, false, 0, parsingWarnings);
					}
					SPSSMissing missFormat = var.getMissingFormat();
					if (!missFormat.equals(SPSSMissing.SPSS_UNKNOWN) && !missFormat.equals(SPSSMissing.SPSS_NO_MISSVAL)) {
						validateValueorMissingField(parsingErrors, onlyValidation, var, null, true, 1, parsingWarnings);
						if (missFormat.equals(SPSSMissing.SPSS_TWO_MISSVAL) || missFormat.equals(SPSSMissing.SPSS_MISS_RANGE)
						    || missFormat.equals(SPSSMissing.SPSS_THREE_MISSVAL) || missFormat.equals(SPSSMissing.SPSS_MISS_RANGEANDVAL)) {
							validateValueorMissingField(parsingErrors, onlyValidation, var, null, true, 2, parsingWarnings);
							if (missFormat.equals(SPSSMissing.SPSS_THREE_MISSVAL) || missFormat.equals(SPSSMissing.SPSS_MISS_RANGEANDVAL)) {
								validateValueorMissingField(parsingErrors, onlyValidation, var, null, true, 3, parsingWarnings);
							} else {
								if (!onlyValidation) {
									var.setMissingVal3(null);
								}
							}
						} else {
							if (!onlyValidation) {
								var.setMissingVal2(null);
								var.setMissingVal3(null);
							}
						}
					} else {
						if (!onlyValidation) {
							var.setMissingVal1(null);
							var.setMissingVal2(null);
							var.setMissingVal3(null);
						}
					}
				});
		}
		if (parsingErrors.size() > 0) {
			throw new DataWizSystemException(
			    "Error(s) during Record Validation in function (RecordService.validateAndPrepareCodebookForm) - List of errors returned to View",
			    DataWizErrorCodes.RECORD_VALIDATION_ERROR);
		}
	}

	/**
	 * @param parsingErrors
	 * @param onlyValidation
	 * @param var
	 * @param date
	 * @param varError
	 * @param val
	 * @param value
	 * @param label
	 * @return
	 */
	private boolean validateValueorMissingField(final Set<String> parsingErrors, final boolean onlyValidation, SPSSVarDTO var, SPSSValueLabelDTO val,
	    boolean switchToMissing, int missingNum, final Set<String> parsingWarnings) {
		boolean date = (var.getType().equals(SPSSVarTypes.SPSS_FMT_DATE_TIME) || var.getType().equals(SPSSVarTypes.SPSS_FMT_DATE)
		    || var.getType().equals(SPSSVarTypes.SPSS_FMT_ADATE) || var.getType().equals(SPSSVarTypes.SPSS_FMT_JDATE)
		    || var.getType().equals(SPSSVarTypes.SPSS_FMT_EDATE) || var.getType().equals(SPSSVarTypes.SPSS_FMT_SDATE)) ? true : false;
		String parsed;
		String value = null;
		String label = null;
		boolean varError = false;
		if (!switchToMissing && val != null) {
			value = val.getValue();
			label = val.getLabel();
		} else {
			switch (missingNum) {
			case 1:
				value = var.getMissingVal1();
				break;
			case 2:
				value = var.getMissingVal2();
				break;
			case 3:
				value = var.getMissingVal3();
				break;
			}
		}
		if (!switchToMissing && (label.isEmpty() || value.isEmpty())) {
			if (label.isEmpty()) {
				parsingWarnings.add(
				    messageSource.getMessage("record.value.label.empty", new Object[] { var.getName(), var.getPosition() }, LocaleContextHolder.getLocale()));
			} else
				parsingErrors.add(
				    messageSource.getMessage("record.value.label.empty", new Object[] { var.getName(), var.getPosition() }, LocaleContextHolder.getLocale()));
			varError = true;
		} else if (switchToMissing && (value == null || value.isEmpty())) {
			parsingErrors.add(messageSource.getMessage("record.value.missing.empty", new Object[] { var.getName(), missingNum, var.getMissingFormat() },
			    LocaleContextHolder.getLocale()));
			varError = true;
		}
		if (!varError && date && (parsed = parseDateToDBTime(value, var.getType(), parsingErrors, var, "value-label", false)) != null) {
			if (!onlyValidation && !switchToMissing)
				val.setValue(parsed);
			else if (!onlyValidation && switchToMissing) {
				switch (missingNum) {
				case 1:
					var.setMissingVal1(parsed);
					break;
				case 2:
					var.setMissingVal2(parsed);
					break;
				case 3:
					var.setMissingVal3(parsed);
					break;
				}
			}
		} else if (!varError && RecordDTO.simplifyVarTypes(var.getType()).equals(SPSSVarTypes.SPSS_FMT_F)) {
			String parsingErrorString = checkNumberFormat(var, value);
			if (parsingErrorString != null)
				addParsingError(parsingErrors, var, value, parsingErrorString, switchToMissing);
		} else if (!varError && !date) {
			switch (var.getType()) {
			case SPSS_FMT_MONTH:
				try {
					SPSSMonths.valueOf(value);
				} catch (Exception e) {
					addParsingError(parsingErrors, var, value, "record.parse.month.invalid", switchToMissing);
					varError = true;
				}
				break;
			case SPSS_FMT_QYR:
				if (!Pattern.compile(RegexUtil.QUATER_REGEX).matcher(value).find()) {
					addParsingError(parsingErrors, var, value, "record.parse.quater.invalid", switchToMissing);
					varError = true;
				}
				break;
			case SPSS_FMT_MOYR:
				if (!Pattern.compile(RegexUtil.MOYR_REGEX).matcher(value).find()) {
					addParsingError(parsingErrors, var, value, "record.parse.moyr.invalid", switchToMissing);
					varError = true;
				}
				break;
			case SPSS_FMT_WKYR:
				if (!Pattern.compile(RegexUtil.WKYR_REGEX).matcher(value).find()) {
					addParsingError(parsingErrors, var, value, "record.parse.wkyr.invalid", switchToMissing);
					varError = true;
				}
				break;
			case SPSS_FMT_DTIME:
				if (!Pattern.compile(RegexUtil.DTIME_REGEX).matcher(value).find()) {
					addParsingError(parsingErrors, var, value, "record.parse.dtime.invalid", switchToMissing);
					varError = true;
				}
				break;
			case SPSS_FMT_WKDAY:
				if (!Pattern.compile(RegexUtil.WKDAY_REGEX).matcher(value).find()) {
					addParsingError(parsingErrors, var, value, "record.parse.dtime.invalid", switchToMissing);
					varError = true;
				}
				break;
			default:
				if (value.length() > var.getWidth()) {
					addParsingError(parsingErrors, var, value, "record.val.invalid.length", switchToMissing);
					varError = true;
				}
				break;
			}
		}
		return varError;
	}

	/**
	 * @param parsingErrors
	 * @param var
	 * @param val
	 * @param parsingErrorString
	 */
	private void addParsingError(final Set<String> parsingErrors, SPSSVarDTO var, String value, String parsingErrorString, boolean switchtoMissing) {
		parsingErrors.add(messageSource.getMessage(parsingErrorString,
		    new Object[] { value,
		        messageSource.getMessage(switchtoMissing ? "dataset.import.report.codebook.missings" : "dataset.import.report.codebook.values", null,
		            LocaleContextHolder.getLocale()),
		        var.getName(), var.getDecimals(), var.getWidth(), var.getPosition() },
		    LocaleContextHolder.getLocale()));
	}

	/**
	 * @param parsingErrors
	 * @param var
	 * @param val
	 */
	private String checkNumberFormat(SPSSVarDTO var, String numberString) {
		String ret = null;
		try {
			String[] arr = numberString.split("\\.");
			if (var.getDecimals() == 0) {
				if (arr.length > 1)
					ret = "record.value.invalid.dec";
				else
					Integer.parseInt(numberString);
			} else {
				Double.parseDouble(numberString);
				if (arr.length > 1 && arr[1].length() > var.getDecimals()) {
					ret = "record.value.invalid.dec";
				}
			}
			if (numberString.length() > var.getWidth()) {
				ret = "record.val.invalid.length";
			}
		} catch (Exception e) {
			log.debug("Error Parsing {} to Number. Exception: {}", () -> numberString, () -> e);
			ret = "record.parse.number.invalid";
		}
		return ret;
	}

	/**
	 * @param versionId
	 * @param recordId
	 * @param exportType
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public RecordDTO loadRecordExportData(long versionId, long recordId, String exportType, StringBuilder res) throws Exception {
		RecordDTO record;
		List<String> parsingErrors = new ArrayList<>();
		record = recordDAO.findRecordWithID(recordId, versionId);
		record.setVariables(recordDAO.findVariablesByVersionID(versionId));
		record.setErrors(null);
		record.setAttributes(recordDAO.findRecordAttributes(versionId, true));
		setCodebook(parsingErrors, record, exportType.equals("SPSS") ? true : false);
		record.setDataMatrixJson(recordDAO.findMatrixByVersionId(versionId));
		if (record.getDataMatrixJson() != null && !record.getDataMatrixJson().isEmpty()) {
			record.setDataMatrix(new Gson().fromJson(record.getDataMatrixJson(), new TypeToken<List<List<Object>>>() {
			}.getType()));
			record.setDataMatrixJson(null);
		}
		setDataMatrix(parsingErrors, record, exportType.equals("SPSS") ? true : false);
		if (!parsingErrors.isEmpty()) {
			parsingErrors.forEach(s -> res.append(s + "<br />"));
		}
		return record;
	}

	/**
	 * Saves the Record to the DB and the uploaded file to the Minio System. Transaction Manager is used, to eventually rollback the transmission if an
	 * error occurs.
	 * 
	 * @param sForm
	 *          StudyForm, which contains the Record
	 * @throws Exception
	 * 
	 */
	public void saveRecordToDBAndMinio(StudyForm sForm) throws Exception {
		RecordDTO spssFile = sForm.getRecord();
		FileDTO file = sForm.getFile();
		MinioResult res;
		if (spssFile != null && file != null) {
			TransactionStatus status = txManager.getTransaction(new DefaultTransactionDefinition());
			try {
				recordDAO.insertCodeBookMetaData(spssFile);
				removeUselessFileAttributes(spssFile.getAttributes());
				recordDAO.insertAttributes(spssFile.getAttributes(), spssFile.getVersionId(), 0);
				if (spssFile.getVersionId() > 0) {
					recordDAO.insertMatrix(spssFile);
					int position = 0;
					for (SPSSVarDTO var : spssFile.getVariables()) {
						if (var.getColumns() == 0)
							var.setColumns(var.getColumns() < 8 ? 8 : var.getColumns());
						if (!sForm.getCompList().get(position).getVarStatus().equals(VariableStatus.EQUAL)
						    && !sForm.getCompList().get(position).getVarStatus().equals(VariableStatus.EQUAL_CSV)
						    && !sForm.getCompList().get(position).getVarStatus().equals(VariableStatus.MOVED)
						    && !sForm.getCompList().get(position).getVarStatus().equals(VariableStatus.MOVED_CSV)) {
							long varId = recordDAO.insertVariable(var);
							recordDAO.insertVariableVersionRelation(varId, spssFile.getVersionId(), var.getPosition(),
							    sForm.getCompList().get(position).getMessage());
							if (var.getAttributes() != null) {
								recordDAO.insertAttributes(var.getAttributes(), spssFile.getVersionId(), varId);
							}
							if (var.getValues() != null)
								recordDAO.insertVarLabels(var.getValues(), varId);
						} else {
							recordDAO.insertVariableVersionRelation(var.getId(), spssFile.getVersionId(), var.getPosition(),
							    sForm.getCompList().get(position).getMessage());
						}
						position++;
					}
					file.setVersion(spssFile.getVersionId());
					res = minioUtil.putFile(file);
					if (res.equals(MinioResult.OK)) {
						fileDAO.saveFile(file);
					} else if (res.equals(MinioResult.CONNECTION_ERROR)) {
						log.error("FATAL: No Connection to Minio Server - please check Settings or Server");
						throw new DataWizSystemException("Minio returns an error MinioResult: " + res.name(), DataWizErrorCodes.MINIO_SAVE_ERROR);
					}
				}
				txManager.commit(status);
			} catch (Exception e) {
				txManager.rollback(status);
				if (file.getFilePath() != null && !file.getFilePath().isEmpty())
					minioUtil.deleteFile(file);
				throw (e);
			}
		} else {
			throw new DataWizSystemException("saveRecordToDBAndMinio canceled: sForm.getRecord(), or sForm.getFile() is empty  ",
			    DataWizErrorCodes.NO_DATA_ERROR);
		}
	}

	/**
	 * This function removes all useless file attributes (not variable attributes). After that, the list only contains the user specific attributes.
	 * 
	 * @param attr
	 *          List of file attributes
	 */
	private void removeUselessFileAttributes(List<SPSSValueLabelDTO> attr) {
		if (attr == null) {
			attr = new ArrayList<SPSSValueLabelDTO>();
		}
		Iterator<SPSSValueLabelDTO> itt = attr.iterator();
		while (itt.hasNext()) {
			SPSSValueLabelDTO attribute = itt.next();
			if (!attribute.getValue().startsWith("@") || attribute.getValue().equals("@dw_construct") || attribute.getValue().equals("@dw_measocc")
			    || attribute.getValue().equals("@dw_instrument") || attribute.getValue().equals("@dw_itemtext")
			    || attribute.getValue().equals("@dw_filtervar")) {
				itt.remove();
			}
		}
	}

	/**
	 * @param sForm
	 */
	public void sortVariablesAndSetMetaData(StudyForm sForm) throws DataWizSystemException {
		List<RecordCompareDTO> compList = sForm.getCompList();
		List<SPSSVarDTO> newVars = sForm.getRecord().getVariables();
		List<SPSSVarDTO> prevVars = sForm.getPreviousRecordVersion().getVariables();
		Boolean CSV = sForm.getSelectedFileType() == null ? false : sForm.getSelectedFileType().equals("CSV") ? true : false;
		if (compList != null) {
			int position = 0;
			for (RecordCompareDTO comp : compList) {
				SPSSVarDTO newVar = null, prevVar = null;
				if (comp.getVarStatus().equals(VariableStatus.EQUAL) || comp.getVarStatus().equals(VariableStatus.EQUAL_CSV)) {
					newVars.get(position).setId(prevVars.get(position).getId());
				} else if (comp.getVarStatus().equals(VariableStatus.MOVED) || comp.getVarStatus().equals(VariableStatus.MOVED_CSV)
				    || comp.getVarStatus().equals(VariableStatus.MOVED)) {
					newVars.get(position).setId(prevVars.get(comp.getMovedFrom() - 1).getId());
				} else if (comp.getVarStatus().equals(VariableStatus.MOVED_AND_META_CHANGED)
				    || comp.getVarStatus().equals(VariableStatus.MOVED_AND_META_CHANGED_CSV)
				    || comp.getVarStatus().equals(VariableStatus.MOVED_AND_TYPE_CHANGED)) {
					newVar = newVars.get(position);
					prevVar = prevVars.get(comp.getMovedFrom() - 1);
				} else {
					newVar = newVars.get(position);
					prevVar = position < prevVars.size() ? prevVars.get(position) : null;
				}
				if (comp.isKeepExpMeta() && newVar != null && prevVar != null) {
					if (CSV) {
						newVar.setLabel(prevVar.getLabel());
						newVar.setMissingFormat(prevVar.getMissingFormat());
						newVar.setMissingVal1(prevVar.getMissingVal1());
						newVar.setMissingVal2(prevVar.getMissingVal2());
						newVar.setMissingVal3(prevVar.getMissingVal3());
						newVar.setColumns(prevVar.getColumns());
						newVar.setAligment(prevVar.getAligment());
						newVar.setMeasureLevel(prevVar.getMeasureLevel());
						newVar.setRole(prevVar.getRole());
						newVar.setNumOfAttributes(prevVar.getNumOfAttributes());
						newVar.setValues(prevVar.getValues());
						newVar.setAttributes(prevVar.getAttributes());
					}
					if (prevVar.getDw_attributes() != null)
						removeEmptyDWAttributes(prevVar.getDw_attributes());
					newVar.getAttributes().addAll(prevVar.getDw_attributes());
					if (newVar.getType().equals(SPSSVarTypes.SPSS_FMT_A) && !prevVar.getType().equals(SPSSVarTypes.SPSS_FMT_A)) {
						newVar.setMeasureLevel(SPSSMeasLevel.SPSS_MLVL_UNK);
					}
				} else if (!comp.isKeepExpMeta() && !CSV && newVar != null) {
					if (newVar.getDw_attributes() != null)
						removeEmptyDWAttributes(newVar.getDw_attributes());
					if (newVar.getAttributes() == null)
						newVar.setAttributes(new LinkedList<>());
					newVar.getAttributes().addAll(newVar.getDw_attributes());
				}
				position++;
			}
		} else {
			// TODO throw no data exception
		}
	}

	public void removeEmptyDWAttributes(List<SPSSValueLabelDTO> attributes) {
		Iterator<SPSSValueLabelDTO> itt = attributes.iterator();
		while (itt.hasNext()) {
			SPSSValueLabelDTO att = itt.next();
			if (att.getValue() == null || att.getValue().trim().isEmpty())
				itt.remove();
		}
	}

	/**
	 * This function copies the needed missing types from the temporary SPSSvarDTO varval (used for the missing modal), to the SPSSVarVar of the current
	 * edited record. The needed missing values are selected by the missing type.
	 * 
	 * @param varVal
	 *          Temporary SPSSVarDTO of the missing modal form
	 * @param var
	 *          SPSSVarDTO of the sForm record
	 */
	public void switchMissingType(SPSSVarDTO varVal, SPSSVarDTO var) {
		switch (varVal.getMissingFormat()) {
		case SPSS_NO_MISSVAL:
			var.setMissingVal1(null);
			var.setMissingVal2(null);
			var.setMissingVal3(null);
			break;
		case SPSS_ONE_MISSVAL:
			var.setMissingVal1(varVal.getMissingVal1());
			var.setMissingVal2(null);
			var.setMissingVal3(null);
			break;
		case SPSS_TWO_MISSVAL:
			var.setMissingVal1(varVal.getMissingVal1());
			var.setMissingVal2(varVal.getMissingVal2());
			var.setMissingVal3(null);
			break;
		case SPSS_THREE_MISSVAL:
			var.setMissingVal1(varVal.getMissingVal1());
			var.setMissingVal2(varVal.getMissingVal2());
			var.setMissingVal3(varVal.getMissingVal3());
			break;
		case SPSS_MISS_RANGE:
			var.setMissingVal1(varVal.getMissingVal1());
			var.setMissingVal2(varVal.getMissingVal2());
			var.setMissingVal3(null);
			break;
		case SPSS_MISS_RANGEANDVAL:
			var.setMissingVal1(varVal.getMissingVal1());
			var.setMissingVal2(varVal.getMissingVal2());
			var.setMissingVal3(varVal.getMissingVal3());
			break;
		default:
			log.warn("MissingFormat not known - " + varVal.getMissingFormat());
			break;
		}
	}

	/**
	 * @param sForm
	 * @param warnString
	 * @return
	 */
	public String setMessageString(List<String> messages) {
		String messageString = null;
		final AtomicInteger count = new AtomicInteger();
		if (messages != null && messages.size() > 0) {
			final int warningSize = messages.size();
			StringBuilder sb = new StringBuilder();
			messages.forEach(s -> {
				sb.append(s);
				if (count.incrementAndGet() < warningSize)
					sb.append("<br />");
			});
			messageString = sb.toString();
		}
		return messageString;
	}

	public String setMessageString(Set<String> messages) {
		if (messages != null)
			return setMessageString(messages.parallelStream().collect(Collectors.toList()));
		else
			return null;
	}

}
