package de.zpid.datawiz.service;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.itextpdf.io.source.ByteArrayOutputStream;

import de.zpid.datawiz.dao.ContributorDAO;
import de.zpid.datawiz.dao.DmpDAO;
import de.zpid.datawiz.dao.FileDAO;
import de.zpid.datawiz.dao.FormTypesDAO;
import de.zpid.datawiz.dao.ProjectDAO;
import de.zpid.datawiz.dao.RecordDAO;
import de.zpid.datawiz.dao.StudyDAO;
import de.zpid.datawiz.dao.UserDAO;
import de.zpid.datawiz.dto.ContributorDTO;
import de.zpid.datawiz.dto.DmpDTO;
import de.zpid.datawiz.dto.ExportRecordDTO;
import de.zpid.datawiz.dto.ExportStudyDTO;
import de.zpid.datawiz.dto.FileDTO;
import de.zpid.datawiz.dto.ProjectDTO;
import de.zpid.datawiz.dto.RecordDTO;
import de.zpid.datawiz.dto.StudyDTO;
import de.zpid.datawiz.dto.UserDTO;
import de.zpid.datawiz.enumeration.DWFieldTypes;
import de.zpid.datawiz.enumeration.DataWizErrorCodes;
import de.zpid.datawiz.enumeration.MinioResult;
import de.zpid.datawiz.enumeration.Roles;
import de.zpid.datawiz.exceptions.DataWizSystemException;
import de.zpid.datawiz.form.ExportProjectForm;
import de.zpid.datawiz.form.ProjectForm;
import de.zpid.datawiz.form.StudyForm;
import de.zpid.datawiz.util.ConsistencyCheckUtil;
import de.zpid.datawiz.util.DDIUtil;
import de.zpid.datawiz.util.FileUtil;
import de.zpid.datawiz.util.ITextUtil;
import de.zpid.datawiz.util.MinioUtil;
import de.zpid.datawiz.util.ObjectCloner;
import de.zpid.spss.SPSSIO;
import de.zpid.spss.dto.SPSSErrorDTO;
import de.zpid.spss.dto.SPSSValueLabelDTO;
import de.zpid.spss.dto.SPSSVarDTO;

@Component
public class ExportService {

	private static Logger log = LogManager.getLogger(ExportService.class);

	private final String FILES_PROJECT_FOLDER = "supplementary_files/";
	private final String STUDY_FOLDER = "studies/";
	private final String RECORD_FOLDER = "records/";
	private final String PROJECT_FILE_NAME = "project_long_term.xml";
	private final String STUDY_FILE_NAME = "study_long_term.xml";
	private final String RECORD_FILE_NAME = "record_long_term.xml";
	private final String RECORD_MATRIX_NAME = "record_matrix.csv";

	@Autowired
	protected MessageSource messageSource;
	@Autowired
	private ClassPathXmlApplicationContext applicationContext;
	@Autowired
	private SPSSIO spss;
	@Autowired
	private FileUtil fileUtil;
	@Autowired
	private ITextUtil itextUtil;
	@Autowired
	private MinioUtil minioUtil;
	@Autowired
	private ProjectDAO projectDAO;
	@Autowired
	private DmpDAO dmpDAO;
	@Autowired
	private FormTypesDAO formTypeDAO;
	@Autowired
	private FileDAO fileDAO;
	@Autowired
	private DDIUtil ddi;
	@Autowired
	private StudyDAO studyDAO;
	@Autowired
	private ContributorDAO contributorDAO;
	@Autowired
	private RecordDAO recordDAO;
	@Autowired
	private UserDAO userDAO;
	@Autowired
	private RecordService recordService;
	@Autowired
	private StudyService studyService;
	@Autowired
	private ConsistencyCheckUtil ccUtil;

