package de.zpid.datawiz.configuration;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import javax.annotation.PreDestroy;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;
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
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.SmartValidator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

import de.zpid.datawiz.util.MinioUtil;
import de.zpid.spss.SPSSIO;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "de.zpid.datawiz")
@PropertySource("classpath:datawiz.properties")
public class DataWizConfiguration implements WebMvcConfigurer {

	private static Logger log = LogManager.getLogger(DataWizConfiguration.class);
	private Environment env;

	@Autowired
	public DataWizConfiguration(Environment env){
		this.env = env;
	}

	@Bean(name = "PropertiesFile")
	public static PropertyPlaceholderConfigurer properties() {
		PropertyPlaceholderConfigurer placeholder = new PropertyPlaceholderConfigurer();
		ClassPathResource[] value = new ClassPathResource[] { new ClassPathResource("datawiz.properties") };
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

	/*
	 * private DataSource getDataSource() { DriverManagerDataSource dataSource = new DriverManagerDataSource();
	 * dataSource.setDriverClassName(env.getRequiredProperty("dataSource.driverClassName")); dataSource.setUrl(env.getRequiredProperty("dataSource.url"));
	 * dataSource.setUsername(env.getRequiredProperty("dataSource.username")); dataSource.setPassword(env.getRequiredProperty("dataSource.password"));
	 * log.info("dataSource succesfully loaded"); return dataSource; }
	 */

	/**
	 * 
	 * @return
	 */
	@Bean
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
	}

	@Bean
	public PlatformTransactionManager txManager() {
		return new DataSourceTransactionManager(getDataSource());
	}

	@Bean(name = "jdbcTemplate")
	@Transactional
	public JdbcTemplate jdbcTemplate() {
		return new JdbcTemplate(getDataSource());
	}

	@Bean(name = "spss")
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
		resolver.setBasenames("classpath:locale/ApplicationResources", "classpath:locale/DMPResources", "classpath:locale/EmailResources",
		    "classpath:locale/StudyResources", "classpath:locale/RecordResources", "classpath:locale/LoggingResources", "classpath:locale/ExportResources");
		resolver.setDefaultEncoding("UTF-8");
		Properties fileCharsets = new Properties();
		fileCharsets.setProperty("org/springframework/context/support/messages_de", "unicode");
		resolver.setFileEncodings(fileCharsets);
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

	@PreDestroy
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
	}
}