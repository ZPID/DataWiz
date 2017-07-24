package de.zpid.datawiz.controller;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicLong;

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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import de.zpid.datawiz.dto.DmpDTO;
import de.zpid.datawiz.dto.ExportListDTO;
import de.zpid.datawiz.dto.FileDTO;
import de.zpid.datawiz.dto.ProjectDTO;
import de.zpid.datawiz.dto.RecordDTO;
import de.zpid.datawiz.dto.StudyDTO;
import de.zpid.datawiz.dto.UserDTO;
import de.zpid.datawiz.enumeration.DataWizErrorCodes;
import de.zpid.datawiz.enumeration.ExportStates;
import de.zpid.datawiz.enumeration.PageState;
import de.zpid.datawiz.enumeration.Roles;
import de.zpid.datawiz.form.ProjectForm;
import de.zpid.datawiz.service.ExportService;
import de.zpid.datawiz.util.BreadCrumpUtil;
import de.zpid.datawiz.util.DDIUtil;
import de.zpid.datawiz.util.UserUtil;

@Controller
@RequestMapping(value = "/export")
@SessionAttributes({ "breadcrumpList", "ProjectForm" })
public class ExportController extends SuperController {

  private static Logger log = LogManager.getLogger(ExportController.class);

  @Autowired
  private DDIUtil ddi;

  @Autowired
  ExportService exportService;

  @RequestMapping(value = { "", "/{pid}" }, method = RequestMethod.GET)
  public String showExportPage(@PathVariable final Optional<Long> pid, final ModelMap model,
      final RedirectAttributes reAtt) {
    log.trace("Entering showExportPage for project [id: {}]", () -> pid.isPresent() ? pid.get() : "null");
    if (!pid.isPresent()) {
      reAtt.addFlashAttribute("errorMSG",
          messageSource.getMessage("roles.error.empty.form", null, LocaleContextHolder.getLocale()));
      return "redirect:/panel";
    }
    final UserDTO user = UserUtil.getCurrentUser();
    if (user == null) {
      log.warn("Auth User Object == null - redirect to login");
      return "redirect:/login";
    }
    final ProjectForm pForm = createProjectForm();
    switch (getExportForm(pid.get(), pForm, user)) {
    case PROJECT_NOT_AVAILABLE:

      break;
    case DATABASE_ERROR:

      break;
    case USER_ACCESS_PERMITTED:

      break;

    default:
      model.put("breadcrumpList", BreadCrumpUtil.generateBC(PageState.PROJECT,
          new String[] { pForm.getProject().getTitle() }, null, messageSource));
      model.put("subnaviActive", PageState.EXPORT.name());
      model.put("ProjectForm", pForm);
      log.trace("Method showExportPage successfully completed");
      break;
    }
    return "export";
  }