	/**
	 * 
	 * @param pid
	 * @param exportForm
	 * @param user
	 * @return
	 * @throws DataWizSystemException
	 * @throws NoSuchMessageException
	 */
	public ExportProjectForm getExportForm(final Long pid, final UserDTO user) throws Exception {
		ExportProjectForm exportForm = (ExportProjectForm) applicationContext.getBean("ExportProjectForm");
		if (user.hasRole(Roles.ADMIN) || user.hasRole(Roles.PROJECT_ADMIN, pid, false) || user.hasRole(Roles.PROJECT_WRITER, pid, false)
		    || user.hasRole(Roles.PROJECT_READER, pid, false)) {
			try {
				ProjectDTO project = projectDAO.findById(pid);
				if (project != null && project.getId() > 0) {
					exportForm.setProjectTitle(project.getTitle());
					exportForm.setProjectId(project.getId());
					exportForm.setStudies(new ArrayList<>());
					exportForm.setMaterial(fileDAO.findProjectMaterialFiles(project));
					List<StudyDTO> studies = studyDAO.findAllStudiesByProjectId(project);
					if (studies != null) {
						for (StudyDTO study : studies) {
							ExportStudyDTO studExp = (ExportStudyDTO) applicationContext.getBean("ExportStudyDTO");
							studExp.setStudyId(study.getId());
							studExp.setStudyTitle(study.getTitle());
							studExp.setRecords(new ArrayList<>());
							studExp.setMaterial(fileDAO.findStudyMaterialFiles(study));
							List<RecordDTO> records = recordDAO.findRecordsWithStudyID(study.getId());
							if (records != null) {
								for (RecordDTO record : records) {
									ExportRecordDTO recExp = (ExportRecordDTO) applicationContext.getBean("ExportRecordDTO");
									recExp.setRecordId(record.getId());
									recExp.setVersionId(record.getVersionId());
									recExp.setRecordTitle(record.getRecordName());
									studExp.getRecords().add(recExp);
								}
							}
							ccUtil.checkStudyConsistency(studExp, studyDAO.findById(study.getId(), project.getId(), false, false));
							exportForm.getStudies().add(studExp);
						}
					}
					ccUtil.checkDMPConsistency(exportForm, dmpDAO.findByID(project));
				} else {
					throw new DataWizSystemException(messageSource.getMessage("logging.project.not.found", new Object[] { pid }, Locale.ENGLISH),
					    DataWizErrorCodes.PROJECT_NOT_AVAILABLE);
				}
			} catch (Exception e) {
				if (e instanceof DataWizSystemException) {
					log.warn(messageSource.getMessage("logging.project.not.found", new Object[] { pid }, Locale.ENGLISH));
					throw e;
				} else {
					log.warn("DBS Exception during getExportForm", () -> e);
					throw new DataWizSystemException(messageSource.getMessage("logging.database.error", new Object[] { e.getMessage() }, Locale.ENGLISH),
					    DataWizErrorCodes.DATABASE_ERROR);
				}
			}
		} else {
			log.warn(
			    messageSource.getMessage("logging.user.permitted", new Object[] { user.getEmail(), "getExportForm for project", pid }, Locale.ENGLISH));
			throw new DataWizSystemException(
			    messageSource.getMessage("logging.user.permitted", new Object[] { user.getEmail(), "getExportForm for project", pid }, Locale.ENGLISH),
			    DataWizErrorCodes.USER_ACCESS_PROJECT_PERMITTED);
		}
		return exportForm;
	}

