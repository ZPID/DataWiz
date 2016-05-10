package de.zpid.datawiz.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import de.zpid.datawiz.dto.FormTypesDTO;
import de.zpid.datawiz.enumeration.DWFieldTypes;

@Repository
@Scope("singleton")
public class FormTypesDAO extends SuperDAO {

  public FormTypesDAO() {
    super();
    if (log.isInfoEnabled())
      log.info("Loading FormTypesDAO as Singleton and Service");
  }

  public List<FormTypesDTO> getAllByType(boolean activ, DWFieldTypes type) throws Exception {
    if (log.isDebugEnabled())
      log.debug("execute getAllByType [type=" + type.toString() + "]");
    String sql = "SELECT * FROM dw_formtypes WHERE dw_formtypes.active = ? AND dw_formtypes.type = ? ORDER BY dw_formtypes.sort";
    return jdbcTemplate.query(sql, new Object[] { activ, type.toString() }, new RowMapper<FormTypesDTO>() {
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
  }
}
