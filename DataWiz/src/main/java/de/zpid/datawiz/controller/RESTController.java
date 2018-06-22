package de.zpid.datawiz.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

import de.zpid.datawiz.dao.RoleDAO;
import de.zpid.datawiz.dao.SideMenuDAO;
import de.zpid.datawiz.dto.SideMenuDTO;
import de.zpid.datawiz.dto.UserDTO;
import de.zpid.datawiz.dto.UserRoleDTO;
import de.zpid.datawiz.enumeration.Roles;
import de.zpid.datawiz.util.UserUtil;


/**
 * This controller handles all calls to /api/*
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
@RestController
@RequestMapping("/api")
public class RESTController {

    private static Logger log = LogManager.getLogger(RESTController.class);

    @Autowired
    private SideMenuDAO sideMenuDAO;
    @Autowired
    private RoleDAO roleDAO;
    @Autowired
    private MessageSource messageSource;

    @RequestMapping(value = "/sideMenu", method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public String getProjectList() {
        log.trace("execute loadSideMenu()");
        UserDTO user = UserUtil.getCurrentUser();
        SideMenuForm sdForm = new SideMenuForm();
        if (user != null) {
            try {
                List<SideMenuDTO> cpdto = null;
                cpdto = sideMenuDAO.findProjectsByUser(user);
                for (SideMenuDTO project : cpdto) {
                    if (user.hasRole(Roles.ADMIN) || user.hasRole(Roles.PROJECT_ADMIN, project.getId(), false)
                            || user.hasRole(Roles.PROJECT_READER, project.getId(), false) || user.hasRole(Roles.PROJECT_WRITER, project.getId(), false)) {
                        List<SideMenuDTO> cStud = sideMenuDAO.findAllStudiesByProjectId(project.getId());
                        cStud.parallelStream().forEach(study -> {
                            try {
                                study.setSublist(sideMenuDAO.findRecordsWithStudyID(study.getId()));
                            } catch (Exception e) {
                                log.debug("Exception during cStud.parallelStream().forEach: ", () -> e);
                            }
                        });
                        project.setSublist(cStud);
                    } else if (user.hasRole(Roles.DS_READER, project.getId(), false) || user.hasRole(Roles.DS_WRITER, project.getId(), false)) {
                        List<UserRoleDTO> userRoles = roleDAO.findRolesByUserIDAndProjectID(user.getId(), project.getId());
                        List<SideMenuDTO> cStud = new ArrayList<SideMenuDTO>();
                        userRoles.parallelStream().forEach(role -> {
                            Roles uRole = Roles.valueOf(role.getType());
                            if (role.getStudyId() > 0 && (uRole.equals(Roles.DS_READER) || uRole.equals(Roles.DS_WRITER))) {
                                try {
                                    SideMenuDTO smdto = sideMenuDAO.findById(role.getStudyId(), role.getProjectId());
                                    smdto.setSublist(sideMenuDAO.findRecordsWithStudyID(smdto.getId()));
                                    cStud.add(smdto);
                                } catch (Exception e) {
                                    log.debug("Exception during userRoles.parallelStream().forEach: ", () -> e);
                                }
                            }
                        });
                        project.setSublist(cStud);
                    }
                }
                sdForm.setItems(cpdto);
                sdForm.setLinkProject(messageSource.getMessage("submenu.project", null, LocaleContextHolder.getLocale()));
                sdForm.setLinkDmp(messageSource.getMessage("submenu.dmp", null, LocaleContextHolder.getLocale()));
                sdForm.setLinkStudies(messageSource.getMessage("project.submenu.studies", null, LocaleContextHolder.getLocale()));
                sdForm.setLinkProMat(messageSource.getMessage("project.submenu.material", null, LocaleContextHolder.getLocale()));
                sdForm.setLinkContri(messageSource.getMessage("submenu.sharing", null, LocaleContextHolder.getLocale()));
                sdForm.setLinkExport(messageSource.getMessage("submenu.export", null, LocaleContextHolder.getLocale()));
                sdForm.setLinkStudy(messageSource.getMessage("submenu.studydoc", null, LocaleContextHolder.getLocale()));
                sdForm.setLinkRecords(messageSource.getMessage("submenu.record", null, LocaleContextHolder.getLocale()));
                sdForm.setLinkStudMat(messageSource.getMessage("submenu.studymaterial", null, LocaleContextHolder.getLocale()));
                sdForm.setLinkRecord(messageSource.getMessage("record.submenu.meta", null, LocaleContextHolder.getLocale()));
                sdForm.setLinkCodebook(messageSource.getMessage("record.submenu.var", null, LocaleContextHolder.getLocale()));
                sdForm.setLinkMatrix(messageSource.getMessage("record.submenu.data", null, LocaleContextHolder.getLocale()));
            } catch (Exception e) {
                log.warn("Exception during getProjectList - Sidemenu not loaded: Exception: ", () -> e);
            }
        } else {
            log.debug("No active user found - Sidemenu is loaded after sucessful login");
        }
        return new Gson().toJson(sdForm);
    }

    public class SideMenuForm {
        private String linkProject;
        private String linkDmp;
        private String linkStudies;
        private String linkProMat;
        private String linkContri;
        private String linkExport;
        private String linkStudy;
        private String linkRecords;
        private String linkStudMat;
        private String linkRecord;
        private String linkCodebook;
        private String linkMatrix;

        private List<SideMenuDTO> items;

        public String getLinkProject() {
            return linkProject;
        }

        public void setLinkProject(String linkProject) {
            this.linkProject = linkProject;
        }

        public String getLinkDmp() {
            return linkDmp;
        }

        public void setLinkDmp(String linkDmp) {
            this.linkDmp = linkDmp;
        }

        public String getLinkStudies() {
            return linkStudies;
        }

        public void setLinkStudies(String linkStudies) {
            this.linkStudies = linkStudies;
        }

        public String getLinkProMat() {
            return linkProMat;
        }

        public void setLinkProMat(String linkProMat) {
            this.linkProMat = linkProMat;
        }

        public String getLinkContri() {
            return linkContri;
        }

        public void setLinkContri(String linkContri) {
            this.linkContri = linkContri;
        }

        public String getLinkExport() {
            return linkExport;
        }

        public void setLinkExport(String linkExport) {
            this.linkExport = linkExport;
        }

        public String getLinkStudy() {
            return linkStudy;
        }

        public void setLinkStudy(String linkStudy) {
            this.linkStudy = linkStudy;
        }

        public String getLinkRecords() {
            return linkRecords;
        }

        public void setLinkRecords(String linkRecords) {
            this.linkRecords = linkRecords;
        }

        public String getLinkStudMat() {
            return linkStudMat;
        }

        public void setLinkStudMat(String linkStudMat) {
            this.linkStudMat = linkStudMat;
        }

        public String getLinkRecord() {
            return linkRecord;
        }

        public void setLinkRecord(String linkRecord) {
            this.linkRecord = linkRecord;
        }

        public String getLinkCodebook() {
            return linkCodebook;
        }

        public void setLinkCodebook(String linkCodebook) {
            this.linkCodebook = linkCodebook;
        }

        public String getLinkMatrix() {
            return linkMatrix;
        }

        public void setLinkMatrix(String linkMatrix) {
            this.linkMatrix = linkMatrix;
        }

        public List<SideMenuDTO> getItems() {
            return items;
        }

        public void setItems(List<SideMenuDTO> items) {
            this.items = items;
        }

    }

}