  private DataWizErrorCodes getExportForm(final Long pid, final ProjectForm pForm, final UserDTO user) {
    DataWizErrorCodes ret = DataWizErrorCodes.OK;
    if (user.hasRole(Roles.ADMIN) || user.hasRole(Roles.PROJECT_ADMIN, pid, false)
        || user.hasRole(Roles.PROJECT_WRITER, pid, false) || user.hasRole(Roles.PROJECT_READER, pid, false)) {
      try {
        ProjectDTO project = projectDAO.findById(pid);
        if (project != null && project.getId() > 0) {
          List<StudyDTO> studies = studyDAO.findAllStudiesByProjectId(project);
          if (studies != null) {
            studies.parallelStream().forEach(study -> {
              try {
                study.setRecords(recordDAO.findRecordsWithStudyID(study.getId()));
              } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
              }
            });
            pForm.setStudies(studies);
          }
          pForm.setProject(project);
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
  public void exportProject(@ModelAttribute("ProjectForm") ProjectForm pForm, @PathVariable final Optional<Long> pid,
      final ModelMap model, final RedirectAttributes reAtt, HttpServletResponse response) {
    ProjectDTO project = null;
    DmpDTO dmp = null;
    List<FileDTO> pFiles = new LinkedList<>();
    List<StudyDTO> studies = new LinkedList<>();
    List<FileDTO> sFiles = new LinkedList<>();
    Map<Long, RecordDTO> records = new HashMap<Long, RecordDTO>();
    try {
      project = projectDAO.findById(pid.get());
      if (project != null)
        for (ExportListDTO export : pForm.getExportList()) {
          RecordDTO recTMP = null;
          switch (export.getState()) {
          case PROJECT_EXPORT_METADATA:
            if (!export.isExport()) {
              ProjectDTO projectTMP = (ProjectDTO) applicationContext.getBean("ProjectDTO");
              projectTMP.setId(project.getId());
              projectTMP.setTitle(project.getTitle());
              project = projectTMP;
            }
            break;
          case PROJECT_EXPORT_DMP:
            if (export.isExport())
              dmp = dmpDAO.findByID(project);
            break;
          case PROJECT_EXPORT_MATERIAL:
            if (export.isExport())
              pFiles.addAll(fileDAO.findProjectMaterialFiles(project));
            break;
          case STUDY_EXPORT_METADATA:
            if (export.isExport())
              studies.add(studyDAO.findById(export.getStudyId(), project.getId(), false, false));
            else {
              AtomicLong loadStudyMeta = new AtomicLong(0);
              pForm.getExportList().forEach(exp -> {
                if (exp.getStudyId() == export.getStudyId()
                    && ((exp.getState().equals(ExportStates.STUDY_EXPORT_MATERIAL) && exp.isExport())
                        || (exp.getState().equals(ExportStates.RECORD_EXPORT_CODEBOOK) && exp.isExport())
                        || (exp.getState().equals(ExportStates.RECORD_EXPORT_MATRIX) && exp.isExport())
                        || (exp.getState().equals(ExportStates.RECORD_EXPORT_METADATA) && exp.isExport()))) {
                  loadStudyMeta.set(exp.getStudyId());
                }
              });
              if (loadStudyMeta.get() > 0) {
                studies.add(studyDAO.findById(loadStudyMeta.get(), project.getId(), true, false));
              }
            }
            break;
          case STUDY_EXPORT_MATERIAL:
            if (export.isExport()) {
              StudyDTO study = (StudyDTO) applicationContext.getBean("StudyDTO");
              study.setId(export.getStudyId());
              study.setProjectId(project.getId());
              sFiles.addAll(fileDAO.findStudyMaterialFiles(study));
              study = null;
            }
            break;
          case RECORD_EXPORT_METADATA:
            if (export.isExport()) {
              RecordDTO recDB = recordDAO.findRecordWithID(export.getRecordId(), 0);
              if (recDB != null) {
                recDB.setAttributes(recordDAO.findRecordAttributes(recDB.getVersionId(), false));
                if (records.containsKey(export.getRecordId())) {
                  recTMP = records.get(export.getRecordId());
                  recDB.setVariables(recTMP.getVariables());
                  recDB.setDataMatrix(recTMP.getDataMatrix());
                  recDB.setDataMatrixJson(recTMP.getDataMatrixJson());
                  records.replace(export.getRecordId(), recDB);
                } else {
                  records.put(export.getRecordId(), recDB);
                }
              }
            }
            break;
          case RECORD_EXPORT_CODEBOOK:
            if (export.isExport()) {
              if (records.containsKey(export.getRecordId())) {
                recTMP = records.get(export.getRecordId());
              } else {
                RecordDTO recDB = recordDAO.findRecordWithID(export.getRecordId(), 0);
                if (recDB != null) {
                  recTMP = (RecordDTO) applicationContext.getBean("RecordDTO");
                  recTMP.setRecordName(recDB.getRecordName());
                  recTMP.setStudyId(export.getStudyId());
                  recTMP.setId(export.getRecordId());
                  recTMP.setVersionId(export.getVersionId());
                  records.put(export.getRecordId(), recTMP);
                }
              }
              if (recTMP != null) {
                recTMP.setVariables(recordDAO.findVariablesByVersionID(export.getVersionId()));
                recTMP.getVariables().parallelStream().forEach(var -> {
                  try {
                    var.setAttributes(recordDAO.findVariableAttributes(var.getId(), true));
                    var.setValues(recordDAO.findVariableValues(var.getId(), true));
                  } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                  }
                });
              }
            }
            break;
          case RECORD_EXPORT_MATRIX:
            if (export.isExport()) {
              if (records.containsKey(export.getRecordId())) {
                recTMP = records.get(export.getRecordId());
              } else {
                RecordDTO recDB = recordDAO.findRecordWithID(export.getRecordId(), 0);
                if (recDB != null) {
                  recTMP = (RecordDTO) applicationContext.getBean("RecordDTO");
                  recTMP.setRecordName(recDB.getRecordName());
                  recTMP.setStudyId(export.getStudyId());
                  recTMP.setId(export.getRecordId());
                  recTMP.setVersionId(export.getVersionId());
                  records.put(export.getRecordId(), recTMP);
                }
              }
              if (recTMP != null) {
                recTMP.setDataMatrixJson(recordDAO.findMatrixByVersionId(export.getVersionId()));
                if (recTMP.getDataMatrixJson() != null && !recTMP.getDataMatrixJson().isEmpty())
                  recTMP.setDataMatrix(
                      new Gson().fromJson(recTMP.getDataMatrixJson(), new TypeToken<List<List<Object>>>() {
                      }.getType()));
              }
            }
            break;
          default:
            break;
          }
        }
    } catch (Exception e) {
      // TODO
      e.printStackTrace();
    }
    log.warn("Project: " + project.getTitle() + " - ID: " + project.getId());
    if (dmp != null)
      log.warn("DMP ID: " + dmp.getId());
    if (pFiles != null)
      pFiles.forEach(s -> log.warn("PFILE: " + s.getFileName() + " - pid: " + s.getProjectId()));
    if (studies != null)
      studies.forEach(s -> log.warn("Study: " + s.getTitle() + " - ID: " + s.getId()));
    if (sFiles != null)
      sFiles.forEach(s -> log.warn("SFILE: " + s.getFileName() + " - sid: " + s.getStudyId()));
    if (records != null)
      records.forEach((k, v) -> {
        log.warn("Record: " + v.getRecordName() + " - rid: " + k);
        v.getAttributes().forEach(att -> log.warn("R-ATTR: " + att));
        if (v.getVariables() != null)
          v.getVariables().forEach(a -> {
            log.warn("VAR: " + a);
            a.getAttributes().forEach(at -> log.warn("V-ATTR: " + at));
            a.getValues().forEach(av -> log.warn("V-VAL: " + av));
          });
        log.warn("Matrix: " + v.getDataMatrixJson());
      });

    // XML EXPORT
    Document projectdmpxml = ddi.exportProjectAndDMPXML(project, dmp);
    List<Entry<String, byte[]>> files = new ArrayList<>();
    files.add(new SimpleEntry<String, byte[]>(project.getTitle() + ".xml", createbyteArrayFromXML(projectdmpxml)));
    StringBuilder res = new StringBuilder();
    byte[] export = exportService.exportZip(files, res);
    if (export != null) {
      response.setContentType("application/zip");
      response.setHeader("Content-Disposition", "attachment; filename=\"" + project.getTitle() + ".zip\"");
      try {
        response.getOutputStream().write(export);
        response.flushBuffer();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }

    }

  }

  /**
   * @param doc
   */
  private byte[] createbyteArrayFromXML(Document doc) {
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

}
