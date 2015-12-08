package de.zpid.datawiz.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import de.zpid.datawiz.dto.ContributorDTO;
import de.zpid.datawiz.dto.ProjectDTO;
import de.zpid.datawiz.dto.StudyDTO;

public class ContributorDAO {

  private static final Logger log = Logger.getLogger(ContributorDAO.class);
  private ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");
  private JdbcTemplate jdbcTemplate;

  public ContributorDAO() {
    super();
  }

  public ContributorDAO(DataSource dataSource) {
    super();
    this.jdbcTemplate = new JdbcTemplate(dataSource);
  }

  public List<ContributorDTO> getByProject(ProjectDTO project, boolean withStudy) throws Exception {
    if (log.isDebugEnabled())
      log.debug("execute getByProject for project [id: " + project.getId() + " name: " + project.getTitle() + "]");
    String sql = "SELECT * FROM dw_contributors WHERE dw_contributors.project_id = ?"
        + (withStudy ? "" : " AND dw_contributors.study_id IS NULL");
    return jdbcTemplate.query(sql, new Object[] { project.getId() }, new RowMapper<ContributorDTO>() {
      public ContributorDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        ContributorDTO contri = (ContributorDTO) context.getBean("ContributorDTO");
        contri.setId(rs.getInt("id"));
        contri.setProjectId(rs.getInt("project_id"));
        contri.setStudyId(rs.getInt("study_id"));
        contri.setStudyVersion(rs.getInt("study_version"));
        contri.setTitle(rs.getString("title"));
        contri.setFirstName(rs.getString("first_name"));
        contri.setLastName(rs.getString("last_name"));
        contri.setInstitution(rs.getString("institution"));
        contri.setDepartment(rs.getString("department"));
        contri.setOrcid(rs.getString("orcid"));
        return contri;
      }
    });
  }
  
  public List<ContributorDTO> getByStudy(StudyDTO study) throws Exception {
    if (log.isDebugEnabled())
      log.debug("execute getByStudy for study [id: " + study.getId() + " name: " + study.getTitle() + "]");
    String sql = "SELECT * FROM dw_contributors WHERE dw_contributors.study_id = ?";
    return jdbcTemplate.query(sql, new Object[] { study.getId() }, new RowMapper<ContributorDTO>() {
      public ContributorDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        ContributorDTO contri = (ContributorDTO) context.getBean("ContributorDTO");
        contri.setId(rs.getInt("id"));
        contri.setProjectId(rs.getInt("project_id"));
        contri.setStudyId(rs.getInt("study_id"));
        contri.setStudyVersion(rs.getInt("study_version"));
        contri.setTitle(rs.getString("title"));
        contri.setFirstName(rs.getString("first_name"));
        contri.setLastName(rs.getString("last_name"));
        contri.setInstitution(rs.getString("institution"));
        contri.setDepartment(rs.getString("department"));
        contri.setOrcid(rs.getString("orcid"));
        return contri;
      }
    });
  }

}
