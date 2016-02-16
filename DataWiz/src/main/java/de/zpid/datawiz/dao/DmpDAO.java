package de.zpid.datawiz.dao;

import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;

import de.zpid.datawiz.dto.DmpDTO;
import de.zpid.datawiz.dto.ProjectDTO;

public class DmpDAO {

  private static final Logger log = Logger.getLogger(DmpDAO.class);
  private ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");
  private JdbcTemplate jdbcTemplate;

  public DmpDAO() {
    super();
  }

  public DmpDAO(DataSource dataSource) {
    super();
    this.jdbcTemplate = new JdbcTemplate(dataSource);
  }

  public DmpDTO getByID(ProjectDTO project) throws Exception {
    if (log.isDebugEnabled())
      log.debug("execute getByID [id: " + project.getId() + "]");
    String sql = "SELECT * FROM dw_dmp where id = ?";
    DmpDTO dmp = jdbcTemplate.query(sql, new Object[] { project.getId() }, new ResultSetExtractor<DmpDTO>() {
      @Override
      public DmpDTO extractData(ResultSet rs) throws SQLException, DataAccessException {
        if (rs.next()) {
          DmpDTO dmp = (DmpDTO) context.getBean("DmpDTO");
          dmp.setId(new BigInteger(rs.getBigDecimal("id").toString()));
          // ***************** Administrative Data *****************
          dmp.setProjectAims(rs.getString("projectAims"));
          dmp.setProjectSponsors(rs.getString("projectSponsors"));
          dmp.setDuration(rs.getString("duration"));
          dmp.setOrganizations(rs.getString("organizations"));
          dmp.setPlanAims(rs.getString("planAims"));
          // ***************** Research Data *****************
          // dmp.setExistingData(rs.getString("existingData"));
          // dmp.setDataCitation(rs.getString("dataCitation"));
          // dmp.setExistingDataRelevance(rs.getString("existingDataRelevance"));
          // dmp.setExistingDataIntegration(rs.getString("existingDataIntegration"));

          return dmp;
        }
        return null;
      }
    });
    dmp.setAdminChanged(false);
    if (log.isDebugEnabled())
      log.debug("leaving getByProject with result:" + ((dmp != null) ? dmp.getId() : "null"));
    return dmp;
  }

  public int updateAdminData(DmpDTO dmp) {
    if (log.isDebugEnabled())
      log.debug("execute updateAdminData for [id: " + dmp.getId() + "]");
    return this.jdbcTemplate.update(
        "UPDATE dw_dmp SET projectAims = ?, projectSponsors = ?, duration = ?, organizations = ?, planAims = ? WHERE id = ?",
        dmp.getProjectAims(), dmp.getProjectSponsors(), dmp.getDuration(), dmp.getOrganizations(), dmp.getPlanAims(),
        dmp.getId());
  }

}
