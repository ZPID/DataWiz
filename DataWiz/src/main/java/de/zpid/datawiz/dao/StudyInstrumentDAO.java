package de.zpid.datawiz.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Scope;
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
            StudyInstrumentDTO dt = (StudyInstrumentDTO) context.getBean("StudyInstrumentDTO");
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

}