	/**
	 * 
	 * @param exportForm
	 * @param pid
	 * @param user
	 * @return
	 * @throws Exception
	 */
	public List<Entry<String, byte[]>> createExportFileList(ExportProjectForm exportForm, final Optional<Long> pid, final UserDTO user)
	    throws Exception {
		List<Entry<String, byte[]>> files = new ArrayList<>();
		ProjectDTO projectDB = projectDAO.findById(pid.get());
		if (projectDB != null) {
			List<FileDTO> pFiles = null;
			List<UserDTO> pUploader = new ArrayList<>();
			ProjectForm pForm = (ProjectForm) applicationContext.getBean("ProjectForm");
			if (exportForm.isExportMetaData() || exportForm.isExportDMP() || exportForm.isExportProjectMaterial()) {
				if (exportForm.isExportMetaData()) {
					pForm.setProject(projectDB);
					pForm.setContributors(contributorDAO.findByProject(projectDB, false, false));
				} else {
					ProjectDTO project = (ProjectDTO) applicationContext.getBean("ProjectDTO");
					project.setId(projectDB.getId());
					project.setTitle(projectDB.getTitle());
					project.setDescription(projectDB.getDescription());
					project.setFunding(projectDB.getFunding());
					pForm.setProject(project);
				}
				if (exportForm.isExportProjectMaterial()) {
					pFiles = fileDAO.findProjectMaterialFiles(projectDB);
					if (pFiles != null && pFiles.size() > 0) {
						setAdditionalFilestoExportList(files, pFiles, FILES_PROJECT_FOLDER);
						pForm.setFiles(pFiles);
						for (FileDTO file : pFiles) {
							pUploader.add(userDAO.findById(file.getUserId()));
						}
					}
				}
				pForm.setPrimaryContributor(contributorDAO.findPrimaryContributorByProject(projectDB));
				if (exportForm.isExportDMP()) {
					DmpDTO dmpDB = dmpDAO.findByID(projectDB);
					if (dmpDB != null && dmpDB.getId() > 0) {
						dmpDB.setUsedDataTypes(formTypeDAO.findSelectedFormTypesByIdAndType(dmpDB.getId(), DWFieldTypes.DATATYPE, false));
						dmpDB.setUsedCollectionModes(formTypeDAO.findSelectedFormTypesByIdAndType(dmpDB.getId(), DWFieldTypes.COLLECTIONMODE, false));
						dmpDB.setSelectedMetaPurposes(formTypeDAO.findSelectedFormTypesByIdAndType(dmpDB.getId(), DWFieldTypes.METAPORPOSE, false));
						pForm.setDataTypes(formTypeDAO.findAllByType(false, DWFieldTypes.DATATYPE));
						pForm.setCollectionModes(formTypeDAO.findAllByType(false, DWFieldTypes.COLLECTIONMODE));
						pForm.setMetaPurposes(formTypeDAO.findAllByType(false, DWFieldTypes.METAPORPOSE));
					}
					pForm.setDmp(dmpDB);
				}
				Document pdoc = ddi.createProjectDocument(pForm, pFiles, pUploader);
				if (pdoc != null)
					files.add(new SimpleEntry<String, byte[]>(PROJECT_FILE_NAME, createByteArrayFromXML(pdoc)));
				else {
					throw new DataWizSystemException(
					    messageSource.getMessage("logging.xml.create.error", new Object[] { "createProjectDocument", exportForm.toString() }, Locale.ENGLISH),
					    DataWizErrorCodes.STUDY_NOT_AVAILABLE);
				}
			}
			// Create Studies
			if (exportForm.getStudies() != null) {
				Set<String> studyNames = new HashSet<>();
				int studyNameDoublette = 0;
				for (ExportStudyDTO studyEx : exportForm.getStudies()) {
					StudyForm sForm = (StudyForm) applicationContext.getBean("StudyForm");
					List<FileDTO> sFiles = null;
					StudyDTO study = null;
					StudyDTO studyDB = studyDAO.findById(studyEx.getStudyId(), exportForm.getProjectId(), false, false);
					List<UserDTO> sUploader = new ArrayList<>();
					if (studyDB != null) {
						if (!studyNames.add(studyDB.getTitle())) {
							studyDB.setTitle(studyDB.getTitle() + "_" + ++studyNameDoublette);
						}
						String studyFolder = STUDY_FOLDER + formatFilename(studyDB.getTitle()) + "/";
						studyService.setStudyDTOExport(studyDB);
						if (studyEx.isExportMetaData() || studyEx.isExportStudyMaterial()) {
							if (studyEx.isExportMetaData()) {
								study = studyDB;
							} else {
								study = (StudyDTO) applicationContext.getBean("StudyDTO");
								study.setId(studyDB.getId());
								study.setTitle(studyDB.getTitle());
							}
							if (studyEx.isExportStudyMaterial()) {
								sFiles = fileDAO.findStudyMaterialFiles(studyDB);
								if (sFiles != null && sFiles.size() > 0) {
									setAdditionalFilestoExportList(files, sFiles, studyFolder + FILES_PROJECT_FOLDER);
									for (FileDTO file : sFiles) {
										sUploader.add(userDAO.findById(file.getUserId()));
									}
								}
							}
							sForm.setProject(projectDB);
							sForm.setStudy(study);
							sForm.setCollectionModes(formTypeDAO.findAllByType(true, DWFieldTypes.COLLECTIONMODE));
							sForm.setSourFormat(formTypeDAO.findAllByType(true, DWFieldTypes.DATAFORMAT));
							Document sdoc = ddi.createStudyDocument(sForm, sFiles, sUploader, user);
							if (sdoc != null) {
								files.add(new SimpleEntry<String, byte[]>(studyFolder + STUDY_FILE_NAME, createByteArrayFromXML(sdoc)));
							} else {
								throw new DataWizSystemException(messageSource.getMessage("logging.xml.create.error",
								    new Object[] { "createProjectDocument", exportForm.toString() }, Locale.ENGLISH), DataWizErrorCodes.STUDY_NOT_AVAILABLE);
							}
						}
						// Create Records
						if (studyEx.getRecords() != null) {
							Set<String> recordNames = new HashSet<>();
							int recNameDoublette = 0;
							for (ExportRecordDTO recordEx : studyEx.getRecords()) {
								if (recordEx.isExportMetaData() || recordEx.isExportCodebook() || recordEx.isExportMatrix()) {
									RecordDTO recordDB = recordDAO.findRecordWithID(recordEx.getRecordId(), recordEx.getVersionId());
									if (recordDB != null) {
										if (!recordNames.add(recordDB.getRecordName())) {
											recordDB.setRecordName(recordDB.getRecordName() + "_" + ++recNameDoublette);
										}
										String recordFolder = studyFolder + RECORD_FOLDER + formatFilename(recordDB.getRecordName()) + "/";
										RecordDTO record = (RecordDTO) applicationContext.getBean("RecordDTO");
										List<String> parsingErrors = new LinkedList<>();
										if (recordEx.isExportMetaData() && recordEx.isExportCodebook()) {
											recordDB.setVariables(recordDAO.findVariablesByVersionID(recordDB.getVersionId()));
											recordService.setCodebook(parsingErrors, recordDB, false);
											record = recordDB;
										} else if (!recordEx.isExportMetaData() && recordEx.isExportCodebook()) {
											record.setRecordName(recordDB.getRecordName());
											record.setVersionId(recordDB.getVersionId());
											record.setId(recordDB.getId());
											record.setStudyId(recordDB.getStudyId());
											record.setVariables(recordDAO.findVariablesByVersionID(recordDB.getVersionId()));
											recordService.setCodebook(parsingErrors, record, false);
										} else if (recordEx.isExportMetaData() && !recordEx.isExportCodebook()) {
											record = recordDB;
										} else {
											record = null;
										}
										if (!parsingErrors.isEmpty()) {
											StringBuilder s = new StringBuilder();
											parsingErrors.forEach(pe -> {
												s.append(pe + " ; ");
											});
											throw new DataWizSystemException(messageSource.getMessage("logging.record.parsing.error",
											    new Object[] { recordEx.getRecordId(), s.toString() }, Locale.ENGLISH), DataWizErrorCodes.RECORD_PARSING_ERROR);
										}
										StringBuilder res = new StringBuilder();
										FileDTO fileHash = null;
										byte[] content = null;
										if (recordEx.isExportMatrix()) {
											RecordDTO copy = (RecordDTO) ObjectCloner.deepCopy(recordDB);
											copy.setVariables(recordDAO.findVariablesByVersionID(copy.getVersionId()));
											recordService.setDataMatrix(parsingErrors, copy, false);
											if (!parsingErrors.isEmpty()) {
												StringBuilder s = new StringBuilder();
												parsingErrors.forEach(pe -> {
													s.append(pe + " ; ");
												});
												throw new DataWizSystemException(messageSource.getMessage("logging.record.parsing.error",
												    new Object[] { recordEx.getRecordId(), s.toString() }, Locale.ENGLISH), DataWizErrorCodes.RECORD_PARSING_ERROR);
											}
											if (copy.getDataMatrix() != null && !copy.getDataMatrix().isEmpty())
												content = exportCSV(copy, res, true);
											if (content != null && res.toString().trim().isEmpty()) {
												fileHash = (FileDTO) applicationContext.getBean("FileDTO");
												fileHash.setMd5checksum(fileUtil.getFileChecksum("MD5", content));
												fileHash.setSha1Checksum(fileUtil.getFileChecksum("SHA-1", content));
												fileHash.setSha256Checksum(fileUtil.getFileChecksum("SHA-256", content));
												files.add(new SimpleEntry<String, byte[]>(recordFolder + RECORD_MATRIX_NAME, content));
											} else if (!res.toString().trim().isEmpty()) {
												throw new DataWizSystemException(messageSource.getMessage("logging.record.parsing.error",
												    new Object[] { recordEx.getRecordId(), messageSource.getMessage(res.toString(), null, Locale.ENGLISH) }, Locale.ENGLISH),
												    DataWizErrorCodes.RECORD_PARSING_ERROR);
											}
										}
										if (record != null) {
											sForm.setRecord(record);
											sForm.setStudy(studyDB);
											Document rdoc = ddi.createRecordDocument(sForm, fileHash, user);
											if (rdoc != null) {
												files.add(new SimpleEntry<String, byte[]>(recordFolder + RECORD_FILE_NAME, createByteArrayFromXML(rdoc)));
											} else {
												throw new DataWizSystemException(messageSource.getMessage("logging.xml.create.error",
												    new Object[] { "createProjectDocument", exportForm.toString() }, LocaleContextHolder.getLocale()),
												    DataWizErrorCodes.STUDY_NOT_AVAILABLE);
											}
										}
									}
								}
							}
						}
					} else {
						throw new DataWizSystemException(
						    messageSource.getMessage("logging.study.not.found", new Object[] { studyEx.getStudyId() }, Locale.ENGLISH),
						    DataWizErrorCodes.STUDY_NOT_AVAILABLE);
					}
				}
			}
		} else {
			throw new DataWizSystemException(
			    messageSource.getMessage("logging.project.not.found", new Object[] { exportForm.getProjectId() }, Locale.ENGLISH),
			    DataWizErrorCodes.PROJECT_NOT_AVAILABLE);
		}
		return files;
	}

