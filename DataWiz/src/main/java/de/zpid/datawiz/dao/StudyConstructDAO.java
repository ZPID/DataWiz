package de.zpid.datawiz.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import de.zpid.datawiz.dto.StudyConstructDTO;

@Repository
@Scope("singleton")
public class StudyConstructDAO extends SuperDAO {

  private static Logger log = LogManager.getLogger(StudyConstructDAO.class);

  public StudyConstructDAO() {
    super();
    if (log.isInfoEnabled())
      log.info("Loading StudyConstructDAO as Singleton and Service");
  }

  public List<StudyConstructDTO> findAllByStudy(final long studyId) throws Exception {
    log.trace("execute findAllByType [id: {}]", () -> studyId);
    String sql = "SELECT * FROM dw_study_constructs WHERE dw_study_constructs.study_id = ?";
    final List<StudyConstructDTO> ret = jdbcTemplate.query(sql, new Object[] { studyId },
        new RowMapper<StudyConstructDTO>() {
          public StudyConstructDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
            StudyConstructDTO dt = (StudyConstructDTO) applicationContext.getBean("StudyConstructDTO");
            dt.setId(rs.getLong("id"));
            dt.setStudyId(rs.getLong("study_id"));
            dt.setName(rs.getString("name"));
            dt.setType(rs.getString("type"));
            dt.setOther(rs.getString("other"));
            return dt;
          }
        });
    log.debug("Transaction for findAllByType returned [size: {}]", () -> ret.size());
    return ret;
  }

  public int[] delete(final List<StudyConstructDTO> types) {
    log.trace("execute delete [size: {}]", () -> types.size());
    int[] ret = this.jdbcTemplate.batchUpdate("DELETE FROM dw_study_constructs WHERE id = ?",
        new BatchPreparedStatementSetter() {
          public void setValues(PreparedStatement ps, int i) throws SQLException {
            StudyConstructDTO type = types.get(i);
            ps.setLong(1, type.getId());
          }

          public int getBatchSize() {
            return types.size();
          }
        });
    log.debug("leaving delete with result: {}", () -> ret.length);
    return ret;
  }

  public int[] insert(final List<StudyConstructDTO> types) {
    log.trace("execute insert [size: {}]", () -> types.size());
    int[] ret = this.jdbcTemplate.batchUpdate(
        "Insert INTO dw_study_constructs (study_id, name, type, other) VALUES (?,?,?,?)",
        new BatchPreparedStatementSetter() {
          public void setValues(PreparedStatement ps, int i) throws SQLException {
            StudyConstructDTO cont = types.get(i);
            ps.setLong(1, cont.getStudyId());
            ps.setString(2, cont.getName());
            ps.setString(3, cont.getType());
            ps.setString(4, cont.getOther());
          }

          public int getBatchSize() {
            return types.size();
          }
        });
    log.debug("leaving insert with result: {}", () -> ret.length);
    return ret;
  }

  public int[] update(final List<StudyConstructDTO> types) {
    log.trace("execute update [size: {}]", () -> types.size());
    int[] ret = this.jdbcTemplate.batchUpdate(
        "UPDATE dw_study_constructs SET  name = ?, type = ?, other = ? WHERE id = ?",
        new BatchPreparedStatementSetter() {
          public void setValues(PreparedStatement ps, int i) throws SQLException {
            StudyConstructDTO cont = types.get(i);
            ps.setString(1, cont.getName());
            ps.setString(2, cont.getType());
            ps.setString(3, cont.getOther());
            ps.setLong(4, cont.getId());
          }

          public int getBatchSize() {
            return types.size();
          }
        });
    log.debug("leaving update with result: {}", () -> ret.length);
    return ret;
  }

}
