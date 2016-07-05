package de.zpid.datawiz.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import de.zpid.datawiz.dto.ProjectDTO;
import de.zpid.datawiz.dto.StudyDTO;

@Repository
@Scope("singleton")
public class StudyDAO extends SuperDAO {

  private static Logger log = LogManager.getLogger(StudyDAO.class);

  public StudyDAO() {
    super();
    if (log.isInfoEnabled())
      log.info("Loading StudyDAO as Singleton and Service");
  }

  public List<StudyDTO> findAllStudiesByProjectId(final ProjectDTO project) throws Exception {
    log.trace("execute findAllStudiesByProjectId for project [id: {}; name: {}]", () -> project.getId(),
        () -> project.getTitle());
    String sql = "SELECT id, project_id, last_user_id, lastEdit, currentlyEdit, editSince,"
        + " editUserId, title, internalID, transTitle, sAbstract, sAbstractTrans"
        + " FROM dw_study WHERE dw_study.project_id = ?";
    final List<StudyDTO> res = jdbcTemplate.query(sql, new Object[] { project.getId() }, new RowMapper<StudyDTO>() {
      public StudyDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        return setStudyDTO(rs, true);
      }
    });
    log.debug("leaving findAllStudiesByProjectId with size: {}", () -> res.size());
    return res;
  }

  public StudyDTO findById(final long studyId, final long projectId) throws Exception {
    log.trace("execute findById for study [id: {}] from project [id: {}]", () -> studyId, () -> projectId);
    final StudyDTO res = jdbcTemplate.query(
        "SELECT dw_study.* FROM dw_study WHERE dw_study.id = ? AND dw_study.project_id = ?",
        new Object[] { studyId, projectId }, new ResultSetExtractor<StudyDTO>() {
          @Override
          public StudyDTO extractData(ResultSet rs) throws SQLException, DataAccessException {
            if (rs.next()) {
              return setStudyDTO(rs, false);
            }
            return null;
          }
        });
    log.debug("leaving findByID with study: {}", () -> res);
    return res;
  }

  private StudyDTO setStudyDTO(final ResultSet rs, final boolean overview) throws SQLException {
    StudyDTO study = (StudyDTO) applicationContext.getBean("StudyDTO");
    study.setId(rs.getLong("id"));
    study.setProjectId(rs.getLong("project_id"));
    study.setLastUserId(rs.getLong("last_user_id"));
    study.setTimestamp(rs.getTimestamp("lastEdit") != null ? rs.getTimestamp("lastEdit").toLocalDateTime() : null);
    study.setCurrentlyEdit(rs.getBoolean("currentlyEdit"));
    study.setEditSince(rs.getTimestamp("editSince") != null ? rs.getTimestamp("editSince").toLocalDateTime() : null);
    study.setEditUserId(rs.getLong("editUserId"));
    // Administrative Data
    study.setTitle(rs.getString("title"));
    study.setInternalID(rs.getString("internalID"));
    study.setTransTitle(rs.getString("transTitle"));
    study.setsAbstract(rs.getString("sAbstract"));
    study.setsAbstractTrans(rs.getString("sAbstractTrans"));
    if (!overview) {
      study.setCompleteSel(rs.getString("completeSel"));
      study.setExcerpt(rs.getString("excerpt"));
      study.setPrevWork(rs.getString("prevWork"));
      study.setPrevWorkStr(rs.getString("prevWorkStr"));
    }
    return study;
  }

}
