package de.zpid.datawiz.dao;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

public class SuperDAO {

  @Autowired
  protected DataSource dataSource;

  @PostConstruct
  @Transactional
  protected void initialize() {
    jdbcTemplate = new JdbcTemplate(dataSource);
  }
  @Autowired
  protected ClassPathXmlApplicationContext applicationContext;
  protected JdbcTemplate jdbcTemplate;

}
