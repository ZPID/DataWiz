package de.zpid.datawiz.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import de.zpid.datawiz.dto.StudyListTypesDTO;
import de.zpid.datawiz.enumeration.DWFieldTypes;

@Repository
@Scope("singleton")
public class StudyListTypesDAO {

  @Autowired
  protected ClassPathXmlApplicationContext applicationContext;
  @Autowired
  protected JdbcTemplate jdbcTemplate;

  private static Logger log = LogManager.getLogger(StudyListTypesDAO.class);

  public StudyListTypesDAO() {
    super();
    if (log.isInfoEnabled())
      log.info("Loading StudyListTypesDAO as Singleton and Service");
  }

  public List<StudyListTypesDTO> findAllByStudyAndType(final long studyId, final DWFieldTypes type) throws Exception {
    log.trace("execute findAllByStudyAndType [id: {}; type: {}]", () -> studyId, () -> type.name());
    String sql = "SELECT * FROM dw_study_listtypes WHERE dw_study_listtypes.studyid = ? AND dw_study_listtypes.type = ?"
        + (type.equals(DWFieldTypes.MEASOCCNAME) ? "ORDER BY sort ASC" : "");
    final List<StudyListTypesDTO> ret = jdbcTemplate.query(sql, new Object[] { studyId, type.toString() },
        new RowMapper<StudyListTypesDTO>() {
          public StudyListTypesDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
            StudyListTypesDTO dt = (StudyListTypesDTO) applicationContext.getBean("StudyListTypesDTO");
            dt.setId(rs.getLong("id"));
            dt.setStudyid(rs.getLong("studyid"));
            dt.setText(rs.getString("text"));
            dt.setType(DWFieldTypes.valueOf(rs.getString("type")));
            if (type.equals(DWFieldTypes.MEASOCCNAME)) {
              dt.setSort(rs.getInt("sort"));
              dt.setTimetable(rs.getBoolean("timetable"));
            }
            if (type.equals(DWFieldTypes.OBJECTIVES)) {
              dt.setObjectivetype(rs.getString("objectivetype"));
            }
            return dt;
          }
        });
    log.debug("Transaction for findAllByStudyAndType returned [size: {}]", () -> ret.size());
    return ret;
  }

  public int[] delete(final List<StudyListTypesDTO> types) {
    log.trace("execute delete [size: {}]", () -> types.size());
    int[] ret = this.jdbcTemplate.batchUpdate("DELETE FROM dw_study_listtypes WHERE id = ?",
        new BatchPreparedStatementSetter() {
          public void setValues(PreparedStatement ps, int i) throws SQLException {
            StudyListTypesDTO type = types.get(i);
            ps.setLong(1, type.getId());
          }

          public int getBatchSize() {
            return types.size();
          }
        });
    log.debug("leaving deleteFromStudy with result: {}", () -> ret.length);
    return ret;
  }

  public int[] insert(final List<StudyListTypesDTO> types) {
    log.trace("execute insert [size: {}]", () -> types.size());
    int[] ret = this.jdbcTemplate.batchUpdate(
        "Insert INTO dw_study_listtypes (studyid, text, type, sort, timetable, objectivetype) VALUES (?,?,?,?,?,?)",
        new BatchPreparedStatementSetter() {
          public void setValues(PreparedStatement ps, int i) throws SQLException {
            StudyListTypesDTO cont = types.get(i);
            ps.setLong(1, cont.getStudyid());
            ps.setString(2, cont.getText());
            ps.setString(3, cont.getType().name());
            ps.setInt(4, cont.getSort());
            ps.setBoolean(5, cont.isTimetable());
            ps.setString(6, cont.getObjectivetype());
          }

          public int getBatchSize() {
            return types.size();
          }
        });
    log.debug("leaving insertIntoStudy with result: {}", () -> ret.length);
    return ret;
  }

  public int[] update(final List<StudyListTypesDTO> types) {
    log.trace("execute update [size: {}]", () -> types.size());
    int[] ret = this.jdbcTemplate.batchUpdate(
        "UPDATE dw_study_listtypes SET  text = ?, sort = ?, timetable = ?, objectivetype = ? WHERE id = ?",
        new BatchPreparedStatementSetter() {
          public void setValues(PreparedStatement ps, int i) throws SQLException {
            StudyListTypesDTO cont = types.get(i);
            ps.setString(1, cont.getText());
            ps.setInt(2, cont.getSort());
            ps.setBoolean(3, cont.isTimetable());
            ps.setString(4, cont.getObjectivetype());
            ps.setLong(5, cont.getId());
          }

          public int getBatchSize() {
            return types.size();
          }
        });
    log.debug("leaving update with result: {}", () -> ret.length);
    return ret;
  }

}
