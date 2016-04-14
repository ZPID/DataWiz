package de.zpid.datawiz.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import de.zpid.datawiz.dto.ProjectDTO;
import de.zpid.datawiz.dto.StudyDTO;

@Service
@Scope("singleton")
public class StudyDAO extends SuperDAO {

  public StudyDAO() {
    super();
    if (log.isInfoEnabled())
      log.info("Loading StudyDAO as Singleton and Service");
  }

  public List<StudyDTO> findAllStudiesByProjectId(final ProjectDTO project) throws Exception {
    log.trace("execute findAllStudiesByProjectId for project [id: {}; name: {}]", () -> project.getId(),
        () -> project.getTitle());
    String sql = "SELECT * FROM dw_study WHERE dw_study.project_id = ?";
    final List<StudyDTO> res = jdbcTemplate.query(sql, new Object[] { project.getId() }, new RowMapper<StudyDTO>() {
      public StudyDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        return setStudyDTO(rs);
      }
    });
    log.debug("leaving findAllStudiesByProjectId with size: {}", () -> res.size());
    return res;
  }

  public StudyDTO findById(final long studyId) throws Exception {
    log.trace("execute findById for study [id: {}]", () -> studyId);
    final StudyDTO res = jdbcTemplate.query("SELECT dw_study.* FROM dw_study WHERE dw_study.id = ?",
        new Object[] { studyId }, new ResultSetExtractor<StudyDTO>() {
          @Override
          public StudyDTO extractData(ResultSet rs) throws SQLException, DataAccessException {
            if (rs.next()) {
              return setStudyDTO(rs);
            }
            return null;
          }
        });
    log.debug("leaving findByID with study: {}", () -> res);
    return res;
  }

  private StudyDTO setStudyDTO(final ResultSet rs) throws SQLException {
    StudyDTO study = (StudyDTO) context.getBean("StudyDTO");
    study.setId(rs.getInt("id"));
    study.setProjectId(rs.getLong("project_id"));
    study.setLastUserId(rs.getLong("last_user_id"));
    study.setTimestamp(rs.getTimestamp("last_edit"));
    study.setTitle(rs.getString("title"));
    return study;
  }

}
