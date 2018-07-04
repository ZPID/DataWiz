package de.zpid.datawiz.controller;

import de.zpid.datawiz.exceptions.DWDownloadException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.env.Environment;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * This controller handles all not intercepted exceptions
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
@ControllerAdvice
public class ExceptionHandlerController {

    private final MessageSource messageSource;
    private final Environment env;

    private static final String DEFAULT_ERROR_VIEW = "error";
    private static final Logger log = LogManager.getLogger(ExceptionHandlerController.class);

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
        log.error("Exception thrown from[{}] Exception: ", request::getRequestURL, () -> e);
        ModelAndView mav = new ModelAndView(DEFAULT_ERROR_VIEW);
        if (e instanceof HttpRequestMethodNotSupportedException) {
            mav.addObject("errormsg", "You have tried to access a page that does not exist. If this is unwanted behavior, please contact us. "
                    + env.getRequiredProperty("organisation.admin.email"));
        } else {
            mav.addObject("errormsg", e.getMessage());
        }
        return mav;
    }
}
