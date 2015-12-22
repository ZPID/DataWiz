package de.zpid.datawiz.dao;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;

import de.zpid.datawiz.dto.ProjectDTO;

public class TagDAO {

  private static final Logger log = Logger.getLogger(TagDAO.class);
  private JdbcTemplate jdbcTemplate;

  public TagDAO() {
    super();
  }

  public TagDAO(DataSource dataSource) {
    super();
    this.jdbcTemplate = new JdbcTemplate(dataSource);
  }

  public HashMap<String, String> getTagsByProjectID(ProjectDTO project) throws Exception {
    if (log.isDebugEnabled())
      log.debug(
          "execute getTagsByProjectID for project [id: " + project.getId() + " name: " + project.getTitle() + "]");
    String sql = "SELECT * FROM dw_tags LEFT JOIN dw_tags_project "
        + "ON dw_tags_project.tag_id = dw_tags.id WHERE dw_tags_project.project_id = ?";
    HashMap<String, String> res = new HashMap<String, String>();
    List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, new Object[] { project.getId() });
    for (Map<String, Object> m : results) {
      res.put(new String(((BigInteger) m.get("id")).toByteArray()), (String) m.get("tag"));
    }
    if (log.isDebugEnabled())
      log.debug("leaving getTagsByProjectID with result length: " + res.size());
    return res;
  }

}
