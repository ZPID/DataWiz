package de.zpid.datawiz.dao;

import de.zpid.datawiz.dto.FormTypesDTO;
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
 * DAO Class for FormTypes
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
public class FormTypesDAO {

    private final ClassPathXmlApplicationContext applicationContext;
    private final JdbcTemplate jdbcTemplate;

    private static final Logger log = LogManager.getLogger(FormTypesDAO.class);

    /**
     * Instantiates a new FormTypesDAO.
     */
    @Autowired
    public FormTypesDAO(ClassPathXmlApplicationContext applicationContext, JdbcTemplate jdbcTemplate) {
        super();
        log.info("Loading FormTypesDAO as @Scope(\"singleton\") and @Repository");
        this.applicationContext = applicationContext;
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * This function returns all matching FormTypes entities from the table dmp_formtypes depending on the passed parameters.
     *
     * @param active If true, only active FormTypes will be returned
     * @param type   {@link DWFieldTypes} Type of the form type to be selected
     * @return {@link List} of {@link FormTypesDTO}, which contains the selected subset
     */
    public List<FormTypesDTO> findAllByType(final boolean active, final DWFieldTypes type) {
        log.trace("Entering getAllByType for DWFieldType [type: {}; active: {}]", () -> type, () -> active);
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM dw_formtypes WHERE");
        if (active)
            sb.append(" dw_formtypes.active = true AND");
        sb.append(" dw_formtypes.type = ? ORDER BY dw_formtypes.sort");
        final List<FormTypesDTO> ret = jdbcTemplate.query(sb.toString(), new Object[]{type.toString()}, (resultSet, rowNum) -> {
            FormTypesDTO dt = (FormTypesDTO) applicationContext.getBean("FormTypesDTO");
            dt.setId(resultSet.getInt("id"));
            dt.setNameDE(resultSet.getString("name_de"));
            dt.setNameEN(resultSet.getString("name_en"));
            dt.setActive(resultSet.getBoolean("active"));
            dt.setInvestPresent(resultSet.getBoolean("investpresent"));
            dt.setSort(resultSet.getInt("sort"));
            dt.setType(DWFieldTypes.valueOf(resultSet.getString("type")));
            return dt;
        });
        log.debug("Transaction \"getAllByType\" terminates with result: [length: {}]", ret::size);
        return ret;
    }

    /**
     * This function returns a list of the selected form type for a DMP or study. To do this, the relation tables dw_dmp_formtypes or dw_study_formtypes are
     * searched, depending on the passed parameters.
     *
     * @param identifier Study, or DMP identifier as long. Usage depends on boolean value isStudy
     * @param type       {@link DWFieldTypes} Type of the form type to be selected
     * @param isStudy    Has to be set true for finding study FormTypes, false for DMP FormTypes
     * @return {@link List} of {@link Integer}, which contains the identifiers of the selected subset
     */
    public List<Integer> findSelectedFormTypesByIdAndType(final long identifier, final DWFieldTypes type, final boolean isStudy) {
        log.trace("Entering getSelectedFormTypesByIdAndType for DWFieldType [id: {}; type: {}; isStudy: {}]", () -> identifier, type::name, () -> isStudy);
        String sql;
        if (isStudy)
            sql = "SELECT dw_study_formtypes.ftid FROM dw_study_formtypes " + "LEFT JOIN dw_formtypes ON dw_study_formtypes.ftid = dw_formtypes.id "
                    + "WHERE dw_study_formtypes.studyid = ? AND dw_formtypes.type = ? ";
        else
            sql = "SELECT dw_dmp_formtypes.ftid FROM dw_dmp_formtypes " + "LEFT JOIN dw_formtypes ON dw_dmp_formtypes.ftid = dw_formtypes.id "
                    + "WHERE dw_dmp_formtypes.dmpid = ? AND dw_formtypes.type = ? ";
        final List<Integer> ret = jdbcTemplate.query(sql, new Object[]{identifier, type.toString()}, (resultSet, rowNum) -> resultSet.getInt("ftid"));
        log.debug("Transaction \"getSelectedFormTypesByIdAndType\" terminates with result: [length: {}]", ret::size);
        return ret;
    }

    /**
     * This functions deletes all FormTypes Study/DMP entities from the relation tables dw_study_formtypes or dw_dmp_formtypes depending on the passed parameters.
     *
     * @param identifier Study, or DMP identifier as long . Usage depends on boolean value isStudy
     * @param types      {@link List} of identifiers {@link Integer} which have to be deleted
     * @param isStudy    Has to be set true for deleting study FormTypes, false for DMP FormTypes
     */
    public void deleteSelectedFormType(final long identifier, final List<Integer> types, final boolean isStudy) {
        log.trace("Entering deleteSelectedFormType [size: {}]", types::size);
        String query;
        if (isStudy)
            query = "DELETE FROM dw_study_formtypes WHERE studyid = ? AND ftid = ?";
        else
            query = "DELETE FROM dw_dmp_formtypes WHERE dmpid = ? AND ftid = ?";
        int[] ret = this.jdbcTemplate.batchUpdate(query, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(@Nonnull PreparedStatement ps, int i) throws SQLException {
                ps.setLong(1, identifier);
                ps.setLong(2, types.get(i));
            }

            public int getBatchSize() {
                return types.size();
            }
        });
        log.debug("Transaction \"deleteSelectedFormType\" terminates with result: [length: {}]", () -> ret.length);
    }

    /**
     * This functions saves a set of FormTypes Study/DMP entities into the relation tables dw_study_formtypes or dw_dmp_formtypes depending on the passed
     * parameters.
     *
     * @param dmpOrStudyID Study, or DMP identifier as long. Usage depends on boolean value isStudy
     * @param types        {@link List} of identifiers {@link Integer}  which have to be saved
     * @param isStudy      Has to be set true for saving study FormTypes, false for DMP FormTypes
     */
    public void insertSelectedFormType(final long dmpOrStudyID, final List<Integer> types, final boolean isStudy) {
        log.trace("Entering insertSelectedFormType [size: {}]", types::size);
        String query;
        if (isStudy)
            query = "INSERT INTO dw_study_formtypes (studyid, ftid) VALUES(?,?)";
        else
            query = "INSERT INTO dw_dmp_formtypes (dmpid, ftid) VALUES(?,?)";
        int[] ret = this.jdbcTemplate.batchUpdate(query, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(@Nonnull PreparedStatement ps, int i) throws SQLException {
                ps.setLong(1, dmpOrStudyID);
                ps.setLong(2, types.get(i));
            }

            public int getBatchSize() {
                return types.size();
            }
        });
        log.debug("Transaction \"insertSelectedFormType\" terminates with result: [length: {}]", () -> ret.length);
    }
}
