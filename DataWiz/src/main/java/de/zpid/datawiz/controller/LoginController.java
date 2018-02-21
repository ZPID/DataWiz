package de.zpid.datawiz.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import de.zpid.datawiz.dao.ProjectDAO;
import de.zpid.datawiz.dao.RoleDAO;
import de.zpid.datawiz.dao.UserDAO;
import de.zpid.datawiz.dto.UserDTO;
import de.zpid.datawiz.dto.UserRoleDTO;
import de.zpid.datawiz.enumeration.PageState;
import de.zpid.datawiz.enumeration.Roles;
import de.zpid.datawiz.service.LoginService;
import de.zpid.datawiz.util.BreadCrumpUtil;
import de.zpid.datawiz.util.EmailUtil;
import de.zpid.datawiz.util.UserUtil;

@Controller
@SessionAttributes("UserDTO")
public class LoginController {

	@Autowired
	private PlatformTransactionManager txManager;
	@Autowired
	private EmailUtil mail;
	@Autowired
	private LoginService loginService;
	@Autowired
	private MessageSource messageSource;
	@Autowired
	private ClassPathXmlApplicationContext applicationContext;
	@Autowired
	private Environment env;
	@Autowired
	private HttpServletRequest request;
	@Autowired
	private EmailUtil emailUtil;

	// TODO SERVICE CLASS
	@Autowired
	private ProjectDAO projectDAO;
	@Autowired
	private RoleDAO roleDAO;
	@Autowired
	private UserDAO userDAO;

	private static Logger log = LogManager.getLogger(LoginController.class);

	public LoginController() {
		super();
		if (log.isInfoEnabled())
			log.info("Loading LoginUserController for mapping /login");
	}

	@Autowired
	private PasswordEncoder passwordEncoder;

	@ModelAttribute("UserDTO")
	public UserDTO createUserDTO() {
		return (UserDTO) applicationContext.getBean("UserDTO");
	}

	/**
	 * mapping to "/" to build content outside of the protected areas
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value = { "/", "/home" })
	public String homePage(ModelMap model) {
		if (log.isDebugEnabled()) {
			log.debug("execute homePage()");
		}
		model.put("breadcrumpList", BreadCrumpUtil.generateBC(PageState.INDEX, null, null, messageSource));
		return "welcome";
	}

	/**
	 * Initialize the register form and throws errors to the view if something went wrong during the user login
	 * 
	 * @param error
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String loginPage(@RequestParam(value = "error", required = false) String error, ModelMap model) {
		if (log.isDebugEnabled()) {
			log.debug("execute loginPage()");
		}
		if (error != null) {
			model.put("error", getErrorMessage("SPRING_SECURITY_LAST_EXCEPTION"));
		}
		model.put("breadcrumpList", BreadCrumpUtil.generateBC(PageState.LOGIN, null, null, messageSource));
		return "login";
	}

	/**
	 * Initialize the register form
	 * 
	 * @return
	 */
	@RequestMapping(value = { "/register", "/register/{projectId}/{email}/{linkhash}" }, method = RequestMethod.GET)
	public String registerDataWizUser(ModelMap model, @PathVariable Optional<Long> projectId, @PathVariable Optional<String> email,
	    @PathVariable Optional<String> linkhash) {
		if (log.isTraceEnabled()) {
			log.trace("execute registerDataWizUser()- GET");
		}
		UserDTO admin = UserUtil.getCurrentUser();
		if (admin != null) {
			log.warn("Auth User Object not null - no registration needed - redirect panel");
			return "redirect:/panel";
		}
		if (projectId.isPresent() && email.isPresent() && linkhash.isPresent()) {
			UserDTO user = createUserDTO();
			user.setEmail(email.get());
			user.setSecEmail(email.get());
			user.setComments(String.valueOf(projectId.get()));
			user.setActivationCode(linkhash.get());
			model.put("UserDTO", user);
		} else {
			model.put("UserDTO", createUserDTO());
		}
		model.put("breadcrumpList", BreadCrumpUtil.generateBC(PageState.REGISTER, null, null, messageSource));
		return "register";
	}

