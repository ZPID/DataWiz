package de.zpid.datawiz.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import de.zpid.datawiz.dto.CollectionModeDTO;

public class CollectionModeDAO {

  private static final Logger log = Logger.getLogger(CollectionModeDAO.class);
  private ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");
  private JdbcTemplate jdbcTemplate;

  public CollectionModeDAO() {
    super();
  }

  public CollectionModeDAO(DataSource dataSource) {
    super();
    this.jdbcTemplate = new JdbcTemplate(dataSource);
  }

  public List<CollectionModeDTO> getAllActiv(boolean activ)
      throws Exception {
    if (log.isDebugEnabled())
      log.debug("execute getAllActiv");
    String sql = "SELECT * FROM dw_dmp_collectionmodes WHERE dw_dmp_collectionmodes.active = ? ORDER BY dw_dmp_collectionmodes.sort";
    return jdbcTemplate.query(sql, new Object[] { activ }, new RowMapper<CollectionModeDTO>() {
      public CollectionModeDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        CollectionModeDTO dt = (CollectionModeDTO) context.getBean("CollectionModeDTO");
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

}
