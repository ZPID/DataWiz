package de.zpid.datawiz.dao;

import de.zpid.datawiz.dto.RecordDTO;
import de.zpid.datawiz.spss.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.annotation.Nonnull;
import java.nio.charset.Charset;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;


/**
 * DAO Class for Record
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
public class RecordDAO {

    private final ClassPathXmlApplicationContext applicationContext;
    private final JdbcTemplate jdbcTemplate;
    private static final Logger log = LogManager.getLogger(RecordDAO.class);

    /**
     * Instantiates a new record DAO.
     */
    @Autowired
    public RecordDAO(ClassPathXmlApplicationContext applicationContext, JdbcTemplate jdbcTemplate) {
        super();
        log.info("Loading RecordDAO as @Scope(\"singleton\") and @Repository");
        this.applicationContext = applicationContext;
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * This function returns a List of RecordDTO entities from the table ddw_record, depending on the passed study identifier.
     *
     * @param studyId long Study identifier
     * @return {@link List} of {@link RecordDTO}
     */
    public List<RecordDTO> findRecordsWithStudyID(final long studyId) {
        log.trace("Entering findRecordsWithStudyID [studyId: {}]", () -> studyId);
        String sql = "SELECT * FROM dw_record WHERE dw_record.study_id  = ?";
        final List<RecordDTO> cRecords = this.jdbcTemplate.query(sql, new Object[]{studyId}, (resultSet, rowNum) -> {
            long recordId = resultSet.getLong("id");
            RecordDTO record = jdbcTemplate.query(
                    "SELECT * FROM dw_record_metadata WHERE dw_record_metadata.record_id = ? ORDER BY dw_record_metadata.version_id DESC LIMIT 1",
                    new Object[]{recordId}, (ResultSet rs2) -> {
                        if (rs2.next()) {
                            final RecordDTO rectmp = (RecordDTO) applicationContext.getBean("RecordDTO");
                            rectmp.setChanged(rs2.getTimestamp("changed").toLocalDateTime());
                            rectmp.setChangeLog(rs2.getString("changeLog"));
                            rectmp.setChangedBy(rs2.getString("changedBy"));
                            rectmp.setVersionId(rs2.getLong("version_id"));
                            return rectmp;
                        }
                        return null;
                    });
            if (record == null) {
                record = (RecordDTO) applicationContext.getBean("RecordDTO");
            }
            record.setId(recordId);
            record.setStudyId(resultSet.getLong("study_id"));
            record.setRecordName(resultSet.getString("name"));
            record.setCreated(resultSet.getTimestamp("created").toLocalDateTime());
            record.setCreatedBy(resultSet.getString("createdBy"));
            record.setDescription(resultSet.getString("description"));
            record.setFileName(resultSet.getString("filename"));
            return record;
        });
        log.debug("Transaction \"findRecordsWithStudyID\" terminates with result: [length: {}]", cRecords::size);
        return cRecords;
    }

    /**
     * This function returns a RecordDTO entity from the table dw_record, depending on the passed record and version identifiers. If the version identifier == 0,
     * the last version of the record is returned. If no record was found, null is returned.
     *
     * @param recordId  long Record identifier
     * @param versionId long Version identifier
     * @return {@link RecordDTO} on success, otherwise null
     */
    public RecordDTO findRecordWithID(final long recordId, final long versionId) {
        log.trace("Entering findRecordWithID [recordId: {}; version: {}]", () -> recordId, () -> versionId);
        final RecordDTO record = jdbcTemplate.query("SELECT * FROM dw_record WHERE dw_record.id  = ?", new Object[]{recordId},
                (ResultSet rs) -> {
                    if (rs.next()) {
                        RecordDTO recVersion = jdbcTemplate.query(
                                "SELECT * FROM dw_record_metadata WHERE dw_record_metadata.record_id = ? "
                                        + (versionId == 0 ? "ORDER BY dw_record_metadata.version_id DESC LIMIT 1" : "AND dw_record_metadata.version_id = ?"),
                                (versionId == 0 ? new Object[]{recordId} : new Object[]{recordId, versionId}), (ResultSet rs2) -> {
                                    if (rs2.next()) {
                                        final RecordDTO recTmp = (RecordDTO) applicationContext.getBean("RecordDTO");
                                        recTmp.setChanged(rs2.getTimestamp("changed").toLocalDateTime());
                                        recTmp.setChangeLog(rs2.getString("changeLog"));
                                        recTmp.setChangedBy(rs2.getString("changedBy"));
                                        recTmp.setVersionId(rs2.getLong("version_id"));
                                        recTmp.setMasterRecord(rs2.getBoolean("masterRec"));
                                        recTmp.setPassword(rs2.getString("password"));
                                        recTmp.setNumberOfVariables(rs2.getInt("numberOfVariables"));
                                        recTmp.setNumberOfFileAttributes(rs2.getInt("numberOfFileAttributes"));
                                        recTmp.setNumberOfCases(rs2.getLong("numberOfCases"));
                                        recTmp.setEstimatedNofCases(rs2.getLong("estimatedNofCases"));
                                        recTmp.setCaseSize(rs2.getLong("caseSize"));
                                        recTmp.setCaseWeightVar(rs2.getString("caseWeightVar"));
                                        recTmp.setCompression(SPSSCompression.fromInt(rs2.getInt("compression")));
                                        recTmp.setDateNumOfElements(rs2.getInt("dateNumOfElements"));
                                        recTmp.setDateInfo(rs2.getLong("dateInfo"));
                                        recTmp.setFileCodePage(rs2.getInt("fileCodePage"));
                                        recTmp.setFileEncoding(rs2.getString("fileEncoding"));
                                        recTmp.setFileIdString(rs2.getString("fileIdString"));
                                        recTmp.setInterfaceEncoding(SPSSPageEncoding.fromInt(rs2.getInt("interfaceEncoding")));
                                        recTmp.setMultRespDefsEx(rs2.getString("multRespDefsEx"));
                                        recTmp.setOriginalName(rs2.getString("originalName"));
                                        recTmp.setMinioName(rs2.getString("minioName"));
                                        return recTmp;
                                    }
                                    return null;
                                });
                        if (recVersion == null) {
                            recVersion = (RecordDTO) applicationContext.getBean("RecordDTO");
                        }
                        recVersion.setId(rs.getLong("id"));
                        recVersion.setStudyId(rs.getLong("study_id"));
                        recVersion.setRecordName(rs.getString("name"));
                        recVersion.setCreated(rs.getTimestamp("created").toLocalDateTime());
                        recVersion.setCreatedBy(rs.getString("createdBy"));
                        recVersion.setDescription(rs.getString("description"));
                        recVersion.setFileName(rs.getString("filename"));
                        return recVersion;
                    }
                    return null;
                });
        log.debug("Transaction \"findRecordWithID\" terminates with result: Record[id: {}]", () -> (record != null ? record.getId() : null));
        return record;
    }

    /**
     * This function returns all versions of a record as List, depending on the passed record identifier.
     *
     * @param recordId long Record identifier
     * @return {@link List} of {@link RecordDTO}
     */
    public List<RecordDTO> findRecordVersionList(final long recordId) {
        log.trace("Entering findRecordVersionList [recordId: {}]", () -> recordId);
        String sql = "SELECT dw_record_metadata.version_id, dw_record_metadata.record_id, dw_record_metadata.changeLog, "
                + "dw_record_metadata.changed, dw_record_metadata.changedBy, dw_user.title, dw_user.first_name, dw_user.last_name "
                + "FROM dw_record_metadata JOIn dw_user on dw_record_metadata.changedBy = dw_user.email "
                + "WHERE dw_record_metadata.record_id = ? ORDER BY dw_record_metadata.version_id DESC";
        final List<RecordDTO> record = this.jdbcTemplate.query(sql, new Object[]{recordId}, (resultSet, rowNum) -> {
            final RecordDTO rectmp = (RecordDTO) applicationContext.getBean("RecordDTO");
            rectmp.setId(recordId);
            rectmp.setChangeLog(resultSet.getString("changeLog"));
            String title = resultSet.getString("title");
            String firstname = resultSet.getString("first_name");
            String lastname = resultSet.getString("last_name");
            String changedBy = resultSet.getString("changedBy");
            String changedByLink;
            if ((title != null && !title.isEmpty()) && ((firstname != null && !firstname.isEmpty()) || (lastname != null && !lastname.isEmpty())))
                changedByLink = "<a href=\"mailto:" + changedBy + "\">" + title + " " + firstname + " " + lastname + "</a>";
            else
                changedByLink = "<a href=\"mailto:" + changedBy + "\">" + changedBy + "</a>";
            rectmp.setChangedBy(changedByLink);
            rectmp.setChanged((resultSet.getTimestamp("changed").toLocalDateTime()));
            rectmp.setVersionId(resultSet.getLong("version_id"));
            rectmp.setId(resultSet.getLong("record_id"));
            return rectmp;
        });
        log.debug("Transaction \"findRecordVersionList\" terminates with result: [lenght: {}]", record::size);
        return record;
    }

    /**
     * This function returns all variable entities, of a specific record version, as a List from the table dw_record_variables, depending on the passed version
     * identifier.
     *
     * @param versionId long Version identifier
     * @return {@link List} of all {@link SPSSVarDTO} that belong to a record version
     */
    public List<SPSSVarDTO> findVariablesByVersionID(final long versionId) {
        log.trace("Entering findVariablesByVersionID [versionId: {}]", () -> versionId);
        String sql = "SELECT * FROM dw_record_version_variables JOIN dw_record_variables " + "ON dw_record_version_variables.var_id = dw_record_variables.id "
                + "WHERE dw_record_version_variables.version_id = ? ORDER BY dw_record_version_variables.position ASC";
        final List<SPSSVarDTO> cVars = this.jdbcTemplate.query(sql, new Object[]{versionId}, (resultSet, rowNum) -> {
            SPSSVarDTO var = new SPSSVarDTO();
            var.setId(resultSet.getLong("id"));
            var.setName(resultSet.getString("name"));
            var.setType(SPSSVarTypes.fromInt(resultSet.getInt("type")));
            var.setVarType(resultSet.getInt("varType"));
            var.setDecimals(resultSet.getInt("decimals"));
            var.setWidth(resultSet.getInt("width"));
            var.setLabel(resultSet.getString("label") == null ? "" : resultSet.getString("label"));
            var.setMissingFormat(SPSSMissing.fromInt(resultSet.getInt("missingFormat")));
            var.setMissingVal1(resultSet.getString("missingVal1"));
            var.setMissingVal2(resultSet.getString("missingVal2"));
            var.setMissingVal3(resultSet.getString("missingVal3"));
            var.setColumns(resultSet.getInt("columns"));
            var.setAligment(SPSSAligment.fromInt(resultSet.getInt("aligment")));
            var.setMeasureLevel(SPSSMeasLevel.fromInt(resultSet.getInt("measureLevel")));
            var.setRole(SPSSRoleCodes.fromInt(resultSet.getInt("role")));
            var.setNumOfAttributes(resultSet.getInt("numOfAttributes"));
            var.setPosition(resultSet.getInt("position"));
            return var;
        });
        log.debug("Transaction \"findVariablesByVersionID\" terminates with result: [lenght: {}]", cVars::size);
        return cVars;
    }

    /**
     * This function returns all Value-Label entities from the table dw_record_var_vallabel that belongs to a specific variable, depending on the passed variable
     * identifier.
     *
     * @param varId  long Variable identifier
     * @param withId true if the Value-Label identifier is required, otherwise false
     * @return {@link List} of all Value-Label as {@link SPSSValueLabelDTO}, that belong to a variable
     */
    public List<SPSSValueLabelDTO> findVariableValues(final long varId, final boolean withId) {
        log.trace("Entering findVariablesValues [varId: {}]", () -> varId);
        String sql = "SELECT * FROM dw_record_var_vallabel WHERE record_var_id = ?";
        final List<SPSSValueLabelDTO> cVars = this.jdbcTemplate.query(sql, new Object[]{varId}, (resultSet, rowNum) -> {
            SPSSValueLabelDTO var = new SPSSValueLabelDTO();
            if (withId)
                var.setId(resultSet.getLong("id"));
            var.setLabel(resultSet.getString("label"));
            var.setValue(resultSet.getString("value"));
            return var;
        });
        log.debug("Transaction \"findVariableValues\" terminates with result: [lenght: {}]", cVars::size);
        return cVars;
    }

    /**
     * This function returns all Attributes entities from the table dw_record_attributes that belongs to a specific variable, depending on the passed variable
     * identifier.
     *
     * @param varId  long Variable identifier
     * @param withId true if the Attributes identifier is required, otherwise false
     * @return {@link List} of all Attributes as {@link SPSSValueLabelDTO}, that belong to a variable
     */
    public List<SPSSValueLabelDTO> findVariableAttributes(final long varId, final boolean withId) {
        log.trace("Entering findVariablesAttributes [varId: {}]", () -> varId);
        String sql = "SELECT * FROM dw_record_attributes WHERE var_id = ?";
        final List<SPSSValueLabelDTO> cVars = this.jdbcTemplate.query(sql, new Object[]{varId}, (resultSet, rowNum) -> {
            SPSSValueLabelDTO var = new SPSSValueLabelDTO();
            if (withId)
                var.setId(resultSet.getLong("id"));
            var.setLabel(resultSet.getString("label"));
            var.setValue(resultSet.getString("text"));
            return var;
        });
        log.debug("Transaction \"findVariableAttributes\" terminates with result: [lenght: {}]", cVars::size);
        return cVars;
    }

    /**
     * This function returns all Attributes entities from the table dw_record_attributes that belongs to a specific record, depending on the passed record
     * identifier. User attributes are stored in SPSS with an @ character prefix. Other attributes are DataWiz-specific attributes that have a dw_ prefix.
     *
     * @param versionId          long  Variable identifier
     * @param onlyUserAttributes true, if only SPSS user attributes are required, otherwise false
     * @return {@link List} of all Attributes as {@link SPSSValueLabelDTO},that belong to a record
     */
    public List<SPSSValueLabelDTO> findRecordAttributes(final long versionId, boolean onlyUserAttributes) {
        log.trace("Entering findRecordAttributes [version: {}]", () -> versionId);
        String sql = "SELECT * FROM dw_record_attributes WHERE version_id = ? AND var_id IS NULL " + (onlyUserAttributes ? "AND text LIKE '@%'" : "");
        final List<SPSSValueLabelDTO> cVars = this.jdbcTemplate.query(sql, new Object[]{versionId}, (rs, rowNum) -> {
            SPSSValueLabelDTO var = new SPSSValueLabelDTO();
            var.setId(rs.getLong("id"));
            var.setLabel(rs.getString("label"));
            var.setValue(rs.getString("text"));
            return var;
        });
        log.debug("Transaction \"findRecordAttributes\" terminates with result: [lenght: {}]", cVars::size);
        return cVars;
    }

    /**
     * Deletes a variable
     *
     * @param id long Variable identifier
     */
    public void deleteVariable(final long id) {
        log.trace("Entering deleteVariable for Variable [id: {}]: ", () -> id);
        int chk = this.jdbcTemplate.update("DELETE FROM dw_record_variables WHERE id = ? ", id);
        log.debug("Transaction \"deleteVariable\" terminates with result:  [result: {}]", () -> chk);
    }

    /**
     * Deletes a record
     *
     * @param id long Record identifier
     */
    public void deleteRecord(final long id) {
        log.trace("Entering deleteRecord for Record [id: {}]: ", () -> id);
        int chk = this.jdbcTemplate.update("DELETE FROM dw_record WHERE id = ? ", id);
        log.debug("Transaction \"deleteVariable\" terminates with result:  [result: {}]", () -> chk);
    }


    /**
     * Inserts codebook meta data
     *
     * @param record {@link RecordDTO} contains the codebook data
     */
    public void insertCodeBookMetaData(final RecordDTO record) {
        log.trace("Entering insertCodeBookMetaData for Record[id: {}]", record::getId);
        KeyHolder holder = new GeneratedKeyHolder();
        final String stmt = "INSERT INTO dw_record_metadata (record_id, changeLog, changed, changedBy, masterRec, password, "
                + "numberOfVariables, numberOfFileAttributes, numberOfCases, estimatedNofCases, caseSize, caseWeightVar,  compression, "
                + "dateNumOfElements, dateInfo, fileCodePage, fileEncoding, fileIdString, interfaceEncoding, multRespDefsEx, originalName, minioName) "
                + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        int res = this.jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(stmt, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, record.getId());
            ps.setString(2, record.getChangeLog());
            ps.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            ps.setString(4, record.getChangedBy());
            ps.setBoolean(5, record.isMasterRecord());
            ps.setString(6, record.getPassword());
            ps.setInt(7, record.getNumberOfVariables());
            ps.setInt(8, record.getNumberOfFileAttributes());
            ps.setLong(9, record.getNumberOfCases());
            ps.setLong(10, record.getEstimatedNofCases());
            ps.setLong(11, record.getCaseSize());
            ps.setString(12, record.getCaseWeightVar());
            ps.setInt(13, (record.getCompression() == null ? 0 : record.getCompression().getNumber()));
            ps.setInt(14, record.getDateNumOfElements());
            ps.setLong(15, record.getDateInfo());
            ps.setInt(16, record.getFileCodePage());
            ps.setString(17, record.getFileEncoding());
            ps.setString(18, record.getFileIdString());
            ps.setInt(19, (record.getInterfaceEncoding() == null ? 0 : record.getInterfaceEncoding().getNumber()));
            ps.setString(20, record.getMultRespDefsEx());
            ps.setString(21, record.getOriginalName());
            ps.setString(22, record.getMinioName());
            return ps;
        }, holder);
        final long key = (holder.getKey() != null && holder.getKey().intValue() > 0) ? holder.getKey().longValue() : -1;
        record.setVersionId(key);
        log.debug("Transaction \"insertCodeBookMetaData\" terminates with result:   [res: {}, key: {}]", () -> res, () -> key);
    }

    /**
     * Updates record meta data
     *
     * @param record {@link RecordDTO} contains the meta data
     */
    public void updateRecordMetaData(final RecordDTO record) {
        log.trace("Entering updateRecordMetaData [recordId: {}]", record::getId);
        System.err.println(record.getDescription());
        final int ret = this.jdbcTemplate.update("UPDATE dw_record SET name= ?, description = ? WHERE id = ?", record.getRecordName(), record.getDescription(),
                record.getId());
        log.debug("Transaction for updateRecordMetaData returned [{}]", () -> ret);
    }

    /**
     * Inserts record meta data
     *
     * @param record {@link RecordDTO} contains the meta data
     */
    public void insertRecordMetaData(final RecordDTO record) {
        log.trace("Entering insertRecordMetaData [studyid: {}]", record::getStudyId);
        KeyHolder holder = new GeneratedKeyHolder();
        final String stmt = "INSERT INTO dw_record (study_id, name, created, createdBy, description, filename) VALUES (?,?,?,?,?,?)";
        int res = this.jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(stmt, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, record.getStudyId());
            ps.setString(2, record.getRecordName());
            ps.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            ps.setString(4, record.getCreatedBy());
            ps.setString(5, record.getDescription());
            ps.setString(6, record.getFileName());
            return ps;
        }, holder);
        final long key = (holder.getKey() != null && holder.getKey().intValue() > 0) ? holder.getKey().longValue() : -1;
        record.setId(key);
        log.debug("Transaction for insertRecordMetaData returned [res: {}, key: {}]", () -> res, () -> key);
    }

    /**
     * Inserts Codebook attributes
     *
     * @param attr      {@link List} of {@link SPSSValueLabelDTO}
     * @param versionId long version identifier
     * @param varId     long variable identifier
     */
    public void insertAttributes(final List<SPSSValueLabelDTO> attr, final long versionId, final long varId) {
        log.trace("execute insertAttributes [size: {}]", attr::size);
        int[] ret = this.jdbcTemplate.batchUpdate("INSERT INTO dw_record_attributes (version_id, var_id, label, text) VALUES (?,?,?,?)",
                new BatchPreparedStatementSetter() {
                    public void setValues(@Nonnull PreparedStatement ps, int i) throws SQLException {
                        SPSSValueLabelDTO cont = attr.get(i);
                        ps.setLong(1, versionId);
                        if (varId > 0)
                            ps.setLong(2, varId);
                        else
                            ps.setNull(2, Types.BIGINT);
                        ps.setString(3, cont.getLabel());
                        ps.setString(4, cont.getValue());
                    }

                    public int getBatchSize() {
                        return attr.size();
                    }
                });
        int sum = Arrays.stream(ret).sum();
        log.debug("leaving insertAttributes with result: {}", () -> (sum == attr.size() ? 1 : 0));
        attr.size();
    }

    /**
     * Inserts a variable
     *
     * @param var {@link SPSSVarDTO} with the variable data
     * @return the new variable identifier as long on success, otherwise -1
     */
    public long insertVariable(final SPSSVarDTO var) {
        log.trace("Entering insertVariable [varName: {}, versionId: {}]", var::getName);
        KeyHolder holder = new GeneratedKeyHolder();
        final String stmt = "INSERT INTO dw_record_variables (name, type, varType, decimals, width, label, missingFormat, "
                + "missingVal1, missingVal2, missingVal3, columns, aligment, measureLevel, role, numOfAttributes) " + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        int res = this.jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(stmt, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, var.getName());
            ps.setInt(2, var.getType().getNumber());
            ps.setInt(3, var.getVarType());
            ps.setInt(4, var.getDecimals());
            ps.setInt(5, var.getWidth());
            ps.setString(6, var.getLabel());
            ps.setInt(7, var.getMissingFormat() == null ? 0 : var.getMissingFormat().getNumber());
            ps.setString(8, var.getMissingVal1());
            ps.setString(9, var.getMissingVal2());
            ps.setString(10, var.getMissingVal3());
            ps.setInt(11, var.getColumns());
            ps.setInt(12, var.getAligment() == null ? 0 : var.getAligment().getNumber());
            ps.setInt(13, var.getMeasureLevel() == null ? 0 : var.getMeasureLevel().getNumber());
            ps.setInt(14, var.getRole() == null ? 0 : var.getRole().getNumber());
            ps.setInt(15, var.getNumOfAttributes());
            return ps;
        }, holder);
        final long key = (holder.getKey() != null && holder.getKey().intValue() > 0) ? holder.getKey().longValue() : -1;
        log.debug("Transaction for insertVariable returned [res: {}, key: {}]", () -> res, () -> key);
        return key;
    }

    /**
     * Inserts a relation between a variable and record version.
     *
     * @param varId     long variable identifier
     * @param versionId long version identifier
     * @param position  int codebook position of the variable
     * @param changelog {@link String} auto-generated Changelog
     */
    public void insertVariableVersionRelation(final long varId, final long versionId, final int position, final String changelog) {
        log.trace("Entering insertVariableVersionRelation[versionId: {}, varid: {}, position: {}, changelog: {}]", () -> versionId, () -> varId, () -> position,
                () -> changelog);
        final int ret = this.jdbcTemplate.update("INSERT INTO dw_record_version_variables (version_id, var_id, position, changelog) VALUES (?,?,?,?)", versionId,
                varId, position, changelog);
        log.debug("Transaction for insertVariableVersionRelation returned [{}]", () -> ret);
    }

    /**
     * Inserts variable's value label attributes
     *
     * @param valLabel {@link SPSSValueLabelDTO} with value-label content
     * @param varId    long variable identifier
     */
    public void insertVarLabels(final List<SPSSValueLabelDTO> valLabel, final long varId) {
        log.trace("execute insertVarLabels [size: {}]", valLabel::size);
        int[] ret = this.jdbcTemplate.batchUpdate("INSERT INTO dw_record_var_vallabel (record_var_id, label, value) VALUES (?,?,?)",
                new BatchPreparedStatementSetter() {
                    public void setValues(@Nonnull PreparedStatement ps, int i) throws SQLException {
                        SPSSValueLabelDTO cont = valLabel.get(i);
                        ps.setLong(1, varId);
                        ps.setString(2, cont.getLabel());
                        ps.setString(3, cont.getValue());
                    }

                    public int getBatchSize() {
                        return valLabel.size();
                    }
                });
        int sum = Arrays.stream(ret).sum();
        log.debug("leaving insertVarLabels with result: {}", () -> (sum == valLabel.size() ? 1 : 0));
        valLabel.size();
    }


    @Deprecated
    public String findMatrixByVersionId(final long versionId) {
        log.trace("Entering findMatrixByVersionId [versionId: {}]", () -> versionId);
        String sql = "SELECT datamatrix FROM dw_record_matrix WHERE version_id = ?";
        String matrixJSON = jdbcTemplate.query(sql, new Object[]{versionId}, rs -> {
            if (rs.next()) {
                return new String(rs.getBytes("datamatrix"), Charset.forName("UTF-8"));
            }
            return null;
        });
        log.debug("leaving findMatrixByVersionId with size: {}", () -> (matrixJSON != null ? matrixJSON.length() : "null"));
        return matrixJSON;
    }


    @Deprecated
    public int insertMatrix(final RecordDTO record) {
        log.trace("Entering insertRecordMatrix [recordId: {}, versionId: {}]", record::getId, record::getVersionId);
        final int ret = this.jdbcTemplate.update("INSERT INTO dw_record_matrix (version_id, datamatrix) VALUES (?,?)", record.getVersionId(),
                record.getDataMatrixJson());
        log.debug("Transaction for insertMatrix returned [{}]", () -> ret);
        return ret;
    }

}