	/**
	 * Function for user registration. After the validation of the required form fields, the function saves the new user into the database and sends an email to
	 * the given emailadress to complete registration
	 * 
	 * @param person
	 * @param bindingResult
	 * @param model
	 * @return
	 */
	@RequestMapping(value = { "/register" }, method = RequestMethod.POST)
	public String saveDataWizUser(@Valid @ModelAttribute("UserDTO") UserDTO person, BindingResult bindingResult, ModelMap model) {
		if (log.isTraceEnabled())
			log.trace("execute registerDataWizUser()- POST {}", person);
		try {
			// password check
			if (person.getPassword() == null || person.getPassword_retyped() == null) {
				// TOTO password msg
				bindingResult.rejectValue("password", "passwords.not.equal");
				log.debug("Passwords are emtpy");
			} else if (!person.getPassword().equals(person.getPassword_retyped())) {
				bindingResult.rejectValue("password", "passwords.not.equal");
				if (log.isDebugEnabled())
					log.debug("Password and retyped password not equal!");
			}
			// GTC(AGB) check
			if (!person.isCheckedGTC()) {
				bindingResult.rejectValue("checkedGTC", "register.gtc.net.set");
				if (log.isDebugEnabled())
					log.debug("Email is already used for an account email:" + person.getEmail());
			}
			// Email exists check
			if (userDAO.findByMail(person.getEmail(), false) != null) {
				bindingResult.rejectValue("email", "email.already.exists");
				if (log.isDebugEnabled())
					log.debug("Email is already used for an account email:" + person.getEmail());
			}
			if (emailUtil.isFakeMail(person.getEmail()))
				bindingResult.rejectValue("email", "error.email.fake");
			// return to registerform if errors
			if (bindingResult.hasErrors()) {
				log.trace("UserDTO has Errors - return to register form");
				model.put("breadcrumpList", BreadCrumpUtil.generateBC(PageState.REGISTER, null, null, messageSource));
				return "register";
			}
			// at this point registerform is valid
			String chkmail = person.getSecEmail();
			long projectId = 0;
			try {
				projectId = (person.getComments() != null && !person.getComments().isEmpty()) ? Long.parseLong(person.getComments()) : -1;
				person.setComments(null);
			} catch (Exception e) {
				log.debug("ProjectId which is temporary stored in comments is not a number : {}", person.getComments());
			}
			if (chkmail != null && !chkmail.isEmpty() && projectId > 0) {
				person.setSecEmail(null);
				if (!chkmail.equals(person.getEmail())) {
					projectDAO.updateInvitationEntity(projectId, chkmail, person.getEmail());
				}
			}
			person.setPassword(passwordEncoder.encode(person.getPassword()));
			userDAO.saveOrUpdate(person, false);
			person = userDAO.findByMail(person.getEmail(), false);
		} catch (Exception e) {
			log.error("DBS error during user registration: ", () -> e);
			model.put("errormsg", messageSource.getMessage("dbs.sql.exception", null, LocaleContextHolder.getLocale()));
			return "error";
		}
		// registration mail
		if (person != null && person.getId() > 0) {
			try {
				mail.sendSSLMail(person.getEmail(), messageSource.getMessage("reg.mail.subject", null, LocaleContextHolder.getLocale()),
				    messageSource.getMessage("reg.mail.content", new Object[] {
				        request.getRequestURL().toString().replace(request.getRequestURI(), request.getContextPath()), person.getEmail(), person.getActivationCode() },
				        LocaleContextHolder.getLocale()));
			} catch (Exception e) {
				log.error("Mail error during user registration: ", () -> e);
				model.put("errormsg", messageSource.getMessage("send.mail.exception", null, LocaleContextHolder.getLocale()));
				return "error";
			}
		}
		return "redirect:/login?activationmail";
	}