	/**
	 * @param exportType
	 * @param attachments
	 * @param record
	 * @param res
	 * @param content
	 * @return
	 * @throws Exception
	 */
	public byte[] getRecordExportContentAsByteArray(final Long pid, final String exportType, final Boolean attachments, final RecordDTO record,
	    final StringBuilder res) throws Exception {
		byte[] content = null;
		if (exportType.equals("CSVMatrix")) {
			content = exportCSV(record, res, true);
		} else if (exportType.equals("CSVCodebook")) {
			content = exportCSV(record, res, false);
		} else if (exportType.equals("JSON")) {
			content = exportJSON(record, res);
		} else if (exportType.equals("SPSS")) {
			content = exportSPSSFile(record, res);
		} else if (exportType.equals("PDF")) {
			ProjectDTO project = projectDAO.findById(pid);
			ContributorDTO primaryContri = null;
			if (project != null)
				primaryContri = contributorDAO.findPrimaryContributorByProject(project);
			StudyDTO study = studyDAO.findById(record.getStudyId(), pid.longValue(), true, false);
			if (study != null)
				study.setContributors(contributorDAO.findByStudy(study.getId()));
			content = itextUtil.createRecordCodeBookPDFA(record, study, project, primaryContri, false, attachments);
		} else if (exportType.equals("CSVZIP")) {
			List<Entry<String, byte[]>> files = new ArrayList<>();
			files.add(new SimpleEntry<String, byte[]>(record.getRecordName() + "_Matrix.csv", exportCSV(record, res, true)));
			files.add(new SimpleEntry<String, byte[]>(record.getRecordName() + "_Codebook.csv", exportCSV(record, res, false)));
			content = exportZip(files, res);
		}
		return content;
	}

