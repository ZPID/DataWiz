package de.zpid.datawiz.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import de.zpid.datawiz.dto.StudyListTypesDTO;
import de.zpid.datawiz.enumeration.DWFieldTypes;

@Repository
@Scope("singleton")
public class StudyListTypesDAO extends SuperDAO {
  
  private static Logger log = LogManager.getLogger(StudyListTypesDAO.class);

  public StudyListTypesDAO() {
    super();
    if (log.isInfoEnabled())
      log.info("Loading StudyListTypesDAO as Singleton and Service");
  }

  public List<StudyListTypesDTO> findAllByStudyAndType(final long studyId, final DWFieldTypes type) throws Exception {
    log.trace("execute findAllByStudyAndType [id: {}; type: {}]", () -> studyId, () -> type.name());
    String sql = "SELECT * FROM dw_study_listtypes WHERE dw_study_listtypes.studyid = ? AND dw_study_listtypes.type = ?";
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
            return dt;
          }
        });
    log.debug("Transaction for findAllByStudyAndType returned [size: {}]", () -> ret.size());
    return ret;
  }

}
