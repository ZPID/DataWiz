package de.zpid.datawiz.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import de.zpid.datawiz.dto.DmpRelTypeDTO;

public class DmpRelTypeDAO {

  private static final Logger log = Logger.getLogger(DmpRelTypeDAO.class);
  private ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");
  private JdbcTemplate jdbcTemplate;

  public DmpRelTypeDAO() {
    super();
  }

  public DmpRelTypeDAO(DataSource dataSource) {
    super();
    this.jdbcTemplate = new JdbcTemplate(dataSource);
  }

  public List<DmpRelTypeDTO> getAllActivDataTypes(boolean activ)
      throws Exception {
    if (log.isDebugEnabled())
      log.debug("execute getAllActiv");
    String sql = "SELECT * FROM dw_dmp_datatypes WHERE dw_dmp_datatypes.active = ? ORDER BY dw_dmp_datatypes.sort";
    return jdbcTemplate.query(sql, new Object[] { activ }, new RowMapper<DmpRelTypeDTO>() {
      public DmpRelTypeDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        DmpRelTypeDTO dt = (DmpRelTypeDTO) context.getBean("DmpRelTypeDTO");
        dt.setId(rs.getInt("id"));
        dt.setNameDE(rs.getString("name_de"));
        dt.setNameEN(rs.getString("name_en"));
        dt.setActive(rs.getBoolean("active"));
        dt.setSort(rs.getInt("sort"));
        return dt;
      }
    });
  }
  
  public List<DmpRelTypeDTO> getAllActivCollectionModes(boolean activ)
      throws Exception {
    if (log.isDebugEnabled())
      log.debug("execute getAllActiv");
    String sql = "SELECT * FROM dw_dmp_collectionmodes WHERE dw_dmp_collectionmodes.active = ? ORDER BY dw_dmp_collectionmodes.sort";
    return jdbcTemplate.query(sql, new Object[] { activ }, new RowMapper<DmpRelTypeDTO>() {
      public DmpRelTypeDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        DmpRelTypeDTO dt = (DmpRelTypeDTO) context.getBean("DmpRelTypeDTO");
        dt.setId(rs.getInt("id"));
        dt.setNameDE(rs.getString("name_de"));
        dt.setNameEN(rs.getString("name_en"));
        dt.setInvestPresent(rs.getBoolean("investpresent"));
        dt.setActive(rs.getBoolean("active"));
        dt.setSort(rs.getInt("sort"));
        return dt;
      }
    });
  }
  
  public List<DmpRelTypeDTO> getAllActivMetaPurposes(boolean activ)
      throws Exception {
    if (log.isDebugEnabled())
      log.debug("execute getAllActiv");
    String sql = "SELECT * FROM dw_dmp_metaporpose WHERE dw_dmp_metaporpose.active = ? ORDER BY dw_dmp_metaporpose.sort";
    return jdbcTemplate.query(sql, new Object[] { activ }, new RowMapper<DmpRelTypeDTO>() {
      public DmpRelTypeDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        DmpRelTypeDTO dt = (DmpRelTypeDTO) context.getBean("DmpRelTypeDTO");
        dt.setId(rs.getInt("id"));
        dt.setNameDE(rs.getString("name_de"));
        dt.setNameEN(rs.getString("name_en"));
        dt.setActive(rs.getBoolean("active"));
        dt.setSort(rs.getInt("sort"));
        return dt;
      }
    });
  }

}
