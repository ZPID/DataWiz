package de.zpid.datawiz.controller;

import de.zpid.datawiz.dto.DmpDTO;
import de.zpid.datawiz.dto.UserDTO;
import de.zpid.datawiz.enumeration.DataWizErrorCodes;
import de.zpid.datawiz.enumeration.DmpCategory;
import de.zpid.datawiz.enumeration.PageState;
import de.zpid.datawiz.enumeration.Roles;
import de.zpid.datawiz.exceptions.DWDownloadException;
import de.zpid.datawiz.exceptions.DataWizSystemException;
import de.zpid.datawiz.form.ProjectForm;
import de.zpid.datawiz.service.DMPService;
import de.zpid.datawiz.service.ExceptionService;
import de.zpid.datawiz.service.ProjectService;
import de.zpid.datawiz.util.BreadCrumbUtil;
import de.zpid.datawiz.util.UserUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import java.util.Locale;
import java.util.Optional;

/**
 * This controller handles all calls to /dmp/*
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
@Controller
@RequestMapping(value = "/dmp")
public class DMPController {

    private static Logger log = LogManager.getLogger(DMPController.class);

    private MessageSource messageSource;
    private ProjectService projectService;
    private ExceptionService exceptionService;
    private DMPService dmpService;
    private Environment env;
    private PlatformTransactionManager txManager;

    /**
     * Constructor for DMPController
     */
    @Autowired
    public DMPController(final MessageSource messageSource, final ProjectService projectService, final ExceptionService exceptionService,
                         final DMPService dmpService, final Environment env,
                         final PlatformTransactionManager txManager) {
        super();
        log.info("Loading DMPController for mapping /dmp");
        this.messageSource = messageSource;
        this.projectService = projectService;
        this.exceptionService = exceptionService;
        this.dmpService = dmpService;
        this.env = env;
        this.txManager = txManager;
    }


    /**
     * This function loads the DMP data for editing. Therefore, the pid is required. If no DMP has been found with the given pid, or other errors occur, the
     * exceptionService is called to handle the exceptions and redirect to the correct jsp.
     *
     * @param pid                ProjectID
     * @param pForm              ProjectForm
     * @param model              ModelMap
     * @param redirectAttributes RedirectAttributes
     * @return Mapping to dmp.jsp
     */
    @RequestMapping(value = {"", "/{pid}"}, method = RequestMethod.GET)
    public String editDMP(final @PathVariable Optional<Long> pid, final @ModelAttribute("ProjectForm") ProjectForm pForm, final ModelMap model,
                          RedirectAttributes redirectAttributes) {
        log.trace("Entering editDMP for DMP [pid: {}]", () -> pid);
        UserDTO user = UserUtil.getCurrentUser();
        if (user == null) {
            log.warn(messageSource.getMessage("logging.user.auth.missing", null, Locale.ENGLISH));
            return "redirect:/login";
        }
        String pName = "";
        if (!pid.isPresent() || pid.get() <= 0) {
            return "redirect:/panel";
        }
        try {
            projectService.getProjectForm(pForm, pid.get(), user, PageState.DMP, projectService.checkProjectRoles(user, pid.get(), 0, false, false));
            if (pForm != null && pForm.getProject() != null && pForm.getProject().getTitle() != null && !pForm.getProject().getTitle().trim().isEmpty()) {
                pName = pForm.getProject().getTitle();
            }
        } catch (Exception e) {
            return exceptionService.setErrorMessagesAndRedirects(pid, Optional.empty(), Optional.empty(), model, redirectAttributes, e, "dmpController.editDMP");
        }
        model.put("breadcrumbList", BreadCrumbUtil.generateBC(PageState.PROJECT, new String[]{pName}, null, messageSource));
        model.put("subnaviActive", PageState.DMP.name());
        model.put("ProjectForm", pForm);
        log.trace("Leaving editDMP for DMP [pid: {}]", () -> pid);
        return "dmp";
    }

    /**
     * This function is called from the dmp jsp to save the DMP form. DMP form is split in several parts, they are checked separately for changes, and only saved
     * if changes occurred. This should minimize the traffic to the database and the writing operations. If an error will occur during the saving and checking
     * process, this error will be send to the view and displayed. If the user tries to submit an unchanged DMP, nothing will be saved, and the user gets an info
     * message.
     *
     * @param pForm              ProjectForm
     * @param model              ModelMap
     * @param redirectAttributes RedirectAttributes
     * @param bRes               BindingResult
     * @param pid                ProjectID
     * @return mapping to the dmp.jsp if an error occurred, or if the form is unchanged <br>
     * redirect mapping to the GET function if saving was successful
     */
    @RequestMapping(value = {"", "/{pid}"}, method = RequestMethod.POST)
    public String saveDMP(@ModelAttribute("ProjectForm") ProjectForm pForm, ModelMap model, RedirectAttributes redirectAttributes, BindingResult bRes,
                          @PathVariable final Optional<Long> pid) {
        log.trace("Entering saveDMP for DMP [pid: {}]", () -> pid);
        UserDTO user = UserUtil.getCurrentUser();
        String ret;
        if (!pid.isPresent() || pid.get() <= 0 || pForm == null || pForm.getProject() == null || pForm.getProject().getId() <= 0
                || pForm.getProject().getId() != pid.get()) {
            bRes.reject("globalErrors", messageSource.getMessage("dmp.save.pid.error", new Object[]{env.getRequiredProperty("organisation.admin.email")},
                    LocaleContextHolder.getLocale()));
            ret = "dmp";
        } else if (projectService.checkProjectRoles(user, pForm.getProject().getId(), 0, true, false) == null) {
            bRes.reject("globalErrors", messageSource.getMessage("dmp.save.access.error", new Object[]{env.getRequiredProperty("organisation.admin.email")},
                    LocaleContextHolder.getLocale()));
            ret = "dmp";
        } else {
            TransactionStatus status = txManager.getTransaction(new DefaultTransactionDefinition());
            try {
                dmpService.saveNewDMPSetID(pForm);
                dmpService.saveDMPDataPart(pForm, bRes, DmpCategory.ADMIN, DmpDTO.AdminVal.class);
                dmpService.saveDMPDataPart(pForm, bRes, DmpCategory.RESEARCH, DmpDTO.ResearchVal.class);
                dmpService.saveDMPDataPart(pForm, bRes, DmpCategory.META, DmpDTO.MetaVal.class);
                dmpService.saveDMPDataPart(pForm, bRes, DmpCategory.SHARING, DmpDTO.SharingVal.class);
                dmpService.saveDMPDataPart(pForm, bRes, DmpCategory.STORAGE, DmpDTO.StorageVal.class);
                dmpService.saveDMPDataPart(pForm, bRes, DmpCategory.ORGANIZATION, DmpDTO.OrganizationVal.class);
                dmpService.saveDMPDataPart(pForm, bRes, DmpCategory.ETHICAL, DmpDTO.EthicalVal.class);
                dmpService.saveDMPDataPart(pForm, bRes, DmpCategory.COSTS, DmpDTO.CostsVal.class);
                if (bRes.hasErrors()) {
                    txManager.rollback(status);
                    ret = "dmp";
                } else {
                    txManager.commit(status);
                    redirectAttributes.addFlashAttribute("successMSG", messageSource.getMessage("dmp.save.success.msg", null, LocaleContextHolder.getLocale()));
                    ret = "redirect:/dmp/" + pForm.getDmp().getId();
                }
            } catch (DataWizSystemException e) {
                txManager.rollback(status);
                bRes.reject("globalErrors", messageSource.getMessage("dmp.save.dbs.error", new Object[]{env.getRequiredProperty("organisation.admin.email")},
                        LocaleContextHolder.getLocale()));
                ret = "dmp";
            }
        }
        if (bRes.hasErrors()) {
            model.put("breadcrumbList", BreadCrumbUtil.generateBC(PageState.PROJECT,
                    new String[]{(pForm != null && pForm.getProject() != null && pForm.getProject().getTitle() != null) ? pForm.getProject().getTitle() : ""}, null, messageSource));
            model.put("subnaviActive", PageState.DMP.name());
        }
        log.trace("Leaving saveDMP for DMP [pid: {}] with result: [error: {}; mapping: {}]", pid, bRes.hasErrors(), ret);
        return ret;
    }

    /**
     * This function is called if a user wants to download a data-management-plan export file (ODF). If no error occurs during creation, the desired file will be
     * made available for download. If an error occurs, an exception is thrown and displayed to the user.
     *
     * @param pid      ProjectID
     * @param type     Optional<String> Export type to identify the ODF file that the user wants to download.
     * @param response HttpServletResponse
     * @throws DWDownloadException The link to this function is called with the "_blank" HTML parameter, therefore it is useful to throw Exceptions. DWDownload-Exception are
     *                             captured and processed by the ExceptionHandlerController.
     */
    @RequestMapping(value = {"/{pid}/exportDMP/{type}"}, method = RequestMethod.GET)
    public void exportDMPODF(@PathVariable final Long pid, @PathVariable final String type,
                             final HttpServletResponse response) throws DWDownloadException {
        log.trace("Entering exportDMPODF for DMP [pid: {}] and exportType [{}]", () -> pid, () -> type);
        UserDTO user = UserUtil.getCurrentUser();
        if (user == null || (!user.hasRole(Roles.PROJECT_READER, pid, false) && !user.hasRole(Roles.PROJECT_ADMIN, pid, false)
                && !user.hasRole(Roles.PROJECT_WRITER, pid, false) && !user.hasRole(Roles.ADMIN))) {
            log.warn("Auth User Object empty or User is permitted to download this file");
            throw new DWDownloadException("export.access.denied");
        }
        byte[] content;
        try {
            // TODO? locale is set to German - should be changed if the form inputs are available in English
            content = dmpService.createDMPExport(pid, type, Locale.GERMAN);
        } catch (Exception e) {
            log.warn("Exception during dmpService.createDMPExport Message: ", () -> e);
            if (e instanceof DataWizSystemException) {
                if (((DataWizSystemException) e).getErrorCode().equals(DataWizErrorCodes.NO_DATA_ERROR))
                    throw new DWDownloadException("export.odt.error.dmp");
                else
                    throw new DWDownloadException("export.odt.error.project");
            } else {
                throw new DWDownloadException("dbs.sql.exception");
            }
        }
        try {
            response.setContentType("application/vnd.oasis.opendocument.text");
            response.setHeader("Content-Disposition", "attachment; filename=\"testodf.odt\"");
            response.setContentLength(content.length);
            response.getOutputStream().write(content);
            response.flushBuffer();
            log.trace("Leaving exportDMPODF for DMP [pid: {}] and exportType [{}]", () -> pid, () -> type);
        } catch (Exception e) {
            log.warn("Exception during creating response ", () -> e);
            throw new DWDownloadException("export.error.response");
        }
    }

    /**
     * To check the Internet connection, this function is asynchronously called before saving the DMP Data.
     *
     * @return {@link HttpStatus} OK on success
     */
    @RequestMapping(value = {"/checkConnection"})
    public ResponseEntity<Object> checkConnection() {
        log.trace("checkConnection");
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