	public byte[] exportZip(List<Entry<String, byte[]>> files, StringBuilder res) {
		log.trace("Entering exportZip for Num of File: [{}]", () -> files.size());
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ZipOutputStream zos = new ZipOutputStream(baos);
		String fileName = null;
		byte[] content = null;
		try {
			for (Entry<String, byte[]> file : files) {
				fileName = file.getKey();
				ZipEntry entry = new ZipEntry(file.getKey());
				entry.setSize(file.getValue().length);
				zos.putNextEntry(entry);
				zos.write(file.getValue());
				zos.closeEntry();
			}
			zos.close();
			content = baos.toByteArray();
			baos.close();
		} catch (Exception e) {
			log.warn("Error during exportZip: Filename: [{}] Exception: {}", fileName, e);
			res.insert(0, "export.error.exception.thown");
		}
		log.debug("Leaving exportZip with result [{}]", res.toString().trim().isEmpty() ? "OK" : res.toString());
		return content;
	}

	/**
	 * This function prepares the record DTO for the export and transfers it to the SPSS IO Module. If the return of the SPSS IO Module is valid, a
	 * byte[] which contains the spss file is returned. If some errors occur diring this process, null is returned and an error code is written to
	 * ResponseEntity<String> resp.
	 * 
	 * @param record
	 *          Complete copy of the requested record version
	 * @param resp
	 *          Reference parameter to handle export errors in the calling function
	 * @return the spss file as byte array
	 */
	public byte[] exportSPSSFile(final RecordDTO record, final StringBuilder res) {
		byte[] content = null;
		if (record != null) {
			log.trace("Entering exportSPSSFile for RecordDTO: [id: {}; version:{}]", () -> record.getId(), () -> record.getVersionId());
			record.setFileIdString(record.getRecordName() + "_Version_(" + record.getVersionId() + ")");
			record.setErrors(new LinkedList<>());
			String dir = fileUtil.setFolderPath("temp");
			String filename = UUID.randomUUID().toString() + ".sav";
			if (record.getAttributes() == null) {
				record.setAttributes(new LinkedList<>());
			}
			record.getAttributes().add(new SPSSValueLabelDTO("@dw_construct", "@dw_construct"));
			record.getAttributes().add(new SPSSValueLabelDTO("@dw_measocc", "@dw_measocc"));
			record.getAttributes().add(new SPSSValueLabelDTO("@dw_instrument", "@dw_instrument"));
			record.getAttributes().add(new SPSSValueLabelDTO("@dw_itemtext", "@dw_itemtext"));
			record.getAttributes().add(new SPSSValueLabelDTO("@dw_filtervar", "@dw_filtervar"));
			record.getAttributes().add(new SPSSValueLabelDTO("CreatedBy", "DataWiz"));
			record.getAttributes().add(new SPSSValueLabelDTO("DataWizStudyId", String.valueOf(record.getStudyId())));
			record.getAttributes().add(new SPSSValueLabelDTO("DataWizRecordId", String.valueOf(record.getId())));
			record.getAttributes().add(new SPSSValueLabelDTO("DataWizVersionId", String.valueOf(record.getVersionId())));
			record.getAttributes().add(new SPSSValueLabelDTO("DataWizLastUpdateAt", String.valueOf(record.getChanged())));
			record.getAttributes().add(new SPSSValueLabelDTO("DataWizLastUpdateBy", record.getChangedBy()));
			// record.getAttributes().add(new SPSSValueLabelDTO("DataWizLastUpdateLog", record.getChangeLog()));
			// record.getAttributes().add(new SPSSValueLabelDTO("DataWizLRecordDescription", record.getDescription()));
			if (record != null && record.getVariables() != null && record.getDataMatrix() != null) {
				try {
					record.getVariables().parallelStream().forEach(var -> {
						var.setDecimals(var.getDecimals() > 16 ? 16 : var.getDecimals());
					});
					if (!Files.exists(Paths.get(dir)))
						Files.createDirectories(Paths.get(dir));
					spss.writeSPSSFile(record, dir + filename);
					List<SPSSErrorDTO> errors = record.getErrors();
					if (errors.size() > 0) {
						for (SPSSErrorDTO error : errors)
							if (error.getError().getNumber() > 0) {
								res.insert(0, "export.error.spss.error");
								break;
							}
					}
					if (Files.exists(Paths.get(dir + filename))) {
						content = Files.readAllBytes(Paths.get(dir + filename));
					} else {
						if (res.length() == 0)
							res.insert(0, "export.error.file.not.exist");
					}
				} catch (Exception e) {
					log.warn("Error during SPSS export: RecordDTO: [id: {}; version:{}] Error: {}; Exception: {}", () -> record.getId(),
					    () -> record.getVersionId(), () -> res.toString(), () -> e);
					e.printStackTrace();
					res.insert(0, "export.error.exception.thown");
				} finally {
					if (Files.exists(Paths.get(dir + filename)))
						fileUtil.deleteFile(Paths.get(dir + filename));
				}
			} else {
				res.insert(0, "export.recorddto.empty");
			}
			log.debug("Leaving exportSPSSFile with result [{}]", res.toString().trim().isEmpty() ? "OK" : res.toString());
		} else {
			res.insert(0, "export.recorddto.empty");
		}
		return content;
	}

