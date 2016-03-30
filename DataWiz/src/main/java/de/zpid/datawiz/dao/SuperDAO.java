package de.zpid.datawiz.dao;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

public class SuperDAO {

  @Autowired
  protected DataSource dataSource;

  @PostConstruct
  protected void initialize() {
    jdbcTemplate = new JdbcTemplate(dataSource);
  }

  protected Logger log = Logger.getLogger(getClass());
  protected ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");
  protected JdbcTemplate jdbcTemplate;

}
