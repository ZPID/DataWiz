package de.zpid.datawiz.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import de.zpid.datawiz.dto.ContributorDTO;
import de.zpid.datawiz.dto.ProjectDTO;

@Repository
@Scope("singleton")
public class ContributorDAO extends SuperDAO {
  
  private static Logger log = LogManager.getLogger(ContributorDAO.class);

  public ContributorDAO() {
    super();
    if (log.isInfoEnabled())
      log.info("Loading ContributorDAO as Singleton and Service");
  }

  public List<ContributorDTO> findByProject(final ProjectDTO project, final boolean withStudy,
      final boolean withPrimary) throws Exception {
    log.trace("Entering getByProject for project [id: {}]", () -> project.getId());
    String sql = "SELECT * FROM dw_contributors LEFT JOIN dw_study_contributors "
        + "ON dw_study_contributors.contributor_id = dw_contributors.id WHERE "
        + (withStudy ? "" : "dw_study_contributors.study_id IS NULL AND ")
        + (withPrimary ? "" : "dw_contributors.primaryContributor IS FALSE AND ")
        + "dw_study_contributors.project_id = ? ORDER BY dw_contributors.id DESC";
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
    if (log.isDebugEnabled())
      log.debug("execute findPrimaryContributorByProject for project [id: " + project.getId() + " name: "
          + project.getTitle() + "]");
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
