package de.zpid.datawiz.controller;

import de.zpid.datawiz.dto.UserDTO;
import de.zpid.datawiz.enumeration.PageState;
import de.zpid.datawiz.exceptions.DWDownloadException;
import de.zpid.datawiz.exceptions.DataWizSystemException;
import de.zpid.datawiz.form.ExportProjectForm;
import de.zpid.datawiz.service.ExceptionService;
import de.zpid.datawiz.service.ExportService;
import de.zpid.datawiz.service.ProjectService;
import de.zpid.datawiz.util.BreadCrumbUtil;
import de.zpid.datawiz.util.StringUtil;
import de.zpid.datawiz.util.UserUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;

/**
 * This controller handles all calls to /export/*
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
@RequestMapping(value = "/export")
@SessionAttributes({"breadcrumbList", "ExportProjectForm"})
public class ExportController {

    private static final Logger log = LogManager.getLogger(ExportController.class);
    private final MessageSource messageSource;
    private final ExportService exportService;
    private final ExceptionService exceptionService;
    private final ProjectService projectService;
    private final StringUtil stringUtil;

    @Autowired
    public ExportController(final MessageSource messageSource, final ExportService exportService, final ExceptionService exceptionService,
                            final ProjectService projectService, final StringUtil stringUtil) {
        super();
        log.info("Loading ExportController for mapping /export");
        this.messageSource = messageSource;
        this.exportService = exportService;
        this.exceptionService = exceptionService;
        this.projectService = projectService;
        this.stringUtil = stringUtil;
    }

    /**
     * This function prepares the export data set. Therefore, it loads all studies and records of the project which wants to be exported.
     *
     * @param pid                Project Identifier as {@link Optional}&lt;{@link Long}&gt;
     * @param model              {@link ModelMap}
     * @param redirectAttributes {@link RedirectAttributes}
     * @return Mapping to export.jsp on success, otherwise error mapping via {@link ExceptionService}
     **/
    @RequestMapping(value = {"/{pid}"}, method = RequestMethod.GET)
    public String showExportPage(@PathVariable final Optional<Long> pid, final ModelMap model, final RedirectAttributes redirectAttributes) {
        log.trace("Entering showExportPage for project [id: {}]", () -> pid);
        String ret;
        final UserDTO user = UserUtil.getCurrentUser();
        ret = projectService.checkUserAccess(pid.orElse(0L), 0L, redirectAttributes, false, user);
        if (ret == null) {
            ExportProjectForm exportForm;
            try {
                exportForm = exportService.getExportForm(pid.isPresent() ? pid.get() : 0L, user);
                model.put("breadcrumbList", BreadCrumbUtil.generateBC(PageState.PROJECT, new String[]{exportForm.getProjectTitle()}, null, messageSource));
                model.put("subnaviActive", PageState.EXPORT.name());
                model.put("ExportProjectForm", exportForm);
                ret = "export";
            } catch (DataWizSystemException e) {
                ret = exceptionService.setErrorMessagesAndRedirects(pid, Optional.empty(), Optional.empty(), model, redirectAttributes, e, "ExportController->showExportPage");
            }
        }
        log.trace("Leaving showExportPage for project [id: {}]", () -> pid);
        return ret;
    }

    /**
     * This function exports the selected parts of a project. The export package is created in exportService.createExportFileList.
     *
     * @param exportForm {@link ExportProjectForm}
     * @param pid        Project Identifier as {@link Optional}&lt;{@link Long}&gt;
     * @param model      {@link ModelMap}
     * @param response   {@link HttpServletResponse}
     * @throws Exception DWDownloadException or DataWizSystemException on errors
     */
    @RequestMapping(value = {"", "/{pid}"}, method = RequestMethod.POST, produces = "application/zip")
    public void exportProject(@ModelAttribute("ExportProjectForm") ExportProjectForm exportForm, @PathVariable final Optional<Long> pid, final ModelMap model,
                              final HttpServletResponse response) throws Exception {
        final UserDTO user = UserUtil.getCurrentUser();
        List<Entry<String, byte[]>> files = null;
        if (exportForm != null && exportForm.getProjectId() > 0) {
            try {
                files = exportService.createExportFileList(exportForm, pid.orElse(0L), user);
            } catch (Exception e) {
                if (e instanceof DataWizSystemException) {
                    log.warn("Method exportProject->createExportFileList completed with an error [{}] - DWDownloadException thrown: ",
                            ((DataWizSystemException) e)::getErrorCode, () -> e);
                    throw (e);
                } else {
                    log.error("Exception during EXPORT: ", () -> e);
                    throw new DWDownloadException("export.error.exception.thrown");
                }
            }
        }
        if (files != null) {
            StringBuilder res = new StringBuilder();
            byte[] export = exportService.exportZip(files, res);
            if (export != null) {
                try {
                    response.setContentType("application/zip");
                    response.setHeader("Content-Disposition", "attachment; filename=\"" + stringUtil.formatFilename(exportForm.getProjectTitle()) + ".zip\"");
                    response.getOutputStream().write(export);
                    response.flushBuffer();
                } catch (Exception e) {
                    throw new DWDownloadException("export.error.exception.thrown");
                }
                model.put("breadcrumbList", BreadCrumbUtil.generateBC(PageState.PROJECT, new String[]{exportForm.getProjectTitle()}, null, messageSource));
                model.put("subnaviActive", PageState.EXPORT.name());
            } else {
                log.warn("Method exportProject->exportZip completed with an error - DWDownloadException thrown: ", res.toString());
                throw new DWDownloadException(res.toString());
            }
        } else {
            throw new DWDownloadException("export.error.exception.thrown");
        }
    }

}
