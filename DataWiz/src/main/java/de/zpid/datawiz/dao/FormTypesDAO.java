package de.zpid.datawiz.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import de.zpid.datawiz.dto.FormTypesDTO;
import de.zpid.datawiz.enumeration.DWFieldTypes;

@Repository
@Scope("singleton")
public class FormTypesDAO extends SuperDAO {
  
  private static Logger log = LogManager.getLogger(FormTypesDAO.class);

  public FormTypesDAO() {
    super();
    if (log.isInfoEnabled())
      log.info("Loading FormTypesDAO as Singleton and Service");
  }

  public List<FormTypesDTO> findAllByType(final boolean active, final DWFieldTypes type) throws Exception {
    log.trace("execute getAllByType [type: {}; active: {}]", () -> type, () -> active);
    String sql = "SELECT * FROM dw_formtypes WHERE dw_formtypes.active = ? AND dw_formtypes.type = ? ORDER BY dw_formtypes.sort";
    final List<FormTypesDTO> ret = jdbcTemplate.query(sql, new Object[] { active, type.toString() },
        new RowMapper<FormTypesDTO>() {
          public FormTypesDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
            FormTypesDTO dt = (FormTypesDTO) context.getBean("FormTypesDTO");
            dt.setId(rs.getInt("id"));
            dt.setNameDE(rs.getString("name_de"));
            dt.setNameEN(rs.getString("name_en"));
            dt.setActive(rs.getBoolean("active"));
            dt.setInvestPresent(rs.getBoolean("investpresent"));
            dt.setSort(rs.getInt("sort"));
            dt.setType(DWFieldTypes.valueOf(rs.getString("type")));
            return dt;
          }
        });
    log.debug("Transaction for getAllByType returned [size: {}]", () -> ret.size());
    return ret;
  }

  public List<Integer> findSelectedFormTypesByIdAndType(final long id, final DWFieldTypes type, final boolean isStudy)
      throws Exception {
    log.trace("execute getSelectedFormTypesByIdAndType for [id: {}; type: {}; isStudy: {}]", () -> id,
        () -> type.name(), () -> isStudy);
    String sql;
    if (isStudy)
      sql = "SELECT dw_study_formtypes.ftid FROM dw_study_formtypes "
          + "LEFT JOIN dw_formtypes ON dw_study_formtypes.ftid = dw_formtypes.id "
          + "WHERE dw_study_formtypes.dmpid = ? AND dw_formtypes.type = ? ";
    else
      sql = "SELECT dw_dmp_formtypes.ftid FROM dw_dmp_formtypes "
          + "LEFT JOIN dw_formtypes ON dw_dmp_formtypes.ftid = dw_formtypes.id "
          + "WHERE dw_dmp_formtypes.dmpid = ? AND dw_formtypes.type = ? ";
    final List<Integer> ret = jdbcTemplate.query(sql, new Object[] { id, type.toString() }, new RowMapper<Integer>() {
      public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
        return rs.getInt("ftid");
      }
    });
    log.debug("Transaction for getSelectedFormTypesByIdAndType returned [size: {}]", () -> ret.size());
    return ret;
  }

  public int deleteSelectedFormType(final long id, final int ftid, final boolean isStudy) throws Exception {
    log.trace("execute deleteSelectedFormType for [id: {}; ftid: {}; isStuddy {}]", () -> id, () -> ftid,
        () -> isStudy);
    int ret;
    if (isStudy)
      ret = this.jdbcTemplate.update("DELETE FROM dw_study_formtypes WHERE dmpid = ? AND ftid = ?", id, ftid);
    else
      ret = this.jdbcTemplate.update("DELETE FROM dw_dmp_formtypes WHERE dmpid = ? AND ftid = ?", id, ftid);
    log.debug("Transaction for deleteSelectedFormType returned [result: {}]", () -> ret);
    return ret;
  }

  public int insertSelectedFormType(final long id, final int ftid, final boolean isStudy) throws Exception {
    log.trace("execute insertSelectedFormType for [id: {}; ftid: {}; isStuddy {}]", () -> id, () -> ftid,
        () -> isStudy);
    int ret;
    if (isStudy)
      ret = this.jdbcTemplate.update("INSERT INTO dw_study_formtypes (dmpid, ftid) VALUES(?,?)", id, ftid);
    else
      ret = this.jdbcTemplate.update("INSERT INTO dw_dmp_formtypes (dmpid, ftid) VALUES(?,?)", id, ftid);
    log.debug("Transaction for insertSelectedFormType returned [result: {}]", () -> ret);
    return ret;
  }
}
