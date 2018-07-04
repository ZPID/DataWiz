package de.zpid.datawiz.dao;

import de.zpid.datawiz.dto.SideMenuDTO;
import de.zpid.datawiz.dto.UserDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * DAO Class for the side menu
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
public class SideMenuDAO {

    private final JdbcTemplate jdbcTemplate;
    private static final Logger log = LogManager.getLogger(SideMenuDAO.class);

    @Autowired
    public SideMenuDAO(JdbcTemplate jdbcTemplate) {
        super();
        log.info("Loading SideMenuDAO as @Scope(\"singleton\") and @Repository");
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Finds all projects which belongs to a user
     *
     * @param user {@link UserDTO} contains user identifier
     * @return {@link List} of {@link SideMenuDTO}
     */
    public List<SideMenuDTO> findProjectsByUser(final UserDTO user) {
        log.trace("Entering findProjectsByUser for user [id: {}; email: {}]", user::getId, user::getEmail);
        String sql = "SELECT dw_project.id, dw_project.title FROM dw_user_roles " + "LEFT JOIN dw_project ON dw_user_roles.project_id = dw_project.id "
                + "LEFT JOIN dw_roles ON dw_user_roles.role_id = dw_roles.id " + "WHERE dw_user_roles.user_id = ? AND dw_user_roles.project_id > 0 "
                + "GROUP BY dw_project.id";
        List<SideMenuDTO> ret = jdbcTemplate.query(sql, new Object[]{user.getId()}, (rs, rowNum) -> {
            SideMenuDTO smdto = new SideMenuDTO();
            smdto.setId(rs.getLong("id"));
            smdto.setTitle(rs.getString("title"));
            return smdto;
        });
        log.debug("Transaction \"findProjectsByUser\" terminates with result: [lenght: {}]", ret::size);
        return ret;
    }

    /**
     * Finds all studies which belongs to a project
     *
     * @param pid long project identifier
     * @return {@link List} of {@link SideMenuDTO}
     */
    public List<SideMenuDTO> findAllStudiesByProjectId(final long pid) {
        log.trace("execute findAllStudiesByProjectId for project [id: {}]", () -> pid);
        String sql = "SELECT id, title FROM dw_study WHERE dw_study.project_id = ?";
        final List<SideMenuDTO> res = jdbcTemplate.query(sql, new Object[]{pid}, (rs, rowNum) -> {
            SideMenuDTO smdto = new SideMenuDTO();
            smdto.setId(rs.getLong("id"));
            smdto.setTitle(rs.getString("title"));
            return smdto;
        });
        log.debug("leaving findAllStudiesByProjectId with size: {}", res::size);
        return res;
    }

    /**
     * Finds a study which belongs to the passed studyId and projectId
     *
     * @param studyId   long study identifier
     * @param projectId long project identifier
     * @return {@link SideMenuDTO}
     */
    public SideMenuDTO findById(final long studyId, final long projectId) {
        log.trace("execute findById for study [id: {}] from project [id: {}]", () -> studyId, () -> projectId);
        final SideMenuDTO res = jdbcTemplate.query("SELECT dw_study.id, dw_study.title FROM dw_study WHERE dw_study.id = ? AND dw_study.project_id = ?",
                new Object[]{studyId, projectId}, rs -> {
                    if (rs.next()) {
                        SideMenuDTO smdto = new SideMenuDTO();
                        smdto.setId(rs.getLong("id"));
                        smdto.setTitle(rs.getString("title"));
                        return smdto;
                    }
                    return null;
                });
        log.debug("leaving findByID with study: {}", () -> res != null ? res.getId() : "NULL");
        return res;
    }

    /**
     * Finds all records which belongs to a study
     *
     * @param studyId long study identifier
     * @return {@link List} of {@link SideMenuDTO}
     */
    public List<SideMenuDTO> findRecordsWithStudyID(final long studyId) {
        log.trace("Entering findRecordsWithStudyID [studyId: {}]", () -> studyId);
        String sql = "SELECT * FROM dw_record WHERE dw_record.study_id  = ?";
        final List<SideMenuDTO> cRecords = this.jdbcTemplate.query(sql, new Object[]{studyId}, (rs, rowNum) -> {
            SideMenuDTO smdto = new SideMenuDTO();
            smdto.setId(rs.getLong("id"));
            smdto.setTitle(rs.getString("name"));
            return smdto;
        });
        log.debug("Transaction \"findRecordsWithStudyID\" terminates with result: [lenght: {}]", cRecords::size);
        return cRecords;
    }

}
