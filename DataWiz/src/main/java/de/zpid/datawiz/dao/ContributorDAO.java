package de.zpid.datawiz.dao;

import de.zpid.datawiz.dto.ContributorDTO;
import de.zpid.datawiz.dto.ProjectDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.annotation.Nonnull;
import java.sql.*;
import java.util.List;

/**
 * DAO Class for Contributor
 * <p>
 * This file is part of the DataWiz distribution (https://github.com/ZPID/DataWiz).
 * Copyright (c) 2018 <a href="https://leibniz-psychology.org/">Leibniz Institute for Psychology Information (ZPID)</a>.
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 * <p>
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <a href="http://www.gnu.org/licenses/">http://www.gnu.org/licenses/</a>.
 *
 * @author Ronny Boelter
 * @version 1.0
 **/
@Repository
@Scope("singleton")
public class ContributorDAO {

    private final ClassPathXmlApplicationContext applicationContext;
    private final JdbcTemplate jdbcTemplate;
    private static final Logger log = LogManager.getLogger(ContributorDAO.class);

    /**
     * Instantiates a new ContributorDAO.
     */
    @Autowired
    public ContributorDAO(ClassPathXmlApplicationContext applicationContext, JdbcTemplate jdbcTemplate) {
        super();
        log.info("Loading ContributorDAO as @Scope(\"singleton\") and @Repository");
        this.applicationContext = applicationContext;
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Finds all contributors by project identifier with or without relations to study and with or without primary contributor.
     *
     * @param project     {@link ProjectDTO} Contains the project data
     * @param withStudy   Select between all contributor entries project wide (true) or only contributor which are saved in in the project meta data (false).
     * @param withPrimary Include primary contributor (true) otherwise false
     * @return {@link List} of {@link ContributorDTO}
     */
    public List<ContributorDTO> findByProject(final ProjectDTO project, final boolean withStudy, final boolean withPrimary) {
        log.trace("Entering getByProject for project [id: {}]", project::getId);
        String sql = "SELECT * FROM dw_contributors LEFT JOIN dw_study_contributors " + "ON dw_study_contributors.contributor_id = dw_contributors.id WHERE "
                + (withStudy ? "" : "dw_study_contributors.study_id IS NULL AND ") + (withPrimary ? "" : "dw_contributors.primaryContributor IS FALSE AND ")
                + "dw_study_contributors.project_id = ? ORDER BY dw_contributors.sort ASC";
        List<ContributorDTO> cContri = jdbcTemplate.query(sql, new Object[]{project.getId()}, (resultSet, rowNum) -> setContributorDTO(resultSet));
        log.debug("Transaction \"getByProject\" terminates with result: [length: {}]", cContri::size);
        return cContri;
    }

    /**
     * Finds all contributor by study identifier.
     *
     * @param studyId Study Identifier as long
     * @return {@link List} of {@link ContributorDTO}
     */
    public List<ContributorDTO> findByStudy(final long studyId) {
        log.trace("Entering findByStudy for project [id: {}]", () -> studyId);
        String sql = "SELECT * FROM dw_contributors LEFT JOIN dw_study_contributors " + "ON dw_study_contributors.contributor_id = dw_contributors.id WHERE "
                + "dw_study_contributors.study_id = ? ORDER BY dw_contributors.id DESC";
        List<ContributorDTO> cContri = jdbcTemplate.query(sql, new Object[]{studyId}, (resultSet, rowNum) -> setContributorDTO(resultSet));
        log.debug("Transaction \"findByStudy\" terminates with result: [length: {}]", cContri::size);
        return cContri;
    }

    /**
     * Finds primary contributor by project identifier.
     *
     * @param project {@link ProjectDTO} Contains the project data
     * @return {@link ContributorDTO}
     */
    public ContributorDTO findPrimaryContributorByProject(final ProjectDTO project) {
        log.trace("Entering findPrimaryContributorByProject for project [id: {}, name: {}]", project::getId, project::getTitle);
        String sql = "SELECT * FROM dw_contributors " +
                "  LEFT JOIN dw_study_contributors ON dw_study_contributors.contributor_id = dw_contributors.id" +
                "  WHERE dw_contributors.primaryContributor IS TRUE" +
                "        AND dw_study_contributors.study_id is NULL " +
                "        AND dw_study_contributors.project_id = ?" +
                "  GROUP BY contributor_id, project_id, study_id";
        ContributorDTO contri;
        try {
            contri = jdbcTemplate.queryForObject(sql, new Object[]{project.getId()}, (resultSet, rowNum) -> setContributorDTO(resultSet));
        } catch (EmptyResultDataAccessException e) {
            contri = null;
        }
        log.debug("Transaction \"findPrimaryContributorByProject\" terminates with result: [ContributorDTO: {}]", contri);
        return contri;
    }

    /**
     * Deletes contributor relation from project.
     *
     * @param contri {@link ContributorDTO} Contains the to be deleted Contributor
     * @return Amount of Changes (int)
     */
    @SuppressWarnings("unused")
    public int deleteFromProject(final ContributorDTO contri) {
        log.trace("Entering deleteFromProject [projectId: {}; contributorId: {}]", contri::getProjectId, contri::getId);
        int chk = this.jdbcTemplate.update("DELETE FROM dw_study_contributors WHERE project_id = ? AND contributor_id= ?", contri.getProjectId(), contri.getId());
        log.debug("Transaction \"deleteFromProject\" terminates with result: [deleted: {}]", () -> chk);
        return chk;
    }

    /**
     * Delete contributor.
     *
     * @param contri {@link ContributorDTO} Contains the to be deleted Contributor
     * @return Amount of Changes (int)
     */
    public int deleteContributor(final ContributorDTO contri) {
        log.trace("Entering deleteContributor [contributorId: {}]", contri::getId);
        int chk = this.jdbcTemplate.update("DELETE FROM dw_contributors WHERE id= ?", contri.getId());
        log.debug("Transaction \"deleteContributor\" terminates with result: [deleted: {}]", () -> chk);
        return chk;
    }

    /**
     * Delete a list of contributor relations from study.
     *
     * @param contri List of {@link ContributorDTO}
     */
    public void deleteFromStudy(final List<ContributorDTO> contri) {
        log.trace("Entering deleteFromStudy [size: {}]", contri::size);
        int[] ret = this.jdbcTemplate.batchUpdate("DELETE FROM dw_study_contributors WHERE project_id = ? AND study_id = ? AND contributor_id= ?",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(@Nonnull PreparedStatement ps, int i) throws SQLException {
                        ContributorDTO cont = contri.get(i);
                        ps.setLong(1, cont.getProjectId());
                        ps.setLong(2, cont.getStudyId());
                        ps.setLong(3, cont.getId());
                    }

                    public int getBatchSize() {
                        return contri.size();
                    }
                });
        log.debug("Transaction \"deleteFromStudy\" terminates with result: [deleted: {}]", () -> ret.length);
    }

