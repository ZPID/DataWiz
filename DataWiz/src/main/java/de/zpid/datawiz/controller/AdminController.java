package de.zpid.datawiz.controller;

import java.util.LinkedList;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;

import de.zpid.datawiz.dao.UserDAO;
import de.zpid.datawiz.service.AdminService;
import de.zpid.datawiz.util.BreadCrump;

@Controller
@RequestMapping(value = "/admin")
public class AdminController {

	@Autowired
	private AdminService adminService;
	@Autowired
	private UserDAO userDAO;

	private static Logger log = LogManager.getLogger(AdminController.class);

	public AdminController() {
		super();
		if (log.isInfoEnabled())
			log.info("Loading AdminController for mapping /admin");
	}

	@RequestMapping(method = RequestMethod.GET)
	public String handleGet(ModelMap model) {
		log.trace("execute adminPage()");
		model.put("userCount", adminService.countValuesByTableName("dw_user"));
		model.put("projectCount", adminService.countValuesByTableName("dw_project"));
		model.put("studyCount", adminService.countValuesByTableName("dw_study"));
		model.put("recordCount", adminService.countValuesByTableName("dw_record"));
		model.put("versionCount", adminService.countValuesByTableName("dw_record_metadata"));
		model.put("breadcrumpList", new LinkedList<BreadCrump>());
		return "admin/admin";
	}

	@RequestMapping(value = { "/list/{type}/{id}", "/list/{type}" }, method = RequestMethod.GET)
	public String showLists(final ModelMap model, @PathVariable final String type, @PathVariable final Optional<Integer> id) {
		log.trace("execute adminPage()");
		if (type.equals("user")) {
			model.put("userlist", adminService.getList(type, null));
		} else if (type.equals("project")) {
			model.put("userlist", adminService.getList("user", null));
			model.put("projectlist", adminService.getList(type, id));
			model.put("studylist", adminService.getList(type, id));
		} else if (type.equals("study")) {
			model.put("userlist", adminService.getList("user", null));
			model.put("studylist", adminService.getList(type, id));
		}
		model.put("tabletype", type);
		model.put("breadcrumpList", new LinkedList<BreadCrump>());
		return "admin/table";
	}

	@RequestMapping(value = { "/detail/{type}/{id}" }, method = RequestMethod.GET, produces = "application/json;charset=UTF-8")
	public @ResponseBody String showDetail(final ModelMap model, @PathVariable final String type, @PathVariable final Optional<Integer> id) {
		log.trace("execute showDetail");
		String json = "{}";
		try {
			if (id.isPresent())
				json = new Gson().toJson(userDAO.findById(id.get()));
			else
				json = "{\"error\":\"\"}";
		} catch (Exception e) {

		}
		return json;
	}

	@RequestMapping(value = { "/save/user" })
	public String saveUser(@RequestParam(value = "modal_uid", required = true) long id, @RequestParam(value = "modal_title", required = false) String title,
	    @RequestParam(value = "modal_first_name", required = false) String firstName, @RequestParam(value = "modal_last_name", required = false) String lastName,
	    @RequestParam(value = "modal_email", required = false) String email, @RequestParam(value = "modal_sec_email", required = false) String secEmail,
	    @RequestParam(value = "modal_password", required = false) String password,
	    @RequestParam(value = "modal_account_state", required = false) String accountState) {
		log.trace("execute saveUser: [id: {}; mail: {}}", () -> id, () -> email);
		try {
			adminService.setAndUpdateUser(id, title, firstName, lastName, email, secEmail, password, accountState);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "redirect:/admin/list/user";
	}

}
