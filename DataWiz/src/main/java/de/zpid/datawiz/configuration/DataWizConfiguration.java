package de.zpid.datawiz.configuration;

import java.util.Locale;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
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

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "de.zpid.datawiz")
@PropertySource("classpath:datawiz.properties")
public class DataWizConfiguration extends WebMvcConfigurerAdapter {

  @Autowired
  private Environment env;

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
    resolver.setBasenames("de.zpid.datawiz.properties.ApplicationResources", "de.zpid.datawiz.properties.DMPResources",
        "de.zpid.datawiz.properties.EmailResources", "de.zpid.datawiz.properties.StudyResources");
    resolver.setDefaultEncoding("UTF-8");
    return resolver;
  }

  @Bean
  public LocaleResolver localeResolver() {
    CookieLocaleResolver resolver = new CookieLocaleResolver();
    resolver.setDefaultLocale(new Locale("de"));
    return resolver;
  }

  @Bean(name = "validator")
  public SmartValidator validator() {
    LocalValidatorFactoryBean sm = new LocalValidatorFactoryBean();
    sm.setValidationMessageSource(resourceBundleMessageSource());
    return sm;
  }

  @Bean(name = "multipartResolver")
  public CommonsMultipartResolver multipartResolver() {
    CommonsMultipartResolver resolver = new CommonsMultipartResolver();
    resolver.setDefaultEncoding("utf-8");
    return resolver;
  }

  @Bean(name = "dataSource")
  public DataSource getDataSource() {
    DriverManagerDataSource dataSource = new DriverManagerDataSource();
    dataSource.setDriverClassName(env.getRequiredProperty("dataSource.driverClassName"));
    dataSource.setUrl(env.getRequiredProperty("dataSource.url"));
    dataSource.setUsername(env.getRequiredProperty("dataSource.username"));
    dataSource.setPassword(env.getRequiredProperty("dataSource.password"));
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

  @Override
  public void configurePathMatch(PathMatchConfigurer matcher) {
    matcher.setUseRegisteredSuffixPatternMatch(true);
  }
}