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
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
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
import com.squareup.okhttp.ConnectionPool;

import de.zpid.datawiz.util.MinioUtil;
import de.zpid.spss.SPSSIO;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "de.zpid.datawiz")
@PropertySource("classpath:datawiz.properties")
public class DataWizConfiguration extends WebMvcConfigurerAdapter {

  private static Logger log = LogManager.getLogger(DataWizConfiguration.class);

  @Autowired
  private Environment env;

  @Bean(name = "PropertiesFile")
  public static PropertyPlaceholderConfigurer properties() {
    PropertyPlaceholderConfigurer placeholder = new PropertyPlaceholderConfigurer();
    ClassPathResource[] value = new ClassPathResource[] { new ClassPathResource("datawiz.properties") };
    placeholder.setLocations(value);
    return placeholder;
  }

  @Bean(name = "sessionTimeout")
  @Scope("singleton")
  public int getSessionTimeout() {
    try {
      return Integer.parseInt(env.getRequiredProperty("session.timeout").trim());
    } catch (Exception e) {
      return 600;
    }
  }

  @Bean(name = "spss")
  public SPSSIO getSPSSDLL() {
    String OS = System.getProperty("os.name").toLowerCase();
    String path;
    if (OS.contains("win"))
      path = env.getRequiredProperty("spss.absoluth.path").trim();
    else
      path = env.getRequiredProperty("spss.absoluth.path.test").trim();
    log.info("Loading SPSSDLL with path: {}", () -> path);
    SPSSIO.setAbsoluteLibPath(path);
    return new SPSSIO();
  }

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
        "classpath:locale/EmailResources", "classpath:locale/StudyResources", "classpath:locale/RecordResources",
        "classpath:locale/LoggingResources");
    resolver.setDefaultEncoding("UTF-8");
    log.info("messageSource succesfully loaded");
    return resolver;

  }

  private ClassPathXmlApplicationContext context;

  @Bean(name = "applicationContext")
  public ClassPathXmlApplicationContext applicationContext() {
    this.context = new ClassPathXmlApplicationContext("spring.xml");
    return this.context;
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

  @Bean(name = "filterMultipartResolver")
  public CommonsMultipartResolver commonsMultipartResolver() {
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

  @Bean(name = "minioUtil", destroyMethod = "close")
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
    log.warn("Destroy DataWiz Application - ");
    ConnectionPool.getDefault().evictAll();
    Enumeration<Driver> drivers = DriverManager.getDrivers();
    while (drivers.hasMoreElements()) {
      Driver driver = drivers.nextElement();
      try {
        DriverManager.deregisterDriver(driver);
        log.warn(String.format("Driver %s deregistered", driver));
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
    AbandonedConnectionCleanupThread.uncheckedShutdown();
    context.destroy();
    context.close();

  }
}