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

/**
 * Controller for capturing and processing Exceptions <br />
 * <br />
 * This file is part of Datawiz.<br />
 *
 * <b>Copyright 2017, Leibniz Institute for Psychology Information (ZPID),
 * <a href="http://zpid.de" title="http://zpid.de">http://zpid.de</a>.</b><br />
 * <br />
 * <a rel="license" href="http://creativecommons.org/licenses/by-nc-sa/4.0/"><img alt="Creative Commons License" style= "border-width:0" src=
 * "https://i.creativecommons.org/l/by-nc-sa/4.0/80x15.png" /></a><br />
 * <span xmlns:dct="http://purl.org/dc/terms/" property="dct:title">Datawiz</span> by
 * <a xmlns:cc="http://creativecommons.org/ns#" href="zpid.de" property="cc:attributionName" rel="cc:attributionURL"> Leibniz Institute for Psychology
 * Information (ZPID)</a> is licensed under a <a rel="license" href="http://creativecommons.org/licenses/by-nc-sa/4.0/">Creative Commons
 * Attribution-NonCommercial-ShareAlike 4.0 International License</a>.
 *
 * @author Ronny Boelter
 * @version 1.0
 */
@ControllerAdvice
public class ExceptionHandlerController {

    private MessageSource messageSource;
    private Environment env;

    private static final String DEFAULT_ERROR_VIEW = "error";
    private static Logger log = LogManager.getLogger(ExceptionHandlerController.class);

    @Autowired
    public ExceptionHandlerController(final MessageSource messageSource, final Environment env) {
        super();
        log.info("Loading ExceptionHandlerController for exception mapping");
        this.messageSource = messageSource;
        this.env = env;
    }

    /**
     * This function captures and processes DWDownloadExceptions and sends a Information about the thrown Exception to the error.jsp
     *
     * @param request {@link HttpServletRequest}
     * @param e       {@link Exception}
     * @return Mapping to the DEFAULT_ERROR_VIEW (error.jsp)
     */
    @ExceptionHandler(value = {DWDownloadException.class})
    public ModelAndView defaultDWDownloadException(HttpServletRequest request, Exception e) {
        log.warn("DWDownloadException catched from[{}] with val[{}] Exception: {}", request::getRequestURL,
                () -> request.getAttribute("exceptionVal"), e::getMessage);
        ModelAndView mav = new ModelAndView(DEFAULT_ERROR_VIEW);
        mav.addObject("errormsg", messageSource.getMessage(e.getMessage(), new Object[]{env.getRequiredProperty("organisation.admin.email")},
                LocaleContextHolder.getLocale()));
        return mav;
    }

    /**
     * This function captures and processes all other exceptions and sends a Information about the thrown Exception to the error.jsp
     *
     * @param request {@link HttpServletRequest}
     * @param e       {@link Exception}
     * @return Mapping to the DEFAULT_ERROR_VIEW (error.jsp)
     */
    @ExceptionHandler(value = {Exception.class})
    public ModelAndView defaultDataAccessResourceFailureException(HttpServletRequest request, Exception e) {
        log.warn("Exception catched from[{}] Exception: ", request::getRequestURL, () -> e);
        ModelAndView mav = new ModelAndView(DEFAULT_ERROR_VIEW);
        mav.addObject("errormsg", e.getMessage());
        return mav;
    }
}
