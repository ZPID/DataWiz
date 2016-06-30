package de.zpid.datawiz.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import de.zpid.datawiz.dto.StudyObjectivesDTO;
import de.zpid.datawiz.enumeration.ObjectiveTypes;

@Repository
@Scope("singleton")
public class StudyObjectivesDAO extends SuperDAO {
  
  private static Logger log = LogManager.getLogger(StudyObjectivesDAO.class);

  public StudyObjectivesDAO() {
    super();
    if (log.isInfoEnabled())
      log.info("Loading StudyObjectivesDAO as Singleton and Service");
  }

  public List<StudyObjectivesDTO> findAllByStudy(final long studyId) throws Exception {
    log.trace("execute findAllByStudy [id: {}]", () -> studyId);
    String sql = "SELECT * FROM dw_study_objectives WHERE dw_study_objectives.study_id = ?";
    final List<StudyObjectivesDTO> ret = jdbcTemplate.query(sql, new Object[] { studyId },
        new RowMapper<StudyObjectivesDTO>() {
          public StudyObjectivesDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
            StudyObjectivesDTO dt = (StudyObjectivesDTO) applicationContext.getBean("StudyObjectivesDTO");
            dt.setId(rs.getInt("id"));
            dt.setStudyId(rs.getLong("study_id"));
            dt.setObjective(rs.getString("objective"));
            dt.setType(ObjectiveTypes.valueOf(rs.getString("type")));
            return dt;
          }
        });
    log.debug("Transaction for findAllByStudy returned [size: {}]", () -> ret.size());
    return ret;
  }

}