	/**
	 * This function transfers the record into a CSV String and returns it as byte array. It handles the CSV export for the Caodebook and the matrix
	 * (boolean matrix = true).If some errors occur diring this process, null is returned and an error code is written to ResponseEntity<String> resp.
	 * 
	 * @param record
	 *          Complete copy of the requested record version
	 * @param resp
	 *          Reference parameter to handle export errors in the calling function
	 * @return the csv string as byte array
	 */
	public byte[] exportCSV(final RecordDTO record, StringBuilder res, final boolean matrix) {
		log.trace("Entering exportCSV for RecordDTO: [id: {}; version:{}] matrix[{}]", () -> record.getId(), () -> record.getVersionId(), () -> matrix);
		byte[] content = null;
		if (record != null && record.getVariables() != null && !record.getVariables().isEmpty() && record.getDataMatrix() != null) {
			try {
				StringBuilder csv;
				if (matrix)
					csv = recordMatrixToCSVString(record);
				else
					csv = recordCodebookToCSVString(record);
				if (csv != null && csv.length() > 0) {
					content = csv.toString().getBytes(Charset.forName("UTF-8"));
				} else {
					res.insert(0, "export.csv.string.empty");
				}
			} catch (Exception e) {
				log.warn("Error during CSV export: RecordDTO: [id: {}; version:{} matrix[{}]] Exception: {}", () -> record.getId(),
				    () -> record.getVersionId(), () -> matrix, () -> e);
				res.insert(0, "export.error.exception.thown");
			}
		} else {
			res.insert(0, "export.recorddto.empty");
		}
		log.debug("Leaving exportCSV with result [{}]", res.toString().trim().isEmpty() ? "OK" : res.toString());
		return content;
	}

	/**
	 * This function transfers the record into a JSON String and returns it as byte array. If some errors occur diring this process, null is returned
	 * and an error code is written to ResponseEntity resp.
	 * 
	 * @param record
	 *          Complete copy of the requested record version
	 * @param resp
	 *          Reference parameter to handle export errors in the calling function
	 * @return the csv string as byte array
	 */
	public byte[] exportJSON(RecordDTO record, StringBuilder res) {
		log.trace("Entering exportJSON for RecordDTO: [id: {}; version:{}]", () -> record.getId(), () -> record.getVersionId());
		byte[] content = null;
		if (record != null && record.getVariables() != null && record.getDataMatrix() != null) {
			try {
				Gson gson = new GsonBuilder().setPrettyPrinting().create();
				String json = gson.toJson(record);
				if (json != null && json.length() > 0) {
					content = json.getBytes(Charset.forName("UTF-8"));
				} else {
					res.insert(0, "export.json.string.empty");
				}
			} catch (Exception e) {
				log.warn("Error during JSON export: RecordDTO: [id: {}; version:{}] Exception: ", () -> record.getId(), () -> record.getVersionId(), () -> e);
				res.insert(0, "export.error.exception.thown");
			}
		} else {
			res.insert(0, "export.recorddto.empty");
		}
		log.debug("Leaving exportJSON with result [{}]", res.toString().trim().isEmpty() ? "OK" : res.toString());
		return content;
	}

