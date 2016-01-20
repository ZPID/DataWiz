package de.zpid.datawiz.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import de.zpid.datawiz.dto.DataTypeDTO;

public class DataTypeDAO {

  private static final Logger log = Logger.getLogger(DataTypeDAO.class);
  private ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");
  private JdbcTemplate jdbcTemplate;

  public DataTypeDAO() {
    super();
  }

  public DataTypeDAO(DataSource dataSource) {
    super();
    this.jdbcTemplate = new JdbcTemplate(dataSource);
  }

  public List<DataTypeDTO> getAllActiv(boolean activ)
      throws Exception {
    if (log.isDebugEnabled())
      log.debug("execute getAllActiv");
    String sql = "SELECT * FROM dw_dmp_datatypes WHERE dw_dmp_datatypes.active = ? ORDER BY dw_dmp_datatypes.id DESC";
    return jdbcTemplate.query(sql, new Object[] { activ }, new RowMapper<DataTypeDTO>() {
      public DataTypeDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        DataTypeDTO dt = (DataTypeDTO) context.getBean("DataTypeDTO");
        dt.setId(rs.getInt("id"));
        dt.setNameDE(rs.getString("name_de"));
        dt.setNameEN(rs.getString("name_en"));
        dt.setActive(rs.getBoolean("active"));
        return dt;
      }
    });
  }

}
