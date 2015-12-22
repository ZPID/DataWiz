package de.zpid.datawiz.configuration;

import java.util.Locale;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

import de.zpid.datawiz.dao.ContributorDAO;
import de.zpid.datawiz.dao.ProjectDAO;
import de.zpid.datawiz.dao.RoleDAO;
import de.zpid.datawiz.dao.StudyDAO;
import de.zpid.datawiz.dao.TagDAO;
import de.zpid.datawiz.dao.UserDAO;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "de.zpid.datawiz")
public class DataWizConfiguration extends WebMvcConfigurerAdapter {

  @Bean(name = "DataWiz")
  public ViewResolver viewResolver() {
    InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
    viewResolver.setViewClass(JstlView.class);
    viewResolver.setPrefix("/WEB-INF/views/");
    viewResolver.setSuffix(".jsp");
    return viewResolver;
  }

  @Bean(name = "messageSource")
  public MessageSource resourceBundleMessageSource() {
    ResourceBundleMessageSource resolver = new ResourceBundleMessageSource();
    resolver.setBasename("de.zpid.datawiz.properties.ApplicationResources");
    return resolver;
  }

  @Bean
  public LocaleResolver localeResolver() {
    CookieLocaleResolver resolver = new CookieLocaleResolver();
    resolver.setDefaultLocale(new Locale("de"));
    return resolver;
  }

  @Bean(name = "dataSource")
  public DataSource getDataSource() {
    DriverManagerDataSource dataSource = new DriverManagerDataSource();
    dataSource.setDriverClassName("com.mysql.jdbc.Driver");
    dataSource.setUrl("jdbc:mysql://localhost:3306/datawiz");
    dataSource.setUsername("root");
    dataSource.setPassword("");
    return dataSource;
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

  @Autowired
  @Bean(name = "userDao")
  public UserDAO getUserDao() {
    return new UserDAO(getDataSource());
  }

  @Autowired
  @Bean(name = "projectDao")
  public ProjectDAO getProjectDao() {
    return new ProjectDAO(getDataSource());
  }

  @Autowired
  @Bean(name = "studyDao")
  public StudyDAO getStudyDAO() {
    return new StudyDAO(getDataSource());
  }

  @Autowired
  @Bean(name = "roleDao")
  public RoleDAO getRoleDAO() {
    return new RoleDAO(getDataSource());
  }

  @Autowired
  @Bean(name = "contributorDao")
  public ContributorDAO getContributorDAO() {
    return new ContributorDAO(getDataSource());
  }

  @Autowired
  @Bean(name = "tagDao")
  public TagDAO getTagDAO() {
    return new TagDAO(getDataSource());
  }
}