	/**
	 * This function creates the CSV matrix (with variable names as headline) from the data matrix list of the record
	 * 
	 * @param record
	 *          Complete copy of the requested record version
	 * @return StringBuilder which contains the Matrix as CSV
	 */
	public StringBuilder recordMatrixToCSVString(RecordDTO record) {
		StringBuilder csv = new StringBuilder();
		int vars = record.getVariables().size();
		int varcount = 1;
		for (SPSSVarDTO var : record.getVariables()) {
			csv.append(var.getName());
			if (vars > varcount)
				csv.append(",");
			else if (vars == varcount) {
				csv.append("\n");
			}
			varcount++;
		}
		for (List<Object> row : record.getDataMatrix()) {
			if (row.size() != vars) {
				csv = null;
				break;
			}
			varcount = 1;
			for (Object obj : row) {
				if (obj == null) {
					csv.append("");
				} else if (obj instanceof Number)
					csv.append(obj);
				else
					csv.append("\"" + ((String) obj).replaceAll("\"", "\'") + "\"");
				if (vars > varcount++)
					csv.append(",");
			}
			csv.append("\n");
		}
		return csv;
	}

	/**
	 * This function creates the complete Codebook and returns it as StringBuilder Object.
	 * 
	 * @param record
	 *          Complete copy of the requested record version
	 * @return StringBuilder which contains the Codebook as CSV
	 */
	public StringBuilder recordCodebookToCSVString(RecordDTO record) {
		StringBuilder csv = new StringBuilder();
		csv.append(
		    "name,type,label,values,missingformat,missingValue1,missingValue2,missingValue3,width,decimals,measurelevel,role,attributes,dw_konstruct,dw_measocc,dw_instrument,dw_itemtext,dw_filtervariable\n");
		for (SPSSVarDTO var : record.getVariables()) {
			csv.append("\"" + cleanString(var.getName()) + "\"");
			csv.append(",");
			if (var.getType() != null)
				csv.append("\"" + messageSource.getMessage("spss.type." + var.getType(), null, Locale.ENGLISH) + "\"");
			else
				csv.append("\"" + messageSource.getMessage("spss.type.SPSS_UNKNOWN", null, Locale.ENGLISH) + "\"");
			csv.append(",");
			if (var.getLabel() != null && !var.getLabel().isEmpty())
				csv.append("\"" + cleanString(var.getLabel()) + "\"");
			csv.append(",");
			if (var.getValues() != null && var.getValues().size() > 0) {
				csv.append("\"");
				for (SPSSValueLabelDTO val : var.getValues()) {
					csv.append("{");
					csv.append(cleanString(val.getValue()));
					csv.append("=");
					csv.append(cleanString(val.getLabel()));
					csv.append("}");
				}
				csv.append("\"");
			}
			csv.append(",");
			csv.append("\"" + messageSource.getMessage("spss.missings." + var.getMissingFormat(), null, Locale.ENGLISH) + "\"");
			csv.append(",");
			switch (var.getMissingFormat()) {
			case SPSS_NO_MISSVAL:
				csv.append(",");
				csv.append(",");
				break;
			case SPSS_ONE_MISSVAL:
				csv.append("\"" + var.getMissingVal1() + "\"");
				csv.append(",");
				csv.append(",");
				break;
			case SPSS_TWO_MISSVAL:
				csv.append("\"" + var.getMissingVal1() + "\"");
				csv.append(",");
				csv.append("\"" + var.getMissingVal2() + "\"");
				csv.append(",");
				break;
			case SPSS_THREE_MISSVAL:
				csv.append("\"" + var.getMissingVal1() + "\"");
				csv.append(",");
				csv.append("\"" + var.getMissingVal2() + "\"");
				csv.append(",");
				csv.append("\"" + var.getMissingVal3() + "\"");
				break;
			case SPSS_MISS_RANGE:
				csv.append("\"" + var.getMissingVal1() + "\"");
				csv.append(",");
				csv.append("\"" + var.getMissingVal2() + "\"");
				csv.append(",");
				break;
			case SPSS_MISS_RANGEANDVAL:
				csv.append("\"" + var.getMissingVal1() + "\"");
				csv.append(",");
				csv.append("\"" + var.getMissingVal2() + "\"");
				csv.append(",");
				csv.append("\"" + var.getMissingVal3() + "\"");
				break;
			case SPSS_UNKNOWN:
				csv.append(",");
				csv.append(",");
				break;
			}
			csv.append(",");
			csv.append(var.getWidth());
			csv.append(",");
			csv.append(var.getDecimals());
			csv.append(",");
			csv.append("\"" + messageSource.getMessage("spss.measureLevel." + var.getMeasureLevel(), null, Locale.ENGLISH) + "\"");
			csv.append(",");
			csv.append("\"" + messageSource.getMessage("spss.role." + var.getRole(), null, Locale.ENGLISH) + "\"");
			csv.append(",");
			if (var.getAttributes() != null && var.getAttributes().size() > 0) {
				csv.append("\"");
				for (SPSSValueLabelDTO val : var.getAttributes()) {
					csv.append("{");
					csv.append(val.getValue());
					csv.append("=");
					csv.append(val.getLabel());
					csv.append("}");
				}
				csv.append("\"");
			}
			String dw_construct = "", dw_measocc = "", dw_instrument = "", dw_itemtext = "", dw_filtervar = "";
			if (var.getDw_attributes() != null && var.getDw_attributes().size() > 0) {
				for (SPSSValueLabelDTO val : var.getDw_attributes()) {
					switch (val.getLabel()) {
					case "dw_construct":
						dw_construct = val.getValue();
						break;
					case "dw_measocc":
						dw_measocc = val.getValue();
						break;
					case "dw_instrument":
						dw_instrument = val.getValue();
						break;
					case "dw_itemtext":
						dw_itemtext = val.getValue();
						break;
					case "dw_filtervar":
						dw_filtervar = val.getValue();
						break;
					}
				}
			}
			csv.append(",");
			if (!dw_construct.isEmpty())
				csv.append("\"" + cleanString(dw_construct) + "\"");
			csv.append(",");
			if (!dw_measocc.isEmpty())
				csv.append("\"" + cleanString(dw_measocc) + "\"");
			csv.append(",");
			if (!dw_instrument.isEmpty())
				csv.append("\"" + cleanString(dw_instrument) + "\"");
			csv.append(",");
			if (!dw_itemtext.isEmpty())
				csv.append("\"" + cleanString(dw_itemtext) + "\"");
			csv.append(",");
			if (!dw_filtervar.isEmpty())
				csv.append(dw_filtervar);
			csv.append("\n");
		}
		return csv;
	}

