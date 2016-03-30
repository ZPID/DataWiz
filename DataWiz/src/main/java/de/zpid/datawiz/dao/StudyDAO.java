package de.zpid.datawiz.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import de.zpid.datawiz.dto.ProjectDTO;
import de.zpid.datawiz.dto.StudyDTO;

@Service
@Scope("singleton")
public class StudyDAO extends SuperDAO {

  public List<StudyDTO> getAllStudiesByProjectId(ProjectDTO project) throws Exception {
    if (log.isDebugEnabled())
      log.debug("execute getAllStudiesByProjectId for project [id: " + project.getId() + " name: " + project.getTitle()
          + "]");
    String sql = "SELECT * FROM dw_study WHERE dw_study.project_id = ?";
    return jdbcTemplate.query(sql, new Object[] { project.getId() }, new RowMapper<StudyDTO>() {
      public StudyDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        StudyDTO study = (StudyDTO) context.getBean("StudyDTO");
        study.setId(rs.getInt("id"));
        study.setProjectId(rs.getInt("project_id"));
        study.setLastUserId(rs.getInt("last_user_id"));
        study.setTimestamp(rs.getTimestamp("timestamp"));
        study.setTitle(rs.getString("title"));
        return study;
      }
    });
  }

}
