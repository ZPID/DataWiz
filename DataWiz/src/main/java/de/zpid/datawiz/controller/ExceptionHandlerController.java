package de.zpid.datawiz.controller;

import de.zpid.datawiz.exceptions.DWDownloadException;
import de.zpid.datawiz.util.ClientInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

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
    private final ClientInfo clientInfo;

    private static final String DEFAULT_ERROR_VIEW = "error";
    private static final Logger log = LogManager.getLogger(ExceptionHandlerController.class);

    @Autowired
    public ExceptionHandlerController(final MessageSource messageSource, final Environment env, final ClientInfo clientInfo) {
        super();
        log.info("Loading ExceptionHandlerController for exception mapping");
        this.messageSource = messageSource;
        this.env = env;
        this.clientInfo = clientInfo;
    }

    /**
     * This function captures and processes all 404 Exceptions and sends a Information about the thrown Exception to the error.jsp
     *
     * @param request {@link HttpServletRequest}
     * @param ex      {@link NoHandlerFoundException}
     * @return Mapping to the DEFAULT_ERROR_VIEW (error.jsp)
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ModelAndView default404Exception(HttpServletRequest request, NoHandlerFoundException ex) {
        HashMap<String, String> clientInfo = this.clientInfo.getClientInfo(request);
        log.warn("404 NOT_FOUND Exception: [{}] Client-Information: [{}]", () -> ex, () -> clientInfo);
        ModelAndView mav = new ModelAndView(DEFAULT_ERROR_VIEW);
        String ref = clientInfo.get("Referrer");
        if (ref != null && !ref.isEmpty() && ref.contains(env.getRequiredProperty("application.url")))
            mav.addObject("referrerURL", ref);
        else
            mav.addObject("referrerURL", env.getRequiredProperty("application.url"));
        mav.addObject("exceptionTitle", messageSource.getMessage("error.404.title", null, LocaleContextHolder.getLocale()));
        mav.addObject("errormsg", messageSource.getMessage("error.404.msg", new Object[]{env.getRequiredProperty("organisation.admin.email")}, LocaleContextHolder.getLocale()));
        mav.addObject("exception", printExceptionMsg(ex));
        return mav;
    }

    /**
     * This function captures and processes DWDownloadExceptions and sends a Information about the thrown Exception to the error.jsp
     *
     * @param request {@link HttpServletRequest}
     * @param e       {@link DWDownloadException}
     * @return Mapping to the DEFAULT_ERROR_VIEW (error.jsp)
     */
    @ExceptionHandler(DWDownloadException.class)
    public ModelAndView defaultDWDownloadException(HttpServletRequest request, DWDownloadException e) {
        log.warn("DWDownloadException catched from[{}] with val[{}] Exception: [{}],  Client-Information: [{}]", request::getRequestURL,
                () -> request.getAttribute("exceptionVal"), e::getMessage, () -> this.clientInfo.getClientInfo(request));
        ModelAndView mav = new ModelAndView(DEFAULT_ERROR_VIEW);
        mav.addObject("closePageBTN", true);
        mav.addObject("exceptionTitle", messageSource.getMessage("error.download.title", null, LocaleContextHolder.getLocale()));
        try {
            mav.addObject("errormsg", messageSource.getMessage(e.getMessage(), new Object[]{env.getRequiredProperty("organisation.admin.email")},
                    LocaleContextHolder.getLocale()));
        } catch (Exception ex) {
            mav.addObject("errormsg", e.getMessage());
        }
        mav.addObject("exception", printExceptionMsg(e));
        return mav;
    }

    /**
     * This function captures and processes all other exceptions and sends a Information about the thrown Exception to the error.jsp
     *
     * @param request {@link HttpServletRequest}
     * @param e       {@link Exception}
     * @return Mapping to the DEFAULT_ERROR_VIEW (error.jsp)
     */
    @ExceptionHandler(Exception.class)
    public ModelAndView defaultException(HttpServletRequest request, Exception e) {
        HashMap<String, String> clientInfo = this.clientInfo.getClientInfo(request);
        log.error("Exception thrown from[{}] Exception: [{}],  Client-Information: [{}]", request::getRequestURL, () -> e, () -> clientInfo);
        ModelAndView mav = new ModelAndView(DEFAULT_ERROR_VIEW);
        String ref = clientInfo.get("Referrer");
        if (ref != null && !ref.isEmpty() && ref.contains(env.getRequiredProperty("application.url")))
            mav.addObject("referrerURL", ref);
        else
            mav.addObject("referrerURL", env.getRequiredProperty("application.url"));
        mav.addObject("exceptionTitle", messageSource.getMessage("error.gen.exception.title", null, LocaleContextHolder.getLocale()));
        mav.addObject("errormsg", messageSource.getMessage("error.gen.exception.msg", new Object[]{env.getRequiredProperty("organisation.admin.email")},
                LocaleContextHolder.getLocale()));
        mav.addObject("exception", printExceptionMsg(e));
        return mav;
    }

    private String printExceptionMsg(Exception e) {
        StringBuilder sb = new StringBuilder();
        sb.append(e.getMessage());
        if (e.getStackTrace() != null && e.getStackTrace().length > 0)
            sb.append(" @ ").append(e.getStackTrace()[0].toString());
        return sb.toString();
    }
}