	/**
	 * This function removes all line-break and tabulator commands from the passed String.
	 * 
	 * @param s
	 *          String, which has to be cleaned
	 * @return Cleaned String
	 */
	private String cleanString(String s) {
		return s.replaceAll("(\\r|\\n|\\t)", " ").replaceAll("\"", "'");
	}

	/**
	 * @param doc
	 * @throws DocumentException
	 * @throws IOException
	 *           TODO
	 */
	private byte[] createByteArrayFromXML(Document doc) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			XMLWriter writer = new XMLWriter(baos, OutputFormat.createPrettyPrint());
			writer.write(doc);
			return baos.toString().getBytes();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public String formatFilename(String s) {
		if (s != null) {
			if (s.length() > 50) {
				s = s.substring(0, 49);
			}
			s = s.toLowerCase().trim();
			return s.replaceAll("[^a-zA-Z0-9]", "_");
		}
		return null;
	}

	private void setAdditionalFilestoExportList(final List<Entry<String, byte[]>> exportList, final List<FileDTO> files, final String folderName)
	    throws DataWizSystemException {
		Set<String> filenames = new HashSet<>();
		int doublettCount = 0;
		for (FileDTO file : files) {
			if (!filenames.add(file.getFileName())) {
				if (file.getFileName().split("\\.").length > 1) {
					file.setFileName(file.getFileName().replaceFirst("\\.", " (" + ++doublettCount + ")\\."));
				} else
					file.setFileName(file.getFileName() + "(" + ++doublettCount + ")");
			}
			MinioResult result = minioUtil.getFile(file);
			if (result.equals(MinioResult.OK) && file.getContent().length > 0) {
				// only files < 20 MB
				if (file.getFileSize() < 20000000)
					exportList.add(new SimpleEntry<String, byte[]>(folderName + file.getFileName(), file.getContent()));
			} else {
				throw new DataWizSystemException(
				    messageSource.getMessage("logging.minio.read.error", new Object[] { result.name() }, LocaleContextHolder.getLocale()),
				    DataWizErrorCodes.MINIO_READ_ERROR);
			}
		}
	}
}