    /**
     * Insert study relation.
     *
     * @param contri  List of {@link ContributorDTO}
     * @param studyId Study Identifier as long
     */
    public void insertStudyRelation(final List<ContributorDTO> contri, final Long studyId) {
        log.trace("Entering insertIntoStudy [size: {}]", contri::size);
        int[] ret = this.jdbcTemplate.batchUpdate("INSERT INTO dw_study_contributors (project_id, study_id, contributor_id) VALUES (?,?,?)",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(@Nonnull PreparedStatement ps, int i) throws SQLException {
                        ContributorDTO cont = contri.get(i);
                        ps.setLong(1, cont.getProjectId());
                        ps.setLong(2, studyId);
                        ps.setLong(3, cont.getId());
                    }

                    public int getBatchSize() {
                        return contri.size();
                    }
                });
        log.debug("Transaction \"insertStudyRelation\" terminates with result: [size: {}]", () -> ret.length);
    }

    /**
     * Insert contributor.
     *
     * @param contri {@link ContributorDTO}
     */
    public void insertContributor(final ContributorDTO contri) {
        log.trace("Entering insertContributor [contributor: {}]", () -> contri);
        KeyHolder holder = new GeneratedKeyHolder();
        final String stmt = "INSERT INTO dw_contributors (sort, title, first_name, last_name, institution, department, orcid, primaryContributor) "
                + "VALUES (?,?,?,?,?,?,?,?)";
        int chk = this.jdbcTemplate.update((Connection connection) -> {
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
        }, holder);
        contri.setId((holder.getKey() != null && holder.getKey().longValue() > 0) ? holder.getKey().longValue() : -1);
        log.debug("Transaction \"insertContributor\" terminates with result: [success: {}]", () -> chk);
    }

    /**
     * Insert project relation.
     *
     * @param contri {@link ContributorDTO}
     */
    public void insertProjectRelation(ContributorDTO contri) {
        log.trace("Entering insertIntoProject [projectID: {}, contributor: {}]", contri::getProjectId, () -> contri);
        int ret = this.jdbcTemplate.update("INSERT INTO dw_study_contributors (project_id, contributor_id) VALUES (?,?)", contri.getProjectId(), contri.getId());
        log.debug("Transaction \"insertProjectRelation\" terminates with result: [success: {}]", () -> ret);
    }

    /**
     * Update contributor.
     *
     * @param contri {@link ContributorDTO}
     */
    public void updateContributor(ContributorDTO contri) {
        log.trace("Entering updateContributor [projectID: {}, contributor: {}]", contri::getProjectId, () -> contri);
        int ret = this.jdbcTemplate.update(
                "UPDATE dw_contributors SET title = ?, first_name = ?, last_name = ?, institution = ?, department = ?, orcid = ?, sort = ? WHERE dw_contributors.id = ?",
                contri.getTitle(), contri.getFirstName(), contri.getLastName(), contri.getInstitution(), contri.getDepartment(), contri.getOrcid(), contri.getSort(),
                contri.getId());
        log.debug("Transaction \"updateContributor\" terminates with result: [success: {}]", () -> ret);
    }

    /**
     * Sets the contributorDTO.
     *
     * @param rs ResultSet
     * @return ContributorDTO
     * @throws SQLException DBS Exceptions
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
        contri.setSort(rs.getInt("sort"));
        return contri;
    }

}