	/**
	 * Activation endpoint which needs the email of the account which has to be activated and a random generated UUID to authenticate that mail address
	 * 
	 * @param mail
	 * @param activationCode
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/activate/{mail}/{activationCode}", method = RequestMethod.GET)
	public String activateAccount(@PathVariable final String mail, @PathVariable final String activationCode, ModelMap model) {
		if (log.isTraceEnabled()) {
			log.trace("execute activateAccount email: " + mail + " code: " + activationCode);
		}
		TransactionStatus status = txManager.getTransaction(new DefaultTransactionDefinition());
		try {
			UserDTO user = userDAO.findByMail(mail, false);
			if (user != null && user.getActivationCode() != null && !user.getActivationCode().isEmpty() && user.getActivationCode().equals(activationCode)) {
				userDAO.activateUserAccount(user);
				roleDAO.saveRole(new UserRoleDTO(Roles.USER.toInt(), user.getId(), 0, 0, Roles.USER.name()));
				long pid = Long.parseLong(projectDAO.findValFromInviteData(mail, activationCode, "project_id"));
				if (pid > 0) {
					UserRoleDTO role = new UserRoleDTO(Roles.REL_ROLE.toInt(), user.getId(), pid, 0, Roles.REL_ROLE.name());
					roleDAO.saveRole(role);
					projectDAO.deleteInvitationEntity(pid, mail);
				}
			}
			txManager.commit(status);
		} catch (Exception e) {
			txManager.rollback(status);
			log.warn("DBS error during user registration: ", () -> e);
			model.put("errormsg", messageSource.getMessage("login.failed", null, LocaleContextHolder.getLocale()));
			return "error";
		}
		return "redirect:/login?activated";
	}

	/**
	 * This mapping is used if unauthenticated users try to access protected areas
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/Access_Denied")
	public String accessDeniedPage(ModelMap model) {
		if (log.isTraceEnabled()) {
			log.trace("execute accessDeniedPage() - " + request.getHeader("referer") + " - " + request.getAuthType() + " - " + request.getPathInfo());
		}
		try {
			model.addAttribute("user", getPrincipal());
		} catch (Exception e) {
			return "redirect:/login";
		}
		return "accessDenied";
	}

	@RequestMapping(value = "/login/passwordrequest", method = RequestMethod.GET)
	public String requestResetPassword(ModelMap model) {
		if (log.isTraceEnabled()) {
			log.trace("execute requestResetPassword");
		}
		model.put("setemailview", true);
		model.put("UserDTO", createUserDTO());
		return "password";
	}

	@RequestMapping(value = "/login/passwordrequest", method = RequestMethod.POST)
	public String sendPasswordResetRequest(@ModelAttribute("UserDTO") UserDTO person, ModelMap model) {
		if (log.isTraceEnabled()) {
			log.trace("execute requestResetPasswordSubmit");
		}
		String ret = "password";
		String retErr = loginService.sendPasswordRecoveryMail(person, request);
		if (retErr != null && retErr.equals("dbs.sql.exception")) {
			ret = "error";
			model.put("errormsg",
			    messageSource.getMessage(retErr, new Object[] { env.getRequiredProperty("organisation.admin.email"), "" }, LocaleContextHolder.getLocale()));
		} else if (retErr != null && (retErr.equals("reset.password.no.secemail") || retErr.equals("reset.password.email.send"))) {
			model.put("successMSG", messageSource.getMessage(retErr, new Object[] { person != null ? person.getEmail() : "" }, LocaleContextHolder.getLocale()));
			model.put("setemailview", true);
			model.put("sendSuccess", true);
		} else {
			model.put("errorMSG", messageSource.getMessage(retErr,
			    new Object[] { person != null ? person.getEmail() : "", env.getRequiredProperty("organisation.admin.email"), "" }, LocaleContextHolder.getLocale()));
			model.put("setemailview", true);
		}
		return ret;
	}

	@RequestMapping(value = { "/login/resetpwd/{email}/{code}" })
	public String showSetPassword(ModelMap model, @PathVariable final Optional<String> email, @PathVariable final Optional<String> code) {
		if (log.isTraceEnabled()) {
			log.trace("execute showSetPassword for user: [{}]", () -> email.isPresent() ? email.get() : "null");
		}
		String ret = "password";
		String retErr = loginService.setPasswordResetForm(email, code);
		if (retErr == null || retErr.equals("reset.password.email.link.success")) {
			model.put("successMSG", messageSource.getMessage(retErr, null, LocaleContextHolder.getLocale()));
			model.put("setemailview", false);
		} else if (retErr.equals("dbs.sql.exception")) {
			ret = "error";
			model.put("errormsg",
			    messageSource.getMessage(retErr, new Object[] { env.getRequiredProperty("organisation.admin.email"), "" }, LocaleContextHolder.getLocale()));
		} else {
			model.put("errorMSG", messageSource.getMessage(retErr, null, LocaleContextHolder.getLocale()));
			model.put("setemailview", true);
			model.put("sendSuccess", true);
		}
		return ret;
	}

	@RequestMapping(value = "/login/passwordrequest", method = RequestMethod.POST, params = "setPassword")
	public String saveNewPassword(@ModelAttribute("UserDTO") final UserDTO person, final ModelMap model, final RedirectAttributes redirectAttributes) {
		if (log.isTraceEnabled()) {
			log.trace("execute saveNewPassword");
		}
		List<String> retMSG = new ArrayList<>();
		String ret = loginService.validateAndSavePassword(person, retMSG);
		if (ret.equals("redirect:/login")) {
			redirectAttributes.addFlashAttribute("successMSG",
			    messageSource.getMessage(retMSG.get(0), new Object[] { person.getEmail() }, LocaleContextHolder.getLocale()));
		} else if (ret.equals("error")) {
			model.put("errormsg",
			    messageSource.getMessage("dbs.sql.exception",
			        new Object[] { env.getRequiredProperty("organisation.admin.email"), retMSG.get(0).replaceAll("\n", "").replaceAll("\"", "\'") },
			        LocaleContextHolder.getLocale()));
		} else {
			model.put("setemailview", false);
			model.put("errorMSG", messageSource.getMessage(retMSG.get(0), null, LocaleContextHolder.getLocale()));
		}
		return ret;
	}

	/**
	 * Checks out the currently authenticated user from the Spring security SecurityContextLogoutHandler and deletes the remember-me cookie
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/logout")
	public String logout(ModelMap model, HttpServletResponse response) {
		if (log.isTraceEnabled()) {
			log.trace("execute logoutPage()");
		}
		String cookieName = "remember-me";
		Cookie cookie = new Cookie(cookieName, null);
		cookie.setMaxAge(0);
		cookie.setPath(StringUtils.hasLength(request.getContextPath()) ? request.getContextPath() : "/");
		response.addCookie(cookie);
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null) {
			new SecurityContextLogoutHandler().logout(request, response, auth);
		}
		model.put("UserDTO", createUserDTO());
		return "redirect:/login?logout";
	}

	/**
	 * Returns the name of the currently authenticated User
	 * 
	 * @return
	 */
	private String getPrincipal() {
		String userName = null;
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (principal instanceof UserDetails) {
			userName = ((UserDetails) principal).getUsername();
		} else {
			userName = principal.toString();
		}
		return userName;
	}

