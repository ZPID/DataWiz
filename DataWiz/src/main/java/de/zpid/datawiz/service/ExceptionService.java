package de.zpid.datawiz.service;

import de.zpid.datawiz.enumeration.DataWizErrorCodes;
import de.zpid.datawiz.exceptions.DataWizSystemException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;


/**
 * Service class for Exception Handling.
 * <p>
 * This file is part of the DataWiz distribution (https://github.com/ZPID/DataWiz).
 * Copyright (c) 2018 <a href="https://leibniz-psychology.org/">Leibniz Institute for Psychology Information (ZPID)</a>.
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 * <p>
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <a href="http://www.gnu.org/licenses/">http://www.gnu.org/licenses/</a>.
 *
 * @author Ronny Boelter
 * @version 1.0
 **/
@Service
public class ExceptionService {

    private static final Logger log = LogManager.getLogger(ExceptionService.class);

    private final MessageSource messageSource;
    private final Environment env;

    @Autowired
    public ExceptionService(MessageSource messageSource, Environment env) {
        this.messageSource = messageSource;
        this.env = env;
    }

    /**
     * This function is used to catch exceptions and forwards the user to the corresponding page with an error message
     *
     * @param pid                Project identifier
     * @param studyId            Study identifier
     * @param recordId           Record identifier
     * @param model              {@link ModelMap}
     * @param redirectAttributes {@link RedirectAttributes}
     * @param e                  {@link Exception} that was thrown
     * @param functionName       {@link String} contains the name of the function  where the Exception was thrown
     * @return {@link String} Mapping to a page, depending on the exception
     */
    public String setErrorMessagesAndRedirects(final long pid, final long studyId, final long recordId,
                                               final ModelMap model, final RedirectAttributes redirectAttributes, final Exception e, final String functionName) {
        String ret = null;
        if (e instanceof DataWizSystemException) {
            DataWizSystemException dwe = (DataWizSystemException) e;
            if (dwe.getErrorCode().equals(DataWizErrorCodes.DATABASE_ERROR))
                log.fatal("Exception during {} Message[{}]]", () -> functionName, () -> e);
            else
                log.warn("DataWizSystemException during {} Code [{}], Message:", () -> functionName, () -> dwe.getErrorCode().name(), () -> e);
            if (dwe.getErrorCode().equals(DataWizErrorCodes.PROJECT_NOT_AVAILABLE) || dwe.getErrorCode().equals(DataWizErrorCodes.MISSING_PID_ERROR)) {
                redirectAttributes.addFlashAttribute("errorMSG", messageSource.getMessage("project.not.available", null, LocaleContextHolder.getLocale()));
                ret = "redirect:/panel";
            } else if (dwe.getErrorCode().equals(DataWizErrorCodes.USER_ACCESS_PROJECT_PERMITTED)) {
                redirectAttributes.addFlashAttribute("errorMSG", messageSource.getMessage("project.access.denied", null, LocaleContextHolder.getLocale()));
                ret = "redirect:/panel";
            } else if (dwe.getErrorCode().equals(DataWizErrorCodes.STUDY_NOT_AVAILABLE)
                    || dwe.getErrorCode().equals(DataWizErrorCodes.MISSING_STUDYID_ERROR)) {
                redirectAttributes.addFlashAttribute("errorMSG", messageSource.getMessage("study.not.available", null, LocaleContextHolder.getLocale()));
                ret = "redirect:/project/" + pid + "/studies";
            } else if (dwe.getErrorCode().equals(DataWizErrorCodes.USER_ACCESS_STUDY_PERMITTED)) {
                redirectAttributes.addFlashAttribute("errorMSG", messageSource.getMessage("study.not.available", null, LocaleContextHolder.getLocale()));
                ret = "redirect:/project/" + pid + "/studies";
            } else if (((DataWizSystemException) e).getErrorCode().equals(DataWizErrorCodes.RECORD_NOT_AVAILABLE)) {
                redirectAttributes.addFlashAttribute("errorMSG", messageSource.getMessage("record.not.available", null, LocaleContextHolder.getLocale()));
                ret = "redirect:/project/" + pid + "/study/" + studyId + "/records";
            } else if (((DataWizSystemException) e).getErrorCode().equals(DataWizErrorCodes.RECORD_DELETE_ERROR)) {
                redirectAttributes.addFlashAttribute("errorMSG", messageSource
                        .getMessage(recordId > 0 ? "record.not.deleted.error" : "study.record.delete.error", null, LocaleContextHolder.getLocale()));
                ret = "redirect:/project/" + pid + "/study/" + studyId + (recordId > 0 ? "/record/" + recordId : "");
            } else if (((DataWizSystemException) e).getErrorCode().equals(DataWizErrorCodes.IMPORT_TYPE_NOT_SUPPORTED)) {
                redirectAttributes.addFlashAttribute("errorMSG",
                        messageSource.getMessage("import.error.file.extension", null, LocaleContextHolder.getLocale()));
                ret = "redirect:/project/" + pid + "/study/" + studyId + "/record/" + recordId;
            } else if (((DataWizSystemException) e).getErrorCode().equals(DataWizErrorCodes.IMPORT_FILE_IS_EMPTY)) {
                redirectAttributes.addFlashAttribute("errorMSG", messageSource.getMessage("import.error.file.null", null, LocaleContextHolder.getLocale()));
                ret = "redirect:/project/" + pid + "/study/" + studyId + "/record/" + recordId;
            } else if (((DataWizSystemException) e).getErrorCode().equals(DataWizErrorCodes.MISSING_UID_ERROR)) {
                redirectAttributes.addFlashAttribute("errorMSG", messageSource.getMessage("roles.error.empty.form",
                        new Object[]{env.getRequiredProperty("organisation.admin.email")}, LocaleContextHolder.getLocale()));
                ret = "redirect:/login";
            } else if (((DataWizSystemException) e).getErrorCode().equals(DataWizErrorCodes.MINIO_SAVE_ERROR) && recordId > 0) {
                redirectAttributes.addFlashAttribute("errorMSG", messageSource.getMessage("roles.error.empty.form",
                        new Object[]{env.getRequiredProperty("organisation.admin.email")}, LocaleContextHolder.getLocale()));
                ret = "redirect:/project/" + pid + "/study/" + studyId + "/record/" + recordId;
            } else if (dwe.getErrorCode().equals(DataWizErrorCodes.DATABASE_ERROR)) {
                model.put("errormsg",
                        messageSource.getMessage("dbs.sql.exception",
                                new Object[]{env.getRequiredProperty("organisation.admin.email"), e.getMessage().replaceAll("\n", "").replaceAll("\"", "\'")},
                                LocaleContextHolder.getLocale()));
                ret = "error";
            }
        } else {
            log.fatal("Exception during {} Message: ", () -> functionName, () -> e);
            ret = "error";
            model.put("errormsg",
                    messageSource.getMessage("dbs.sql.exception",
                            new Object[]{env.getRequiredProperty("organisation.admin.email"), e.getMessage().replaceAll("\n", "").replaceAll("\"", "\'")},
                            LocaleContextHolder.getLocale()));
        }
        return ret;
    }


    /**
     * This function is used to catch exceptions and forwards the user to the corresponding page with an error message
     *
     * @param pid                Project identifier
     * @param studyId            Study identifier
     * @param recordId           Record identifier
     * @param model              {@link ModelMap}
     * @param redirectAttributes {@link RedirectAttributes}
     * @param e                  {@link Exception} that was thrown
     * @param functionName       {@link String} contains the name of the function  where the Exception was thrown
     * @return {@link String} Mapping to a page, depending on the exception
     */
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public String setErrorMessagesAndRedirects(final Optional<Long> pid, final Optional<Long> studyId, final Optional<Long> recordId,
                                               final ModelMap model, final RedirectAttributes redirectAttributes, final Exception e, final String functionName) {
        return setErrorMessagesAndRedirects(pid != null && pid.isPresent() ? pid.get() : 0, studyId != null && studyId.isPresent() ? studyId.get() : 0,
                recordId != null && recordId.isPresent() ? recordId.get() : 0, model, redirectAttributes, e, functionName);
    }

}
