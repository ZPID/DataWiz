package de.zpid.datawiz.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import de.zpid.datawiz.exceptions.DWDownloadException;

@ControllerAdvice
public class ExceptionHandlerController {

  @Autowired
  protected MessageSource messageSource;
  @Autowired
  protected Environment env;

  public static final String DEFAULT_ERROR_VIEW = "error";
  private static Logger log = LogManager.getLogger(ExceptionHandlerController.class);

  @ExceptionHandler(value = { DWDownloadException.class })
  public ModelAndView defaultDWDownloadException(HttpServletRequest request, Exception e) {
    log.warn("DWDownloadException catched from[{}] Exception: {}", () -> request.getRequestURL(), () -> e.getMessage());
    ModelAndView mav = new ModelAndView(DEFAULT_ERROR_VIEW);
    mav.addObject("errormsg", messageSource.getMessage(e.getMessage(),
        new Object[] { env.getRequiredProperty("organisation.admin.email") }, LocaleContextHolder.getLocale()));
    return mav;
  }

  @ExceptionHandler(value = { Exception.class })
  public ModelAndView defaultDataAccessResourceFailureException(HttpServletRequest request, Exception e) {
    log.fatal("Exception catched from[{}] Exception: ", () -> request.getRequestURL(), () -> e);
    ModelAndView mav = new ModelAndView(DEFAULT_ERROR_VIEW);
    mav.addObject("errormsg", e.getMessage());
    return mav;
  }
}
