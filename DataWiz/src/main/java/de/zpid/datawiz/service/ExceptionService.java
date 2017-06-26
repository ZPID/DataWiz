package de.zpid.datawiz.service;

import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import de.zpid.datawiz.enumeration.DataWizErrorCodes;
import de.zpid.datawiz.exceptions.DataWizSystemException;

@Service
public class ExceptionService {

  private static Logger log = LogManager.getLogger(ExceptionService.class);

  @Autowired
  protected MessageSource messageSource;
  @Autowired
  protected Environment env;

  /**
   * @param pid
   * @param model
   * @param redirectAttributes
   * @param ret
   * @param e
   * @return
   */
  public String setErrorMessagesAndRedirects(final Optional<Long> pid, final Optional<Long> studyId,
      final Optional<Long> recordId, final ModelMap model, final RedirectAttributes redirectAttributes, Exception e,
      String functionName) {
    String ret = null;
    if (e instanceof DataWizSystemException) {
      DataWizSystemException dwe = (DataWizSystemException) e;
      if (dwe.getErrorCode().equals(DataWizErrorCodes.DATABASE_ERROR))
        log.fatal("Exception during {} Message[{}]]", () -> functionName, () -> e);
      else
        log.warn("DataWizSystemException during {} Code [{}], Message:", () -> functionName,
            () -> dwe.getErrorCode().name(), () -> e);
      if (dwe.getErrorCode().equals(DataWizErrorCodes.PROJECT_NOT_AVAILABLE)
          || dwe.getErrorCode().equals(DataWizErrorCodes.MISSING_PID_ERROR)) {
        redirectAttributes.addFlashAttribute("errorMSG",
            messageSource.getMessage("project.not.available", null, LocaleContextHolder.getLocale()));
        ret = "redirect:/panel";
      } else if (dwe.getErrorCode().equals(DataWizErrorCodes.STUDY_NOT_AVAILABLE)
          || dwe.getErrorCode().equals(DataWizErrorCodes.MISSING_STUDYID_ERROR)) {
        redirectAttributes.addFlashAttribute("errorMSG",
            messageSource.getMessage("study.not.available", null, LocaleContextHolder.getLocale()));
        ret = "redirect:/project/" + pid.get() + "/studies";
      } else if (((DataWizSystemException) e).getErrorCode().equals(DataWizErrorCodes.RECORD_NOT_AVAILABLE)) {
        redirectAttributes.addFlashAttribute("errorMSG",
            messageSource.getMessage("record.not.available", null, LocaleContextHolder.getLocale()));
        ret = "redirect:/project/" + pid.get() + "/study/" + studyId.get() + "/records";
      } else if (((DataWizSystemException) e).getErrorCode().equals(DataWizErrorCodes.IMPORT_TYPE_NOT_SUPPORTED)) {
        redirectAttributes.addFlashAttribute("errorMSG",
            messageSource.getMessage("import.error.file.extension", null, LocaleContextHolder.getLocale()));
        ret = "redirect:/project/" + pid.get() + "/study/" + studyId.get() + "/record/" + recordId.get();
      } else if (((DataWizSystemException) e).getErrorCode().equals(DataWizErrorCodes.IMPORT_FILE_IS_EMPTY)) {
        redirectAttributes.addFlashAttribute("errorMSG",
            messageSource.getMessage("import.error.file.null", null, LocaleContextHolder.getLocale()));
        ret = "redirect:/project/" + pid.get() + "/study/" + studyId.get() + "/record/" + recordId.get();
      } else if (dwe.getErrorCode().equals(DataWizErrorCodes.DATABASE_ERROR)) {
        model
            .put("errormsg",
                messageSource
                    .getMessage("dbs.sql.exception",
                        new Object[] { env.getRequiredProperty("organisation.admin.email"),
                            e.getMessage().replaceAll("\n", "").replaceAll("\"", "\'") },
                        LocaleContextHolder.getLocale()));
        ret = "error";
      }
    } else {
      log.fatal("Exception during {} Message: ", () -> functionName, () -> e);
      ret = "error";
      model
          .put("errormsg",
              messageSource
                  .getMessage("dbs.sql.exception",
                      new Object[] { env.getRequiredProperty("organisation.admin.email"),
                          e.getMessage().replaceAll("\n", "").replaceAll("\"", "\'") },
                      LocaleContextHolder.getLocale()));
    }
    return ret;
  }

}