	/**
	 * Returns custom messages for Login Exceptions - Checks BadCredentialsException, LockedException, AccountExpiredException and
	 * InternalAuthenticationServiceException. Input String
	 * 
	 * @param key
	 *          SessionParameterKey
	 * @return Custom ErrorMessage
	 */
	private String getErrorMessage(String key) {
		Exception exception = (Exception) request.getSession().getAttribute(key);
		String error = "";
		if (exception instanceof BadCredentialsException) {
			error = messageSource.getMessage("login.failed", new Object[] { env.getRequiredProperty("organisation.admin.email") }, LocaleContextHolder.getLocale());
		} else if (exception instanceof LockedException) {
			error = messageSource.getMessage("login.locked", new Object[] { env.getRequiredProperty("organisation.admin.email") }, LocaleContextHolder.getLocale());
		} else if (exception instanceof AccountExpiredException) {
			error = messageSource.getMessage("login.expired", new Object[] { env.getRequiredProperty("organisation.admin.email") }, LocaleContextHolder.getLocale());
		} else if (exception instanceof InternalAuthenticationServiceException) {
			error = messageSource.getMessage("login.system.error", new Object[] { env.getRequiredProperty("organisation.admin.email") },
			    LocaleContextHolder.getLocale());
		} else {
			error = messageSource.getMessage("login.failed", new Object[] { env.getRequiredProperty("organisation.admin.email") }, LocaleContextHolder.getLocale());
		}
		return error;
	}
}