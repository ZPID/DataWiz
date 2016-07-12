package de.zpid.datawiz.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import de.zpid.datawiz.dto.StudyConstructDTO;
import de.zpid.datawiz.enumeration.ConstructTypes;

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
            dt.setType(ConstructTypes.valueOf(rs.getString("type")));
            dt.setOther(rs.getString("other"));
            return dt;
          }
        });
    log.debug("Transaction for findAllByType returned [size: {}]", () -> ret.size());
    return ret;
  }

}
