package de.zpid.datawiz.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import de.zpid.datawiz.dto.FormTypesDTO;
import de.zpid.datawiz.util.DelType;

public class FormTypesDAO {

  private static final Logger log = Logger.getLogger(FormTypesDAO.class);
  private ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");
  private JdbcTemplate jdbcTemplate;

  public FormTypesDAO() {
    super();
  }

  public FormTypesDAO(DataSource dataSource) {
    super();
    this.jdbcTemplate = new JdbcTemplate(dataSource);
  }

  public List<FormTypesDTO> getAllByType(boolean activ, DelType type) throws Exception {
    if (log.isDebugEnabled())
      log.debug("execute getAllByType");
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
        dt.setType(DelType.valueOf(rs.getString("type")));
        return dt;
      }
    });
  }
}
