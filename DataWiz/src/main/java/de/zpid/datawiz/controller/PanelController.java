package de.zpid.datawiz.controller;

import de.zpid.datawiz.dto.UserDTO;
import de.zpid.datawiz.enumeration.PageState;
import de.zpid.datawiz.exceptions.DataWizSystemException;
import de.zpid.datawiz.form.ProjectForm;
import de.zpid.datawiz.service.PanelService;
import de.zpid.datawiz.util.BreadCrumbUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This controller handles all calls to /panel/*
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
 * <p>
 **/
@Controller
@RequestMapping(value = "/panel")
public class PanelController {


    private static final Logger log = LogManager.getLogger(PanelController.class);
    private final MessageSource messageSource;
    private final PanelService panelService;
    private final Environment env;

    /**
     * Instantiates a new panel controller.
     */
    @Autowired
    public PanelController(MessageSource messageSource, PanelService panelService, Environment env) {
        super();
        log.info("Loading PanelController for mapping /panel");
        this.messageSource = messageSource;
        this.panelService = panelService;
        this.env = env;
    }


    /**
     * This function handles the calls to /panel. Depending on the access rights for the user who has called the panel, it loads only the content which
     * the user has the appropriate rights for.
     *
     * @param model {@link ModelMap}
     * @return mapping to panel on success
     */
    @RequestMapping(method = RequestMethod.GET)
    public String getPanel(ModelMap model) {
        log.trace("Entering getPanel");
        List<ProjectForm> pFormList;
        String ret;
        try {
            UserDTO user = panelService.refreshAndGetUserDTO();
            final AtomicBoolean parChk = new AtomicBoolean(false);
            pFormList = panelService.getProjects(user, parChk);
            if (parChk.get()) {
                model.put("errorMSG", "");
            }
            model.put("breadcrumbList", BreadCrumbUtil.generateBC(PageState.PANEL, null, null, messageSource));
            model.put("CProjectForm", pFormList);
            ret = "panel";
        } catch (Exception e) {
            if (e instanceof DataWizSystemException) {
                log.warn("DataWizSystemException [{}] during getPanel ", ((DataWizSystemException) e)::getErrorCode, () -> e);
                ret = "redirect:/login";
            } else {
                log.fatal("Exception during getPanel", () -> e);
                model.put("errormsg",
                        messageSource.getMessage("dbs.sql.exception",
                                new Object[]{env.getRequiredProperty("organisation.admin.email"), e.getMessage().replaceAll("\n", "").replaceAll("\"", "\'")},
                                LocaleContextHolder.getLocale()));
                ret = "error";
            }

        }
        log.trace("Leaving getPanel with mapping [{}]", ret);
        return ret;
    }


}
