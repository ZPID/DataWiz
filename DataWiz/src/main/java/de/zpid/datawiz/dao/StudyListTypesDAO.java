package de.zpid.datawiz.dao;

import de.zpid.datawiz.dto.StudyListTypesDTO;
import de.zpid.datawiz.enumeration.DWFieldTypes;
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
 * DAO Class for study list types
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
public class StudyListTypesDAO {

    private final ClassPathXmlApplicationContext applicationContext;
    private final JdbcTemplate jdbcTemplate;

    private static final Logger log = LogManager.getLogger(StudyListTypesDAO.class);

    @Autowired
    public StudyListTypesDAO(ClassPathXmlApplicationContext applicationContext, JdbcTemplate jdbcTemplate) {
        super();
        log.info("Loading StudyListTypesDAO as @Scope(\"singleton\") and @Repository");
        this.applicationContext = applicationContext;
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Finds all list types of a study and a specific DWFieldTypes
     *
     * @param studyId long Study identifier
     * @param type    {@link DWFieldTypes} type identifier
     * @return {@link List} of {@link StudyListTypesDTO}
     */
    public List<StudyListTypesDTO> findAllByStudyAndType(final long studyId, final DWFieldTypes type) {
        log.trace("execute findAllByStudyAndType [id: {}; type: {}]", () -> studyId, type::name);
        String sql = "SELECT * FROM dw_study_listtypes WHERE dw_study_listtypes.studyid = ? AND dw_study_listtypes.type = ?"
                + (type.equals(DWFieldTypes.MEASOCCNAME) ? "ORDER BY sort ASC" : "");
        final List<StudyListTypesDTO> ret = jdbcTemplate.query(sql, new Object[]{studyId, type.toString()},
                (rs, rowNum) -> {
                    StudyListTypesDTO dt = (StudyListTypesDTO) applicationContext.getBean("StudyListTypesDTO");
                    dt.setId(rs.getLong("id"));
                    dt.setStudyid(rs.getLong("studyid"));
                    dt.setText(rs.getString("text"));
                    dt.setType(DWFieldTypes.valueOf(rs.getString("type")));
                    if (type.equals(DWFieldTypes.MEASOCCNAME)) {
                        dt.setSort(rs.getInt("sort"));
                        dt.setTimetable(rs.getBoolean("timetable"));
                    }
                    if (type.equals(DWFieldTypes.OBJECTIVES)) {
                        dt.setObjectivetype(rs.getString("objectivetype"));
                    }
                    return dt;
                });
        log.debug("Transaction for findAllByStudyAndType returned [size: {}]", ret::size);
        return ret;
    }

    /**
     * Deletes list types from a study
     *
     * @param types {@link List} of {@link StudyListTypesDTO}
     * @return amount of changes
     */
    public int[] delete(final List<StudyListTypesDTO> types) {
        log.trace("execute delete [size: {}]", types::size);
        int[] ret = this.jdbcTemplate.batchUpdate("DELETE FROM dw_study_listtypes WHERE id = ?",
                new BatchPreparedStatementSetter() {
                    public void setValues(@Nonnull PreparedStatement ps, int i) throws SQLException {
                        StudyListTypesDTO type = types.get(i);
                        ps.setLong(1, type.getId());
                    }

                    public int getBatchSize() {
                        return types.size();
                    }
                });
        log.debug("leaving deleteFromStudy with result: {}", () -> ret.length);
        return ret;
    }

    /**
     * Inserts list types into a study
     *
     * @param types {@link List} of {@link StudyListTypesDTO}
     * @return amount of changes
     */
    public int[] insert(final List<StudyListTypesDTO> types) {
        log.trace("execute insert [size: {}]", types::size);
        int[] ret = this.jdbcTemplate.batchUpdate(
                "Insert INTO dw_study_listtypes (studyid, text, type, sort, timetable, objectivetype) VALUES (?,?,?,?,?,?)",
                new BatchPreparedStatementSetter() {
                    public void setValues(@Nonnull PreparedStatement ps, int i) throws SQLException {
                        StudyListTypesDTO cont = types.get(i);
                        ps.setLong(1, cont.getStudyid());
                        ps.setString(2, cont.getText());
                        ps.setString(3, cont.getType().name());
                        ps.setInt(4, cont.getSort());
                        ps.setBoolean(5, cont.isTimetable());
                        ps.setString(6, cont.getObjectivetype());
                    }

                    public int getBatchSize() {
                        return types.size();
                    }
                });
        log.debug("leaving insertIntoStudy with result: {}", () -> ret.length);
        return ret;
    }

    /**
     * Updates list types of a study
     *
     * @param types {@link List} of {@link StudyListTypesDTO}
     * @return amount of changes
     */
    public int[] update(final List<StudyListTypesDTO> types) {
        log.trace("execute update [size: {}]", types::size);
        int[] ret = this.jdbcTemplate.batchUpdate(
                "UPDATE dw_study_listtypes SET  text = ?, sort = ?, timetable = ?, objectivetype = ? WHERE id = ?",
                new BatchPreparedStatementSetter() {
                    public void setValues(@Nonnull PreparedStatement ps, int i) throws SQLException {
                        StudyListTypesDTO cont = types.get(i);
                        ps.setString(1, cont.getText());
                        ps.setInt(2, cont.getSort());
                        ps.setBoolean(3, cont.isTimetable());
                        ps.setString(4, cont.getObjectivetype());
                        ps.setLong(5, cont.getId());
                    }

                    public int getBatchSize() {
                        return types.size();
                    }
                });
        log.debug("leaving update with result: {}", () -> ret.length);
        return ret;
    }

}
