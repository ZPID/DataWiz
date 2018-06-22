package de.zpid.datawiz.configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.lang.NonNull;
import org.springframework.lang.NonNullApi;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;


/**
 * SpringMvcInitializer for loading the custom Session Listener
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
public class SpringMvcInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    private static Logger log = LogManager.getLogger(SpringMvcInitializer.class);

    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class[]{DataWizConfiguration.class};
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return null;
    }

    @Override
    @NonNull
    protected String[] getServletMappings() {
        return new String[]{"/"};
    }

    @Override
    public void onStartup(@NonNull ServletContext servletContext) throws ServletException {
        super.onStartup(servletContext);
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        Properties props = new Properties();
        try (InputStream resourceStream = loader.getResourceAsStream("datawiz.properties")) {
            props.load(resourceStream);
        } catch (IOException e) {
            log.fatal("Error loading datawiz.properties - {}", () -> e);
        }
        servletContext.addListener(new SessionListener((String) props.get("session.timeout")));
    }
}
