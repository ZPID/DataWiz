package de.zpid.datawiz.configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

public class SpringMvcInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

  private static Logger log = LogManager.getLogger(SpringMvcInitializer.class);

  @Override
  protected Class<?>[] getRootConfigClasses() {
    return new Class[] { DataWizConfiguration.class };
  }

  @Override
  protected Class<?>[] getServletConfigClasses() {
    return null;
  }

  @Override
  protected String[] getServletMappings() {
    return new String[] { "/" };
  }

  @Override
  public void onStartup(ServletContext servletContext) throws ServletException {
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
