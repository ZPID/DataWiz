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

import de.zpid.datawiz.dto.FormTypesDTO;
import de.zpid.datawiz.enumeration.DWFieldTypes;

@Repository
@Scope("singleton")
public class FormTypesDAO {

  @Autowired
  protected ClassPathXmlApplicationContext applicationContext;
  @Autowired
  protected JdbcTemplate jdbcTemplate;

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
            FormTypesDTO dt = (FormTypesDTO) applicationContext.getBean("FormTypesDTO");
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
          + "WHERE dw_study_formtypes.studyid = ? AND dw_formtypes.type = ? ";
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

  public int[] deleteSelectedFormType(final long dmpOrStudyID, final List<Integer> types, final boolean isStudy) {
    log.trace("execute deleteSelectedFormType [size: {}]", () -> types.size());
    String query = null;
    if (isStudy)
      query = "DELETE FROM dw_study_formtypes WHERE studyid = ? AND ftid = ?";
    else
      query = "DELETE FROM dw_dmp_formtypes WHERE dmpid = ? AND ftid = ?";
    int[] ret = this.jdbcTemplate.batchUpdate(query, new BatchPreparedStatementSetter() {
      public void setValues(PreparedStatement ps, int i) throws SQLException {
        ps.setLong(1, dmpOrStudyID);
        ps.setLong(2, types.get(i));
      }

      public int getBatchSize() {
        return types.size();
      }
    });
    log.debug("leaving deleteSelectedFormType with result: {}", () -> ret.length);
    return ret;
  }

  public int[] insertSelectedFormType(final long dmpOrStudyID, final List<Integer> types, final boolean isStudy) {
    log.trace("execute insertSelectedFormType [size: {}]", () -> types.size());
    String query = null;
    if (isStudy)
      query = "INSERT INTO dw_study_formtypes (studyid, ftid) VALUES(?,?)";
    else
      query = "INSERT INTO dw_dmp_formtypes (dmpid, ftid) VALUES(?,?)";
    int[] ret = this.jdbcTemplate.batchUpdate(query, new BatchPreparedStatementSetter() {
      public void setValues(PreparedStatement ps, int i) throws SQLException {
        ps.setLong(1, dmpOrStudyID);
        ps.setLong(2, types.get(i));
      }

      public int getBatchSize() {
        return types.size();
      }
    });
    log.debug("leaving insertSelectedFormType with result: {}", () -> ret.length);
    return ret;
  }
}
