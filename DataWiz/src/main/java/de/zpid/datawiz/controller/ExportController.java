package de.zpid.datawiz.controller;

import java.io.IOException;
import java.io.StringWriter;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
import de.zpid.datawiz.enumeration.PageState;
import de.zpid.datawiz.enumeration.Roles;
import de.zpid.datawiz.exceptions.DataWizSystemException;
import de.zpid.datawiz.form.ExportProjectForm;
import de.zpid.datawiz.form.StudyForm;
import de.zpid.datawiz.service.ExportService;
import de.zpid.datawiz.service.RecordService;
import de.zpid.datawiz.service.StudyService;
import de.zpid.datawiz.util.BreadCrumpUtil;
import de.zpid.datawiz.util.DDIUtil;
import de.zpid.datawiz.util.UserUtil;

@Controller
@RequestMapping(value = "/export")
@SessionAttributes({ "breadcrumpList", "ExportProjectForm" })
public class ExportController extends SuperController {

	private static Logger log = LogManager.getLogger(ExportController.class);

	private final String FILES_PROJECT_FOLDER = "supplementary_files/";
	private final String STUDY_FOLDER = "studies/";
	private final String RECORD_FOLDER = "records/";
	private final String PROJECT_FILE_NAME = "project_long_term.xml";
	private final String STUDY_FILE_NAME = "study_long_term.xml";
	private final String RECORD_FILE_NAME = "record_long_term.xml";

	@Autowired
	private DDIUtil ddi;

	@Autowired
	StudyService studyService;
	@Autowired
	RecordService recordService;

	@Autowired
	ExportService exportService;

	@RequestMapping(value = { "", "/{pid}" }, method = RequestMethod.GET)
	public String showExportPage(@PathVariable final Optional<Long> pid, final ModelMap model, final RedirectAttributes reAtt) {
		log.trace("Entering showExportPage for project [id: {}]", () -> pid.isPresent() ? pid.get() : "null");
		if (!pid.isPresent()) {
			reAtt.addFlashAttribute("errorMSG", messageSource.getMessage("roles.error.empty.form", null, LocaleContextHolder.getLocale()));
			return "redirect:/panel";
		}
		final UserDTO user = UserUtil.getCurrentUser();
		if (user == null) {
			log.warn("Auth User Object == null - redirect to login");
			return "redirect:/login";
		}
		final ExportProjectForm exportForm = (ExportProjectForm) applicationContext.getBean("ExportProjectForm");
		switch (getExportForm(pid.get(), exportForm, user)) {
		case PROJECT_NOT_AVAILABLE:

			break;
		case DATABASE_ERROR:

			break;
		case USER_ACCESS_PERMITTED:

			break;

		default:
			model.put("breadcrumpList", BreadCrumpUtil.generateBC(PageState.PROJECT, new String[] { exportForm.getProjectTitle() }, null, messageSource));
			model.put("subnaviActive", PageState.EXPORT.name());
			model.put("ExportProjectForm", exportForm);
			log.trace("Method showExportPage successfully completed");
			break;
		}

		return "export";
	}

	private DataWizErrorCodes getExportForm(final Long pid, final ExportProjectForm exportForm, final UserDTO user) {
		DataWizErrorCodes ret = DataWizErrorCodes.OK;
		if (user.hasRole(Roles.ADMIN) || user.hasRole(Roles.PROJECT_ADMIN, pid, false) || user.hasRole(Roles.PROJECT_WRITER, pid, false)
		    || user.hasRole(Roles.PROJECT_READER, pid, false)) {
			try {
				ProjectDTO project = projectDAO.findById(pid);
				if (project != null && project.getId() > 0) {
					exportForm.setProjectTitle(project.getTitle());
					exportForm.setProjectId(project.getId());
					exportForm.setStudies(new ArrayList<>());
					List<StudyDTO> studies = studyDAO.findAllStudiesByProjectId(project);
					if (studies != null) {
						for (StudyDTO study : studies) {
							ExportStudyDTO studExp = (ExportStudyDTO) applicationContext.getBean("ExportStudyDTO");
							studExp.setStudyId(study.getId());
							studExp.setStudyTitle(study.getTitle());
							studExp.setRecords(new ArrayList<>());
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
							List<String> warnings = new ArrayList<>();
							warnings.add("this is only a test");
							studExp.setWarnings(warnings);
							exportForm.getStudies().add(studExp);
						}
					}
				} else {
					ret = DataWizErrorCodes.PROJECT_NOT_AVAILABLE;
				}
			} catch (Exception e) {
				ret = DataWizErrorCodes.DATABASE_ERROR;
				log.warn("DBS Exception during getExportForm", () -> e);
			}
		} else {
			ret = DataWizErrorCodes.USER_ACCESS_PERMITTED;
		}
		return ret;
	}

