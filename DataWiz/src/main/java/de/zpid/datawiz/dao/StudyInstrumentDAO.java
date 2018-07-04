package de.zpid.datawiz.dao;

import de.zpid.datawiz.dto.StudyInstrumentDTO;
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
 * DAO Class for study instruments
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
public class StudyInstrumentDAO {

    private final ClassPathXmlApplicationContext applicationContext;
    private final JdbcTemplate jdbcTemplate;

    private static final Logger log = LogManager.getLogger(StudyInstrumentDAO.class);

    @Autowired
    public StudyInstrumentDAO(ClassPathXmlApplicationContext applicationContext, JdbcTemplate jdbcTemplate) {
        super();
        log.info("Loading StudyInstrumentDAO as @Scope(\"singleton\") and @Repository");
        this.applicationContext = applicationContext;
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Finds all instruments of a study
     *
     * @param studyId   long study identifier
     * @param onlyTitle if true only title is set to {@link StudyInstrumentDTO}
     * @return {@link List} of {@link StudyInstrumentDTO}
     */
    public List<StudyInstrumentDTO> findAllByStudy(final long studyId, final boolean onlyTitle) {
        log.trace("execute findAllByType [id: {}]", () -> studyId);
        String sql = "SELECT * FROM dw_study_instruments WHERE dw_study_instruments.study_id = ?";
        final List<StudyInstrumentDTO> ret = jdbcTemplate.query(sql, new Object[]{studyId},
                (rs, rowNum) -> {
                    StudyInstrumentDTO dt = (StudyInstrumentDTO) applicationContext.getBean("StudyInstrumentDTO");
                    dt.setTitle(rs.getString("title"));
                    if (!onlyTitle) {
                        dt.setId(rs.getLong("id"));
                        dt.setStudyId(rs.getLong("study_id"));
                        dt.setAuthor(rs.getString("author"));
                        dt.setCitation(rs.getString("citation"));
                        dt.setSummary(rs.getString("summary"));
                        dt.setTheoHint(rs.getString("theoHint"));
                        dt.setStructure(rs.getString("structure"));
                        dt.setConstruction(rs.getString("construction"));
                        dt.setObjectivity(rs.getString("objectivity"));
                        dt.setReliability(rs.getString("reliability"));
                        dt.setValidity(rs.getString("validity"));
                        dt.setNorm(rs.getString("norm"));
                    }
                    return dt;
                });
        log.debug("Transaction for findAllByType returned [size: {}]", ret::size);
        return ret;
    }

    /**
     * Deletes instruments from a study
     *
     * @param types {@link List} of {@link StudyInstrumentDTO}
     * @return amount of changes
     */
    public int[] delete(final List<StudyInstrumentDTO> types) {
        log.trace("execute delete [size: {}]", types::size);
        int[] ret = this.jdbcTemplate.batchUpdate("DELETE FROM dw_study_instruments WHERE id = ?",
                new BatchPreparedStatementSetter() {
                    public void setValues(@Nonnull PreparedStatement ps, int i) throws SQLException {
                        StudyInstrumentDTO type = types.get(i);
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
     * Inserts instruments to a study
     *
     * @param types {@link List} of {@link StudyInstrumentDTO}
     * @return amount of changes
     */
    public int[] insert(final List<StudyInstrumentDTO> types) {
        log.trace("execute insert [size: {}]", types::size);
        int[] ret = this.jdbcTemplate.batchUpdate("Insert INTO dw_study_instruments"
                + " (study_id, title, author, citation, summary, theoHint, structure, construction, objectivity, reliability, validity, norm)"
                + " VALUES (?,?,?,?,?,?,?,?,?,?,?,?)", new BatchPreparedStatementSetter() {
            public void setValues(@Nonnull PreparedStatement ps, int i) throws SQLException {
                StudyInstrumentDTO cont = types.get(i);
                setPreparedStatement(ps, cont, false);
            }

            public int getBatchSize() {
                return types.size();
            }
        });
        log.debug("leaving insert with result: {}", () -> ret.length);
        return ret;
    }

    /**
     * Updates instruments of a study
     *
     * @param types {@link List} of {@link StudyInstrumentDTO}
     * @return amount of changes
     */
    public int[] update(final List<StudyInstrumentDTO> types) {
        log.trace("execute update [size: {}]", types::size);
        int[] ret = this.jdbcTemplate.batchUpdate(
                "UPDATE dw_study_instruments"
                        + " SET  title = ?, author = ?, citation = ?, summary = ?, theoHint = ?, structure = ?,"
                        + " construction = ?, objectivity = ?, reliability = ?, validity = ?, norm = ? WHERE id = ?",
                new BatchPreparedStatementSetter() {
                    public void setValues(@Nonnull PreparedStatement ps, int i) throws SQLException {
                        StudyInstrumentDTO cont = types.get(i);
                        setPreparedStatement(ps, cont, true);
                    }

                    public int getBatchSize() {
                        return types.size();
                    }
                });
        log.debug("leaving update with result: {}", () -> ret.length);
        return ret;
    }

    /**
     * Sets a PreparedStatement from a StudyInstrumentDTO
     *
     * @param ps         {@link PreparedStatement}
     * @param instrument {@link StudyInstrumentDTO}
     * @param update     true if update, false if insert
     * @throws SQLException DBS Exceptions
     */
    private void setPreparedStatement(final PreparedStatement ps, final StudyInstrumentDTO instrument, final boolean update)
            throws SQLException {
        int i = 1;
        if (!update)
            ps.setLong(i++, instrument.getStudyId());
        ps.setString(i++, instrument.getTitle());
        ps.setString(i++, instrument.getAuthor());
        ps.setString(i++, instrument.getCitation());
        ps.setString(i++, instrument.getSummary());
        ps.setString(i++, instrument.getTheoHint());
        ps.setString(i++, instrument.getStructure());
        ps.setString(i++, instrument.getConstruction());
        ps.setString(i++, instrument.getObjectivity());
        ps.setString(i++, instrument.getReliability());
        ps.setString(i++, instrument.getValidity());
        ps.setString(i++, instrument.getNorm());
        if (update)
            ps.setLong(i, instrument.getId());
    }

}
