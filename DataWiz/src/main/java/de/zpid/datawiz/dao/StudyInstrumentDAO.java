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

import de.zpid.datawiz.dto.StudyInstrumentDTO;

@Repository
@Scope("singleton")
public class StudyInstrumentDAO extends SuperDAO {

  private static Logger log = LogManager.getLogger(StudyInstrumentDAO.class);

  public StudyInstrumentDAO() {
    super();
    if (log.isInfoEnabled())
      log.info("Loading StudyInstumentDAO as Singleton and Service");
  }

  public List<StudyInstrumentDTO> findAllByStudy(final long studyId) throws Exception {
    log.trace("execute findAllByType [id: {}]", () -> studyId);
    String sql = "SELECT * FROM dw_study_instruments WHERE dw_study_instruments.study_id = ?";
    final List<StudyInstrumentDTO> ret = jdbcTemplate.query(sql, new Object[] { studyId },
        new RowMapper<StudyInstrumentDTO>() {
          public StudyInstrumentDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
            StudyInstrumentDTO dt = (StudyInstrumentDTO) applicationContext.getBean("StudyInstrumentDTO");
            dt.setId(rs.getLong("id"));
            dt.setStudyId(rs.getLong("study_id"));
            dt.setTitle(rs.getString("title"));
            dt.setAuthor(rs.getString("author"));
            dt.setCitation(rs.getString("citation"));
            dt.setSummary(rs.getString("summary"));
            dt.setTheoHint(rs.getString("theoHint"));
            dt.setStructure(rs.getString("structure"));
            dt.setConstruction(rs.getString("construction"));
            dt.setObjectivity(rs.getString("objectivity"));
            dt.setReliability(rs.getString("reliability"));
            dt.setValidity(rs.getString("validity"));
            dt.setNorm(rs.getString("norm"));
            return dt;
          }
        });
    log.debug("Transaction for findAllByType returned [size: {}]", () -> ret.size());
    return ret;
  }

  public int[] delete(final List<StudyInstrumentDTO> types) {
    log.trace("execute delete [size: {}]", () -> types.size());
    int[] ret = this.jdbcTemplate.batchUpdate("DELETE FROM dw_study_instruments WHERE id = ?",
        new BatchPreparedStatementSetter() {
          public void setValues(PreparedStatement ps, int i) throws SQLException {
            StudyInstrumentDTO type = types.get(i);
            ps.setLong(1, type.getId());
          }

          public int getBatchSize() {
            return types.size();
          }
        });
    log.debug("leaving delete with result: {}", () -> ret.length);
    return ret;
  }

  public int[] insert(final List<StudyInstrumentDTO> types) {
    log.trace("execute insert [size: {}]", () -> types.size());
    int[] ret = this.jdbcTemplate.batchUpdate("Insert INTO dw_study_instruments"
        + " (study_id, title, author, citation, summary, theoHint, structure, construction, objectivity, reliability, validity, norm)"
        + " VALUES (?,?,?,?,?,?,?,?,?,?,?,?)", new BatchPreparedStatementSetter() {
          public void setValues(PreparedStatement ps, int i) throws SQLException {
            StudyInstrumentDTO cont = types.get(i);
            setStudyInstrumentDTO(ps, cont, false);
          }

          public int getBatchSize() {
            return types.size();
          }
        });
    log.debug("leaving insert with result: {}", () -> ret.length);
    return ret;
  }

  public int[] update(final List<StudyInstrumentDTO> types) {
    log.trace("execute update [size: {}]", () -> types.size());
    int[] ret = this.jdbcTemplate.batchUpdate(
        "UPDATE dw_study_instruments"
            + " SET  title = ?, author = ?, citation = ?, summary = ?, theoHint = ?, structure = ?,"
            + " construction = ?, objectivity = ?, reliability = ?, validity = ?, norm = ? WHERE id = ?",
        new BatchPreparedStatementSetter() {
          public void setValues(PreparedStatement ps, int i) throws SQLException {
            StudyInstrumentDTO cont = types.get(i);
            setStudyInstrumentDTO(ps, cont, true);
          }

          public int getBatchSize() {
            return types.size();
          }
        });
    log.debug("leaving update with result: {}", () -> ret.length);
    return ret;
  }

  /**
   * @param ps
   * @param cont
   * @throws SQLException
   */
  private void setStudyInstrumentDTO(final PreparedStatement ps, final StudyInstrumentDTO cont, final boolean update)
      throws SQLException {
    int i = 1;
    if (!update)
      ps.setLong(i++, cont.getStudyId());
    ps.setString(i++, cont.getTitle());
    ps.setString(i++, cont.getAuthor());
    ps.setString(i++, cont.getCitation());
    ps.setString(i++, cont.getSummary());
    ps.setString(i++, cont.getTheoHint());
    ps.setString(i++, cont.getStructure());
    ps.setString(i++, cont.getConstruction());
    ps.setString(i++, cont.getObjectivity());
    ps.setString(i++, cont.getReliability());
    ps.setString(i++, cont.getValidity());
    ps.setString(i++, cont.getNorm());
    if (update)
      ps.setLong(i++, cont.getId());
  }

}
