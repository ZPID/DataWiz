package de.zpid.datawiz.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import de.zpid.datawiz.dto.ProjectDTO;
import de.zpid.datawiz.dto.StudyDTO;

public class StudyDAO {

  private static final Logger log = Logger.getLogger(StudyDAO.class);
  private ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");
  private JdbcTemplate jdbcTemplate;

  public StudyDAO() {
    super();
  }

  public StudyDAO(DataSource dataSource) {
    super();
    this.jdbcTemplate = new JdbcTemplate(dataSource);
  }

  public List<StudyDTO> getLatestStudyVersionsByProjectID(ProjectDTO project) throws Exception {
    if (log.isDebugEnabled())
      log.debug("execute getLatestStudyVersionsByProjectID for project [id: " + project.getId() + " name: "
          + project.getTitle() + "]");
    String sql = "SELECT * from dw_study AS study INNER JOIN "
        + "( SELECT dw_study.id, MAX(dw_study.timestamp) max_ts FROM dw_study WHERE dw_study.project_id = ?  GROUP BY dw_study.id ) sid "
        + "ON sid.id=study.id AND sid.max_ts=study.timestamp";
    return jdbcTemplate.query(sql, new Object[] { project.getId() }, new RowMapper<StudyDTO>() {
      public StudyDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        StudyDTO study = (StudyDTO) context.getBean("StudyDTO");
        study.setId(rs.getInt("id"));
        study.setVersion(rs.getInt("version"));
        study.setProjectId(rs.getInt("project_id"));
        study.setAuthorMail(rs.getString("user_mail"));
        // study.setMaster(rs.getBoolean("master"));
        study.setTimestamp(rs.getTimestamp("timestamp"));
        study.setTitle(rs.getString("title"));
        return study;
      }
    });
  }

}
