package de.zpid.datawiz.controller;

import com.google.gson.Gson;
import de.zpid.datawiz.dao.RoleDAO;
import de.zpid.datawiz.dao.UserDAO;
import de.zpid.datawiz.dto.UserDTO;
import de.zpid.datawiz.enumeration.Roles;
import de.zpid.datawiz.service.AdminService;
import de.zpid.datawiz.util.BreadCrump;
import de.zpid.datawiz.util.UserUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * This controller handles all calls to /admin/*
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
@RequestMapping(value = "/admin")
public class AdminController {

    private static final Logger log = LogManager.getLogger(AdminController.class);
    private final AdminService adminService;
    private final UserDAO userDAO;
    private final RoleDAO roleDAO;

    @Autowired
    private AdminController(AdminService adminService, UserDAO userDAO, RoleDAO roleDAO) {
        super();
        log.info("Loading AdminController for mapping /admin");
        this.userDAO = userDAO;
        this.adminService = adminService;
        this.roleDAO = roleDAO;
    }

    /**
     * This function is called by entering the index page of DataWiz Administration and provides the counts for the statistics
     *
     * @param model {@link ModelMap}
     * @return Mapping to admin.jsp
     */
    @RequestMapping(method = RequestMethod.GET)
    public String handleGet(ModelMap model) {
        log.trace("execute adminPage()");
        model.put("userCount", adminService.countValuesByTableName("dw_user"));
        model.put("projectCount", adminService.countValuesByTableName("dw_project"));
        model.put("studyCount", adminService.countValuesByTableName("dw_study"));
        model.put("recordCount", adminService.countValuesByTableName("dw_record"));
        model.put("versionCount", adminService.countValuesByTableName("dw_record_metadata"));
        model.put("breadcrumbList", new LinkedList<BreadCrump>());
        return "admin/admin";
    }

    /**
     * This function provides table data by the passed type and id and sends them to the view (admin/table.jsp)
     *
     * @param model {@link ModelMap}
     * @param type  Table type identifier as {@link String}
     * @param id    User, Project or Study Identifier which depends on the table type, as {@link Optional}&lt;{@link Long}&gt;
     * @return Mapping to admin/table.jsp
     */
    @RequestMapping(value = {"/list/{type}/{id}", "/list/{type}"}, method = RequestMethod.GET)
    public String showLists(final ModelMap model, @PathVariable final String type, @PathVariable final Optional<Integer> id) {
        log.trace("execute adminPage()");
        if (UserUtil.getCurrentUser().hasRole(Roles.ADMIN)) {
            switch (type) {
                case "user":
                    List<?> users = adminService.getList(type, 0);
                    model.put("userlist", users);
                    break;
                case "project":
                    model.put("userlist", adminService.getList("user", 0));
                    model.put("projectlist", adminService.getList(type, id.isPresent() ? id.get() : 0));
                    break;
                case "study":
                    model.put("userlist", adminService.getList("user", 0));
                    model.put("studylist", adminService.getList(type, id.isPresent() ? id.get() : 0));
                    break;
            }
            model.put("tabletype", type);
        }
        model.put("breadcrumbList", new LinkedList<BreadCrump>());
        return "admin/table";
    }


    /**
     * This function is called asynchronously by opening a user-detail modal.
     *
     * @param id   User identifier as {@link Long}
     * @param type Type identifier as {@link String} (At the moment only "user" is supported)
     * @return JSON, with detail-data on success
     */
    @RequestMapping(value = {"/detail/{type}/{id}"}, method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
    public @ResponseBody
    String showDetail(@PathVariable final Long id, @PathVariable final String type) {
        log.trace("execute showDetail for [type: {}; id: {}]", () -> type, () -> id);
        String json = "{}";
        if (UserUtil.getCurrentUser().hasRole(Roles.ADMIN)) {
            try {
                if (type.equals("user")) {
                    UserDTO user = userDAO.findById(id);
                    int projectCount = adminService.countProjectsByUser(user.getId());
                    json = new Gson().toJson(new Object[]{user, projectCount});
                }
            } catch (Exception e) {
                log.warn("DB-Exception thrown during loading details from database: {}", e::getMessage);
                json = "{\"error\":\"\"}";
            }
        } else {
            json = "{\"error\":\"\"}";
        }
        return json;
    }

    /**
     * This function is called from the admin panel if an admin. It saves edited user details.
     * TODO Rolle(1) setzen falls der Nutzer noch keine hat
     *
     * @param id           User identifier as {@link Long}
     * @param title        User's title as {@link String}
     * @param firstName    User's first name as {@link String}
     * @param lastName     User's last name as {@link String}
     * @param email        User's email as {@link String}
     * @param secEmail     User's secondary email as {@link String}
     * @param password     User's (new) password as {@link String}
     * @param accountState User's account as {@link String}
     * @return Redirect to admin/list/detail on success
     */
    @RequestMapping(value = {"/save/user"}, params = {"saveUser"}, method = RequestMethod.POST)
    public String saveUser(@RequestParam(value = "modal_uid") long id, @RequestParam(value = "modal_title", required = false) String title,
                           @RequestParam(value = "modal_first_name", required = false) String firstName, @RequestParam(value = "modal_last_name", required = false) String lastName,
                           @RequestParam(value = "modal_email", required = false) String email, @RequestParam(value = "modal_sec_email", required = false) String secEmail,
                           @RequestParam(value = "modal_password", required = false) String password,
                           @RequestParam(value = "modal_account_state", required = false) String accountState, final RedirectAttributes redirectAttributes) {
        log.trace("execute saveUser: [id: {}; mail: {}}", () -> id, () -> email);
        if (UserUtil.getCurrentUser().hasRole(Roles.ADMIN)) {
            try {
                adminService.setAndUpdateUser(id, title, firstName, lastName, email, secEmail, password, accountState);
                redirectAttributes.addFlashAttribute("successMSG", "User [" + email + "] successfully edited");
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("errorMSG", "Database Error during saveUser");
                log.fatal("DB-Exception thrown during saving user details: {}", e::getMessage);
            }
        }
        return "redirect:/admin/list/user";
    }


    @RequestMapping(value = {"/save/user"}, params = {"deleteUser"}, method = RequestMethod.POST)
    public String deleteUser(final RedirectAttributes redirectAttributes, @RequestParam(value = "modal_uid") long id) {
        log.trace("Entering deleteUser [id: {}}", () -> id);
        UserDTO admin = UserUtil.getCurrentUser();
        if (admin.hasRole(Roles.ADMIN)) {
            try {
                UserDTO user = userDAO.findById(id);
                if (user.hasRole(Roles.ADMIN))
                    redirectAttributes.addFlashAttribute("errorMSG", "It is not allowed to delete administrator accounts");
                else {
                    if (user.getGlobalRoles().size() <= 1) {
                        if (user.getGlobalRoles().size() == 1 && user.getGlobalRoles().get(0).getType().equals(Roles.USER.name()))
                            user.getGlobalRoles().forEach(roleDAO::deleteRole);
                        userDAO.deleteUser(user);
                        redirectAttributes.addFlashAttribute("successMSG", "User [" + user.getEmail() + "] successfully deleted");
                    } else {
                        redirectAttributes.addFlashAttribute("errorMSG", "The user is still linked to existing projects and cannot be deleted. " +
                                " You have to dissolve these relationships before you finally delete the user.");
                    }
                }
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("errorMSG", "Database Error during deleteUser");
                log.fatal("DB-Exception thrown during deleting user from database: {}", e::getMessage);
            }
        } else {
            log.fatal("User [id: {}; email: {}] without Administration role tries to delete User [id: {}]", admin::getId, admin::getEmail, () -> id);
        }
        log.trace("Leaving deleteUser [id: {}}", () -> id);
        return "redirect:/admin/list/user";
    }

}
