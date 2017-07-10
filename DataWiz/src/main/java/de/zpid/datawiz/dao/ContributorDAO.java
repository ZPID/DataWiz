package de.zpid.datawiz.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import de.zpid.datawiz.dto.ContributorDTO;
import de.zpid.datawiz.dto.ProjectDTO;

@Repository
@Scope("singleton")
public class ContributorDAO {

  @Autowired
  protected ClassPathXmlApplicationContext applicationContext;
  @Autowired
  protected JdbcTemplate jdbcTemplate;

  private static Logger log = LogManager.getLogger(ContributorDAO.class);

  public ContributorDAO() {
    super();
    if (log.isInfoEnabled())
      log.info("Loading ContributorDAO as Singleton and Repository");
  }

  public List<ContributorDTO> findByProject(final ProjectDTO project, final boolean withStudy,
      final boolean withPrimary) throws Exception {
    log.trace("Entering getByProject for project [id: {}]", () -> project.getId());
    String sql = "SELECT * FROM dw_contributors LEFT JOIN dw_study_contributors "
        + "ON dw_study_contributors.contributor_id = dw_contributors.id WHERE "
        + (withStudy ? "" : "dw_study_contributors.study_id IS NULL AND ")
        + (withPrimary ? "" : "dw_contributors.primaryContributor IS FALSE AND ")
        + "dw_study_contributors.project_id = ? ORDER BY dw_contributors.sort ASC";
    List<ContributorDTO> cContri = jdbcTemplate.query(sql, new Object[] { project.getId() },
        new RowMapper<ContributorDTO>() {
          public ContributorDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
            return setContributorDTO(rs);
          }
        });
    log.debug("Transaction getByProject returned result length: [{}]",
        () -> ((cContri != null) ? cContri.size() : "null"));
    return cContri;
  }

  public List<ContributorDTO> findByStudy(final long studyId) throws Exception {
    log.trace("Entering findByStudy for project [id: {}]", () -> studyId);
    String sql = "SELECT * FROM dw_contributors LEFT JOIN dw_study_contributors "
        + "ON dw_study_contributors.contributor_id = dw_contributors.id WHERE "
        + "dw_study_contributors.study_id = ? ORDER BY dw_contributors.id DESC";
    List<ContributorDTO> cContri = jdbcTemplate.query(sql, new Object[] { studyId }, new RowMapper<ContributorDTO>() {
      public ContributorDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        return setContributorDTO(rs);
      }
    });
    log.debug("Transaction findByStudy returned result length: [{}]",
        () -> ((cContri != null) ? cContri.size() : "null"));
    return cContri;
  }

  public ContributorDTO findPrimaryContributorByProject(ProjectDTO project) throws Exception {
    log.trace("execute findPrimaryContributorByProject for project [id: {}, name: {}]", () -> project.getId(),
        () -> project.getTitle());
    String sql = "SELECT * FROM dw_contributors LEFT JOIN dw_study_contributors "
        + "ON dw_study_contributors.contributor_id = dw_contributors.id WHERE "
        + "dw_contributors.primaryContributor IS TRUE AND dw_study_contributors.project_id = ?";
    ContributorDTO contri = jdbcTemplate.query(sql, new Object[] { project.getId() },
        new ResultSetExtractor<ContributorDTO>() {
          @Override
          public ContributorDTO extractData(ResultSet rs) throws SQLException, DataAccessException {
            if (rs.next()) {
              return setContributorDTO(rs);
            }
            return null;
          }
        });
    if (log.isDebugEnabled())
      log.debug("leaving findPrimaryContributorByProject with contributor: " + contri);
    return contri;
  }

  public int deleteFromProject(ContributorDTO contri) throws Exception {
    log.trace("execute deleteFromProject [projectId: {}; contributorId: {}]", () -> contri.getProjectId(),
        () -> contri.getId());
    int chk = this.jdbcTemplate.update("DELETE FROM dw_study_contributors WHERE project_id = ? AND contributor_id= ?",
        contri.getProjectId(), contri.getId());
    log.debug("leaving deleteFromProject with result: [success: {}]", () -> chk);
    return chk;
  }

  public int deleteContributor(ContributorDTO contri) throws Exception {
    log.trace("execute deleteContributor [contributorId: {}]", () -> contri.getId());
    int chk = this.jdbcTemplate.update("DELETE FROM dw_contributors WHERE id= ?", contri.getId());
    log.debug("leaving deleteContributor with result: [success: {}]", () -> chk);
    return chk;
  }

  public int[] deleteFromStudy(final List<ContributorDTO> contri) throws Exception {
    log.trace("execute deleteFromStudy [size: {}]", () -> contri.size());
    int[] ret = this.jdbcTemplate.batchUpdate(
        "DELETE FROM dw_study_contributors WHERE project_id = ? AND study_id = ? AND contributor_id= ?",
        new BatchPreparedStatementSetter() {
          public void setValues(PreparedStatement ps, int i) throws SQLException {
            ContributorDTO cont = contri.get(i);
            ps.setLong(1, cont.getProjectId());
            ps.setLong(2, cont.getStudyId());
            ps.setLong(3, cont.getId());
          }

          public int getBatchSize() {
            return contri.size();
          }
        });
    log.debug("leaving deleteFromStudy with result: [size: {}]", () -> ret.length);
    return ret;
  }

  public int[] insertStudyRelation(final List<ContributorDTO> contri, final Long studyId) throws Exception {
    log.trace("execute insertIntoStudy [size: {}]", () -> contri.size());
    int[] ret = this.jdbcTemplate.batchUpdate(
        "INSERT INTO dw_study_contributors (project_id, study_id, contributor_id) VALUES (?,?,?)",
        new BatchPreparedStatementSetter() {
          public void setValues(PreparedStatement ps, int i) throws SQLException {
            ContributorDTO cont = contri.get(i);
            ps.setLong(1, cont.getProjectId());
            ps.setLong(2, studyId);
            ps.setLong(3, cont.getId());
          }

          public int getBatchSize() {
            return contri.size();
          }
        });
    log.debug("leaving insertIntoStudy with result: [size: {}]", () -> ret.length);
    return ret;
  }

  // TODO
  public int insertContributor(final ContributorDTO contri) throws Exception {
    log.trace("execute insertContributor [contributor: {}]", () -> contri);
    KeyHolder holder = new GeneratedKeyHolder();
    final String stmt = "INSERT INTO dw_contributors (sort, title, first_name, last_name, institution, department, orcid, primaryContributor) "
        + "VALUES (?,?,?,?,?,?,?,?)";
    int chk = this.jdbcTemplate.update(new PreparedStatementCreator() {
      @Override
      public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(stmt, Statement.RETURN_GENERATED_KEYS);
        ps.setInt(1, contri.getSort());
        ps.setString(2, contri.getTitle());
        ps.setString(3, contri.getFirstName());
        ps.setString(4, contri.getLastName());
        ps.setString(5, contri.getInstitution());
        ps.setString(6, contri.getDepartment());
        ps.setString(7, contri.getOrcid());
        ps.setBoolean(8, contri.getPrimaryContributor() != null ? contri.getPrimaryContributor() : false);
        return ps;
      }
    }, holder);
    contri.setId((holder.getKey().longValue() > 0) ? holder.getKey().longValue() : -1);
    log.debug("leaving insertContributor with result: [success: {}]", () -> chk);
    return chk;
  }

  public int insertProjectRelation(ContributorDTO contri) throws Exception {
    log.trace("execute insertIntoProject [projectID: {}, contributor: {}]", () -> contri.getProjectId(), () -> contri);
    int ret = this.jdbcTemplate.update("INSERT INTO dw_study_contributors (project_id, contributor_id) VALUES (?,?)",
        contri.getProjectId(), contri.getId());
    log.debug("leaving insertIntoProject with result: [success: {}]", () -> ret);
    return ret;
  }

  public int updateContributor(ContributorDTO contri) throws Exception {
    log.trace("execute updateContributor [projectID: {}, contributor: {}]", () -> contri.getProjectId(), () -> contri);
    int ret = this.jdbcTemplate.update(
        "UPDATE dw_contributors SET title = ?, first_name = ?, last_name = ?, institution = ?, department = ?, orcid = ? WHERE dw_contributors.id = ?",
        contri.getTitle(), contri.getFirstName(), contri.getLastName(), contri.getInstitution(), contri.getDepartment(),
        contri.getOrcid(), contri.getId());
    log.debug("leaving updateContributor with result: [success: {}]", () -> ret);
    return ret;
  }

  /**
   * @param rs
   * @return
   * @throws SQLException
   */
  private ContributorDTO setContributorDTO(ResultSet rs) throws SQLException {
    ContributorDTO contri = (ContributorDTO) applicationContext.getBean("ContributorDTO");
    contri.setId(rs.getInt("id"));
    contri.setProjectId(rs.getInt("project_id"));
    contri.setStudyId(rs.getInt("study_id"));
    contri.setTitle(rs.getString("title"));
    contri.setFirstName(rs.getString("first_name"));
    contri.setLastName(rs.getString("last_name"));
    contri.setInstitution(rs.getString("institution"));
    contri.setDepartment(rs.getString("department"));
    contri.setOrcid(rs.getString("orcid"));
    contri.setPrimaryContributor(rs.getBoolean("primaryContributor"));
    return contri;
  }

}
