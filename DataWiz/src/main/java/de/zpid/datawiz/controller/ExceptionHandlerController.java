package de.zpid.datawiz.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import de.zpid.datawiz.exceptions.DWDownloadException;

@ControllerAdvice
public class ExceptionHandlerController {

  public static final String DEFAULT_ERROR_VIEW = "error";
  private static Logger log = LogManager.getLogger(ExceptionHandlerController.class);

  @ExceptionHandler(value = { DWDownloadException.class })
  public ModelAndView defaultErrorHandler(HttpServletRequest request, Exception e) {
    log.warn("DWDownloadException catched from[{}] Exception: {}", () -> request.getRequestURL(), () -> e.getMessage());
    ModelAndView mav = new ModelAndView(DEFAULT_ERROR_VIEW);
    mav.addObject("errormsg", "asdasd");
    return mav;
  }
}
