package de.zpid.datawiz.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import de.zpid.datawiz.dto.StudyListTypesDTO;
import de.zpid.datawiz.enumeration.DWFieldTypes;

@Repository
@Scope("singleton")
public class StudyListTypesDAO extends SuperDAO {

  public StudyListTypesDAO() {
    super();
    if (log.isInfoEnabled())
      log.info("Loading StudyListTypesDAO as Singleton and Service");
  }

  public List<StudyListTypesDTO> getAllByStudyAndType(final long studyId, final DWFieldTypes type) throws Exception {
    log.trace("execute getAllByType [id: {}; type: {}]", () -> studyId, () -> type.name());
    String sql = "SELECT * FROM dw_study_listtypes WHERE dw_study_listtypes.studyid = ? AND dw_study_listtypes.type = ?";
    final List<StudyListTypesDTO> ret = jdbcTemplate.query(sql, new Object[] { studyId, type.toString() },
        new RowMapper<StudyListTypesDTO>() {
          public StudyListTypesDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
            StudyListTypesDTO dt = (StudyListTypesDTO) context.getBean("StudyListTypesDTO");
            dt.setId(rs.getInt("id"));
            dt.setStudyid(rs.getLong("studyid"));
            dt.setText(rs.getString("text"));
            dt.setType(DWFieldTypes.valueOf(rs.getString("type")));
            return dt;
          }
        });
    log.debug("Transaction for getAllByType returned [size: {}]", () -> ret.size());
    return ret;
  }

}