	@RequestMapping(value = { "", "/{pid}" }, method = RequestMethod.POST, produces = "application/zip")
	public void exportProject(@ModelAttribute("ExportProjectForm") ExportProjectForm exportForm, @PathVariable final Optional<Long> pid,
	    final ModelMap model, final RedirectAttributes reAtt, HttpServletResponse response) {
		List<Entry<String, byte[]>> files = new ArrayList<>();
		final UserDTO user = UserUtil.getCurrentUser();
		// TODO CHECK RIGHTS
		if (exportForm != null && exportForm.getProjectId() > 0) {
			try {
				ProjectDTO projectDB = projectDAO.findById(pid.get());
				if (projectDB != null) {
					ProjectDTO project;
					DmpDTO dmpDB = null;
					List<FileDTO> pFiles = null;
					dmpDB = dmpDAO.findByID(projectDB);
					if (dmpDB != null && dmpDB.getId() > 0) {
						dmpDB.setUsedDataTypes(formTypeDAO.findSelectedFormTypesByIdAndType(dmpDB.getId(), DWFieldTypes.DATATYPE, false));
						dmpDB.setUsedCollectionModes(formTypeDAO.findSelectedFormTypesByIdAndType(dmpDB.getId(), DWFieldTypes.COLLECTIONMODE, false));
						dmpDB.setSelectedMetaPurposes(formTypeDAO.findSelectedFormTypesByIdAndType(dmpDB.getId(), DWFieldTypes.METAPORPOSE, false));
					}
					if (exportForm.isExportMetaData() || exportForm.isExportDMP() || exportForm.isExportProjectMaterial()) {
						if (exportForm.isExportMetaData()) {
							project = projectDB;
						} else {
							project = (ProjectDTO) applicationContext.getBean("ProjectDTO");
							project.setId(projectDB.getId());
							project.setTitle(projectDB.getTitle());
						}
						if (exportForm.isExportProjectMaterial()) {
							pFiles = fileDAO.findProjectMaterialFiles(project);
							if (pFiles != null && pFiles.size() > 0)
								setAdditionalFilestoExportList(files, pFiles, FILES_PROJECT_FOLDER);
						}
						Document pdoc = ddi.createProjectDocument(project, exportForm.isExportDMP() ? dmpDB : null, pFiles);
						if (pdoc != null)
							files.add(new SimpleEntry<String, byte[]>(PROJECT_FILE_NAME, createByteArrayFromXML(pdoc)));
						else {
							throw new DataWizSystemException(messageSource.getMessage("logging.xml.create.error",
							    new Object[] { "createProjectDocument", exportForm.toString() }, LocaleContextHolder.getLocale()),
							    DataWizErrorCodes.STUDY_NOT_AVAILABLE);
						}
					}
					// Create Studies
					if (exportForm.getStudies() != null) {
						for (ExportStudyDTO studyEx : exportForm.getStudies()) {
							List<FileDTO> sFiles = null;
							StudyDTO study = null;
							StudyDTO studyDB = studyDAO.findById(studyEx.getStudyId(), exportForm.getProjectId(), false, false);
							if (studyDB != null) {
								String studyFolder = STUDY_FOLDER + formatFilename(studyDB.getTitle()) + "/";
								studyService.setStudyDTO(studyDB);
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
										if (sFiles != null && sFiles.size() > 0)
											setAdditionalFilestoExportList(files, sFiles, studyFolder + FILES_PROJECT_FOLDER);
									}
									StudyForm sForm = (StudyForm) applicationContext.getBean("StudyForm");
									sForm.setProject(projectDB);
									sForm.setStudy(study);
									sForm.setCollectionModes(formTypeDAO.findAllByType(true, DWFieldTypes.COLLECTIONMODE));
									sForm.setSourFormat(formTypeDAO.findAllByType(true, DWFieldTypes.DATAFORMAT));
									Document sdoc = ddi.createStudyDocument(sForm, sFiles, user);
									if (sdoc != null) {
										files.add(new SimpleEntry<String, byte[]>(studyFolder + STUDY_FILE_NAME, createByteArrayFromXML(sdoc)));
									} else {
										throw new DataWizSystemException(messageSource.getMessage("logging.xml.create.error",
										    new Object[] { "createProjectDocument", exportForm.toString() }, LocaleContextHolder.getLocale()),
										    DataWizErrorCodes.STUDY_NOT_AVAILABLE);
									}
								}
								// Create Records
								if (studyEx.getRecords() != null) {
									for (ExportRecordDTO recordEx : studyEx.getRecords()) {
										RecordDTO recordDB = recordDAO.findRecordWithID(recordEx.getRecordId(), recordEx.getVersionId());
										if (recordDB != null) {
											List<String> parsingErrors = new LinkedList<>();
											recordService.setRecordDTO(parsingErrors, recordDB);
											// TODO
											String recordFolder = studyFolder + RECORD_FOLDER + formatFilename(recordDB.getRecordName()) + "/";
											Document rdoc = ddi.createRecordDocument(recordDB);
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

							} else {
								throw new DataWizSystemException(
								    messageSource.getMessage("logging.study.not.found", new Object[] { studyEx.getStudyId() }, LocaleContextHolder.getLocale()),
								    DataWizErrorCodes.STUDY_NOT_AVAILABLE);
							}

						}
					}

				} else {
					throw new DataWizSystemException(
					    messageSource.getMessage("logging.project.not.found", new Object[] { exportForm.getProjectId() }, LocaleContextHolder.getLocale()),
					    DataWizErrorCodes.PROJECT_NOT_AVAILABLE);
				}

			} catch (Exception e) {
				// TODO: handle exception
				log.warn("EXPORTERROR", () -> e);
			}
		}

		// case PROJECT_EXPORT_MATERIAL:
		// if (export.isExport())
		//
		// break;
		// case STUDY_EXPORT_METADATA:
		// if (export.isExport())
		// studies.add();
		// else {
		// AtomicLong loadStudyMeta = new AtomicLong(0);
		// pForm.getExportList().forEach(exp -> {
		// if (exp.getStudyId() == export.getStudyId()
		// && ((exp.getState().equals(ExportStates.STUDY_EXPORT_MATERIAL) && exp.isExport())
		// || (exp.getState().equals(ExportStates.RECORD_EXPORT_CODEBOOK) && exp.isExport())
		// || (exp.getState().equals(ExportStates.RECORD_EXPORT_MATRIX) && exp.isExport())
		// || (exp.getState().equals(ExportStates.RECORD_EXPORT_METADATA) && exp.isExport()))) {
		// loadStudyMeta.set(exp.getStudyId());
		// }
		// });
		// if (loadStudyMeta.get() > 0) {
		// studies.add(studyDAO.findById(loadStudyMeta.get(), project.getId(), true, false));
		// }
		// }
		// break;
		// case STUDY_EXPORT_MATERIAL:
		// if (export.isExport()) {
		// StudyDTO study = (StudyDTO) applicationContext.getBean("StudyDTO");
		// study.setId(export.getStudyId());
		// study.setProjectId(project.getId());
		// sFiles.addAll(fileDAO.findStudyMaterialFiles(study));
		// study = null;
		// }
		// break;
		// case RECORD_EXPORT_METADATA:
		// if (export.isExport()) {
		// RecordDTO recDB = recordDAO.findRecordWithID(export.getRecordId(), 0);
		// if (recDB != null) {
		// recDB.setAttributes(recordDAO.findRecordAttributes(recDB.getVersionId(), false));
		// if (records.containsKey(export.getRecordId())) {
		// recTMP = records.get(export.getRecordId());
		// recDB.setVariables(recTMP.getVariables());
		// recDB.setDataMatrix(recTMP.getDataMatrix());
		// recDB.setDataMatrixJson(recTMP.getDataMatrixJson());
		// records.replace(export.getRecordId(), recDB);
		// } else {
		// records.put(export.getRecordId(), recDB);
		// }
		// }
		// }
		// break;
		// case RECORD_EXPORT_CODEBOOK:
		// if (export.isExport()) {
		// if (records.containsKey(export.getRecordId())) {
		// recTMP = records.get(export.getRecordId());
		// } else {
		// RecordDTO recDB = recordDAO.findRecordWithID(export.getRecordId(), 0);
		// if (recDB != null) {
		// recTMP = (RecordDTO) applicationContext.getBean("RecordDTO");
		// recTMP.setRecordName(recDB.getRecordName());
		// recTMP.setStudyId(export.getStudyId());
		// recTMP.setId(export.getRecordId());
		// recTMP.setVersionId(export.getVersionId());
		// records.put(export.getRecordId(), recTMP);
		// }
		// }
		// if (recTMP != null) {
		// recTMP.setVariables(recordDAO.findVariablesByVersionID(export.getVersionId()));
		// recTMP.getVariables().parallelStream().forEach(var -> {
		// try {
		// var.setAttributes(recordDAO.findVariableAttributes(var.getId(), true));
		// var.setValues(recordDAO.findVariableValues(var.getId(), true));
		// } catch (Exception e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// });
		// }
		// }
		// break;
		// case RECORD_EXPORT_MATRIX:
		// if (export.isExport()) {
		// if (records.containsKey(export.getRecordId())) {
		// recTMP = records.get(export.getRecordId());
		// } else {
		// RecordDTO recDB = recordDAO.findRecordWithID(export.getRecordId(), 0);
		// if (recDB != null) {
		// recTMP = (RecordDTO) applicationContext.getBean("RecordDTO");
		// recTMP.setRecordName(recDB.getRecordName());
		// recTMP.setStudyId(export.getStudyId());
		// recTMP.setId(export.getRecordId());
		// recTMP.setVersionId(export.getVersionId());
		// records.put(export.getRecordId(), recTMP);
		// }
		// }
		// if (recTMP != null) {
		// recTMP.setDataMatrixJson(recordDAO.findMatrixByVersionId(export.getVersionId()));
		// if (recTMP.getDataMatrixJson() != null && !recTMP.getDataMatrixJson().isEmpty())
		// recTMP.setDataMatrix(
		// new Gson().fromJson(recTMP.getDataMatrixJson(), new TypeToken<List<List<Object>>>() {
		// }.getType()));
		// }
		// }
		// break;
		// default:
		// break;
		// }
		// }
		// } catch (Exception e) {
		// // TODO
		// e.printStackTrace();
		// }

		// XML EXPORT

		StringBuilder res = new StringBuilder();
		byte[] export = exportService.exportZip(files, res);
		if (export != null) {
			response.setContentType("application/zip");
			response.setHeader("Content-Disposition", "attachment; filename=\"" + formatFilename(exportForm.getProjectTitle()) + ".zip\"");
			try {
				response.getOutputStream().write(export);
				response.flushBuffer();
			} catch (IOException e) {
				// TODO Auto-generated catch block

				e.printStackTrace();

			}

		}

	}

	private void setAdditionalFilestoExportList(final List<Entry<String, byte[]>> exportList, final List<FileDTO> files, final String folderName)
	    throws DataWizSystemException {
		for (FileDTO file : files) {
			MinioResult result = minioUtil.getFile(file);
			if (result.equals(MinioResult.OK) && file.getContent().length > 0) {
				exportList.add(new SimpleEntry<String, byte[]>(folderName + file.getFileName(), file.getContent()));
			} else {
				throw new DataWizSystemException(
				    messageSource.getMessage("logging.minio.read.error", new Object[] { result.name() }, LocaleContextHolder.getLocale()),
				    DataWizErrorCodes.MINIO_READ_ERROR);
			}
		}
	}

	/**
	 * @param doc
	 */
	private byte[] createByteArrayFromXML(Document doc) {
		try {
			StringWriter sw = new StringWriter();
			XMLWriter writer = new XMLWriter(sw, OutputFormat.createPrettyPrint());
			writer.write(doc);
			return sw.toString().getBytes();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	private String formatFilename(String s) {
		if (s != null) {
			if (s.length() > 100) {
				s = s.substring(0, 99);
			}
			s = s.toLowerCase().trim();
			return s.replaceAll("[^a-zA-Z0-9]", "_");
		}
		return null;
	}

}
