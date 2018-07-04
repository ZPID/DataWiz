package de.zpid.datawiz.dao;

import de.zpid.datawiz.dto.StudyConstructDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Nonnull;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;


/**
 * DAO Class for study constructs
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
public class StudyConstructDAO {

    private final ClassPathXmlApplicationContext applicationContext;
    private final JdbcTemplate jdbcTemplate;

    private static final Logger log = LogManager.getLogger(StudyConstructDAO.class);

    @Autowired
    public StudyConstructDAO(JdbcTemplate jdbcTemplate, ClassPathXmlApplicationContext applicationContext) {
        super();
        log.info("Loading StudyConstructDAO as @Scope(\"singleton\") and @Repository");
        this.jdbcTemplate = jdbcTemplate;
        this.applicationContext = applicationContext;
    }

    /**
     * Finds all constructs which belong to a study
     *
     * @param studyId long Study identifier
     * @return {@link List} of {@link StudyConstructDTO}
     */
    public List<StudyConstructDTO> findAllByStudy(final long studyId) {
        log.trace("execute findAllByType [id: {}]", () -> studyId);
        String sql = "SELECT * FROM dw_study_constructs WHERE dw_study_constructs.study_id = ?";
        final List<StudyConstructDTO> ret = jdbcTemplate.query(sql, new Object[]{studyId},
                (rs, rowNum) -> {
                    StudyConstructDTO dt = (StudyConstructDTO) applicationContext.getBean("StudyConstructDTO");
                    dt.setId(rs.getLong("id"));
                    dt.setStudyId(rs.getLong("study_id"));
                    dt.setName(rs.getString("name"));
                    dt.setType(rs.getString("type"));
                    dt.setOther(rs.getString("other"));
                    return dt;
                });
        log.debug("Transaction for findAllByType returned [size: {}]", ret::size);
        return ret;
    }

    /**
     * Deletes study constructs
     *
     * @param types {@link List} of {@link StudyConstructDTO}
     * @return Amount of changes
     */
    public int[] delete(final List<StudyConstructDTO> types) {
        log.trace("execute delete [size: {}]", types::size);
        int[] ret = this.jdbcTemplate.batchUpdate("DELETE FROM dw_study_constructs WHERE id = ?",
                new BatchPreparedStatementSetter() {
                    public void setValues(@Nonnull PreparedStatement ps, int i) throws SQLException {
                        StudyConstructDTO type = types.get(i);
                        ps.setLong(1, type.getId());
                    }

                    public int getBatchSize() {
                        return types.size();
                    }
                });
        log.debug("leaving delete with result: {}", () -> ret.length);
        return ret;
    }

    /**
     * Inserts study constructs
     *
     * @param types {@link List} of {@link StudyConstructDTO}
     * @return Amount of changes
     */
    public int[] insert(final List<StudyConstructDTO> types) {
        log.trace("execute insert [size: {}]", types::size);
        int[] ret = this.jdbcTemplate.batchUpdate(
                "Insert INTO dw_study_constructs (study_id, name, type, other) VALUES (?,?,?,?)",
                new BatchPreparedStatementSetter() {
                    public void setValues(@Nonnull PreparedStatement ps, int i) throws SQLException {
                        StudyConstructDTO cont = types.get(i);
                        ps.setLong(1, cont.getStudyId());
                        ps.setString(2, cont.getName());
                        ps.setString(3, cont.getType());
                        ps.setString(4, cont.getOther());
                    }

                    public int getBatchSize() {
                        return types.size();
                    }
                });
        log.debug("leaving insert with result: {}", () -> ret.length);
        return ret;
    }

    /**
     * Updates study constructs
     *
     * @param types {@link List} of {@link StudyConstructDTO}
     * @return Amount of changes
     */
    public int[] update(final List<StudyConstructDTO> types) {
        log.trace("execute update [size: {}]", types::size);
        int[] ret = this.jdbcTemplate.batchUpdate(
                "UPDATE dw_study_constructs SET  name = ?, type = ?, other = ? WHERE id = ?",
                new BatchPreparedStatementSetter() {
                    public void setValues(@Nonnull PreparedStatement ps, int i) throws SQLException {
                        StudyConstructDTO cont = types.get(i);
                        ps.setString(1, cont.getName());
                        ps.setString(2, cont.getType());
                        ps.setString(3, cont.getOther());
                        ps.setLong(4, cont.getId());
                    }

                    public int getBatchSize() {
                        return types.size();
                    }
                });
        log.debug("leaving update with result: {}", () -> ret.length);
        return ret;
    }

}
