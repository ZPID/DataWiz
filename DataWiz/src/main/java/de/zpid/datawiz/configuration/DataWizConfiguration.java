package de.zpid.datawiz.configuration;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Locale;

import javax.annotation.PreDestroy;
import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.validation.SmartValidator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

import com.mysql.jdbc.AbandonedConnectionCleanupThread;

import de.zpid.datawiz.util.MinioUtil;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "de.zpid.datawiz")
@PropertySource("classpath:datawiz.properties")
public class DataWizConfiguration extends WebMvcConfigurerAdapter {

  private static Logger log = LogManager.getLogger(DataWizConfiguration.class);

  @Autowired
  private Environment env;

  @Bean(name = "DataWiz")
  public ViewResolver viewResolver() {
    InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
    viewResolver.setViewClass(JstlView.class);
    viewResolver.setPrefix("/WEB-INF/views/");
    viewResolver.setSuffix(".jsp");
    log.info("viewResolver succesfully loaded");
    return viewResolver;
  }

  @Bean(name = "messageSource")
  public MessageSource resourceBundleMessageSource() {
    ReloadableResourceBundleMessageSource resolver = new ReloadableResourceBundleMessageSource();
    resolver.setBasenames("classpath:locale/ApplicationResources", "classpath:locale/DMPResources",
        "classpath:locale/EmailResources", "classpath:locale/StudyResources");
    resolver.setDefaultEncoding("UTF-8");
    log.info("messageSource succesfully loaded");
    return resolver;
  }

  @Bean(name = "applicationContext")
  public ClassPathXmlApplicationContext applicationContext() {
    return new ClassPathXmlApplicationContext("spring.xml");
  }

  @Bean
  public LocaleResolver localeResolver() {
    CookieLocaleResolver resolver = new CookieLocaleResolver();
    resolver.setDefaultLocale(new Locale("de"));
    log.info("localeResolver succesfully loaded");
    return resolver;
  }

  @Bean(name = "validator")
  public SmartValidator validator() {
    LocalValidatorFactoryBean sm = new LocalValidatorFactoryBean();
    sm.setValidationMessageSource(resourceBundleMessageSource());
    log.info("validator succesfully loaded");
    return sm;
  }

  @Bean(name = "multipartResolver")
  public CommonsMultipartResolver multipartResolver() {
    CommonsMultipartResolver resolver = new CommonsMultipartResolver();
    resolver.setDefaultEncoding("utf-8");
    log.info("multipartResolver succesfully loaded");
    return resolver;
  }

  @Bean(name = "dataSource")
  public DataSource getDataSource() {
    DriverManagerDataSource dataSource = new DriverManagerDataSource();
    dataSource.setDriverClassName(env.getRequiredProperty("dataSource.driverClassName"));
    dataSource.setUrl(env.getRequiredProperty("dataSource.url"));
    dataSource.setUsername(env.getRequiredProperty("dataSource.username"));
    dataSource.setPassword(env.getRequiredProperty("dataSource.password"));
    log.info("dataSource succesfully loaded");
    return dataSource;
  }
  
  @Bean
  public PlatformTransactionManager txManager() {
      return new DataSourceTransactionManager(getDataSource());
  }

  @Bean(name = "minioUtil")
  public MinioUtil minioUtil() {
    return new MinioUtil(env);
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
    interceptor.setParamName("datawiz_locale");
    registry.addInterceptor(interceptor);
  }

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/static/**").addResourceLocations("/static/");
  }

  @Override
  public void configurePathMatch(PathMatchConfigurer matcher) {
    matcher.setUseRegisteredSuffixPatternMatch(true);
  }

  @PreDestroy
  public void destroy() {
    try {
      AbandonedConnectionCleanupThread.shutdown();
    } catch (Throwable t) {
    }
    Enumeration<Driver> drivers = DriverManager.getDrivers();
    while (drivers.hasMoreElements()) {
      Driver driver = drivers.nextElement();
      try {
        DriverManager.deregisterDriver(driver);
      } catch (SQLException e) {
        e.printStackTrace();

      }
    }
  }
}