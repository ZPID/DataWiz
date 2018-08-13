package de.zpid.datawiz.configuration;

import de.zpid.datawiz.util.MinioUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.CacheControl;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.SmartValidator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

import javax.sql.DataSource;
import java.util.Locale;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * SpringMVC Configuration for DataWiz
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
@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "de.zpid.datawiz")
@PropertySource("classpath:datawiz.properties")
public class DataWizConfiguration implements WebMvcConfigurer {

    private static Logger log = LogManager.getLogger(DataWizConfiguration.class);
    private Environment env;

    @Autowired
    public DataWizConfiguration(Environment env) {
        this.env = env;
    }

    @Bean(name = "PropertiesFile")
    public static PropertyPlaceholderConfigurer properties() {
        PropertyPlaceholderConfigurer placeholder = new PropertyPlaceholderConfigurer();
        ClassPathResource[] value = new ClassPathResource[]{new ClassPathResource("datawiz.properties")};
        placeholder.setLocations(value);
        return placeholder;
    }

    @Bean(name = "sessionTimeout")
    public int getSessionTimeout() {
        try {
            return Integer.parseInt(env.getRequiredProperty("session.timeout").trim());
        } catch (Exception e) {
            return 600;
        }
    }

    private DataSource getDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(env.getRequiredProperty("dataSource.driverClassName"));
        dataSource.setUrl(env.getRequiredProperty("dataSource.url"));
        dataSource.setUsername(env.getRequiredProperty("dataSource.username"));
        dataSource.setPassword(env.getRequiredProperty("dataSource.password"));
        log.info("dataSource succesfully loaded");
        return dataSource;
    }


/*    @Bean
    public DataSource getDataSource() {
        PoolProperties poolProperties = new PoolProperties();
        poolProperties.setDriverClassName(env.getRequiredProperty("dataSource.driverClassName"));
        poolProperties.setUrl(env.getRequiredProperty("dataSource.url"));
        poolProperties.setUsername(env.getRequiredProperty("dataSource.username"));
        poolProperties.setPassword(env.getRequiredProperty("dataSource.password"));
        poolProperties.setTestWhileIdle(true);
        poolProperties.setTestOnBorrow(true);
        poolProperties.setValidationQuery("SELECT 1");
        poolProperties.setMinIdle(3);
        poolProperties.setTimeBetweenEvictionRunsMillis(60000);
        log.info("dataSource succesfully loaded");
        return new DataSource(poolProperties);
    }*/

    @Bean
    public PlatformTransactionManager txManager() {
        return new DataSourceTransactionManager(getDataSource());
    }

    @Bean(name = "jdbcTemplate")
    @Transactional
    public JdbcTemplate jdbcTemplate() {
        return new JdbcTemplate(getDataSource());
    }

/*    @Bean(name = "spss")
    public SPSSIO getSPSSDLL() {
        String OS = System.getProperty("os.name").toLowerCase();
        String path;
        if (OS.contains("win"))
            path = env.getRequiredProperty("spss.absoluth.path.windows").trim();
        else
            path = env.getRequiredProperty("spss.absoluth.path.unix").trim();
        log.info("Loading SPSSDLL with path: {}", () -> path);
        SPSSIO.setAbsoluteLibPath(path);
        return new SPSSIO();
    }*/

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
        resolver.setBasenames("classpath:locale/ApplicationResources", "classpath:locale/DMPResources", "classpath:locale/EmailResources",
                "classpath:locale/StudyResources", "classpath:locale/RecordResources", "classpath:locale/LoggingResources", "classpath:locale/ExportResources");
        resolver.setDefaultEncoding("UTF-8");
        Properties fileCharsets = new Properties();
        fileCharsets.setProperty("org/springframework/context/support/messages_de", "unicode");
        resolver.setFileEncodings(fileCharsets);
        log.info("messageSource succesfully loaded");
        return resolver;

    }


    @Bean(name = "applicationContext")
    public ClassPathXmlApplicationContext applicationContext() {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");
        return context;
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
        resolver.setMaxInMemorySize(268435456);
        resolver.setMaxUploadSize(1610612736);
        log.info("multipartResolver succesfully loaded");
        return resolver;
    }

    @Bean(name = "minioUtil")
    public MinioUtil minioUtil() {
        return new MinioUtil(env, true);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
        interceptor.setParamName("datawiz_locale");
        registry.addInterceptor(interceptor);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**").addResourceLocations("/static/").setCacheControl(CacheControl.maxAge(1, TimeUnit.DAYS));
    }

    @Override
    public void configurePathMatch(PathMatchConfigurer matcher) {
        matcher.setUseRegisteredSuffixPatternMatch(true);
    }

    /*@PreDestroy
    public void destroy() {
        log.info("Destroy DataWiz Application");
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement();
            try {
                DriverManager.deregisterDriver(driver);
                log.info("Driver {} deregistered", () -> driver);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        context.close();
    }*/
}