package de.zpid.datawiz.dao;

import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import de.zpid.datawiz.dto.RecordDTO;
import de.zpid.spss.dto.SPSSValueLabelDTO;
import de.zpid.spss.dto.SPSSVarDTO;
import de.zpid.spss.util.SPSSAligment;
import de.zpid.spss.util.SPSSCompression;
import de.zpid.spss.util.SPSSMeasLevel;
import de.zpid.spss.util.SPSSMissing;
import de.zpid.spss.util.SPSSPageEncoding;
import de.zpid.spss.util.SPSSRoleCodes;
import de.zpid.spss.util.SPSSVarTypes;

@Repository
@Scope("singleton")
public class RecordDAO extends SuperDAO {

  private static Logger log = LogManager.getLogger(RecordDAO.class);

  public RecordDAO() {
    super();
    if (log.isInfoEnabled())
      log.info("Loading RecordDAO as Singleton and Service");
  }

  public List<RecordDTO> findRecordsWithStudyID(final long studyId) throws Exception {
    log.trace("Entering findRecordsWithStudyID [studyId: {}]", () -> studyId);
    String sql = "SELECT * FROM dw_record WHERE dw_record.study_id  = ?";
    final List<RecordDTO> cRecords = this.jdbcTemplate.query(sql, new Object[] { studyId }, new RowMapper<RecordDTO>() {
      public RecordDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        long recordId = rs.getLong("id");
        RecordDTO record = jdbcTemplate.query(
            "SELECT * FROM dw_record_metadata WHERE dw_record_metadata.record_id = ? ORDER BY dw_record_metadata.version_id DESC LIMIT 1",
            new Object[] { recordId }, new ResultSetExtractor<RecordDTO>() {
              @Override
              public RecordDTO extractData(ResultSet rs2) throws SQLException, DataAccessException {
                if (rs2.next()) {
                  final RecordDTO rectmp = (RecordDTO) applicationContext.getBean("RecordDTO");
                  rectmp.setChanged(rs2.getTimestamp("changed").toLocalDateTime());
                  rectmp.setChangeLog(rs2.getString("changeLog"));
                  rectmp.setChangedBy(rs2.getString("changedBy"));
                  rectmp.setVersionId(rs2.getLong("version_id"));
                  return rectmp;
                }
                return null;
              }
            });
        if (record == null) {
          record = (RecordDTO) applicationContext.getBean("RecordDTO");
        }
        record.setId(recordId);
        record.setStudyId(rs.getLong("study_id"));
        record.setRecordName(rs.getString("name"));
        record.setCreated(rs.getTimestamp("created").toLocalDateTime());
        record.setCreatedBy(rs.getString("createdBy"));
        record.setDescription(rs.getString("description"));
        record.setFileName(rs.getString("filename"));
        return record;
      }
    });
    log.debug("leaving findRecordsWithStudyID with size: {}", () -> cRecords.size());
    return cRecords;
  }

  public RecordDTO findRecordWithID(final long recordId, final long versionId) throws Exception {
    log.trace("Entering findRecordWithID [recordId: {}; version: {}]", () -> recordId, () -> versionId);
    final RecordDTO record = jdbcTemplate.query("SELECT * FROM dw_record WHERE dw_record.id  = ?",
        new Object[] { recordId }, new ResultSetExtractor<RecordDTO>() {
          @Override
          public RecordDTO extractData(ResultSet rs) throws SQLException, DataAccessException {
            if (rs != null && rs.next()) {
              long recordId = rs.getLong("id");
              RecordDTO recVersion = jdbcTemplate.query(
                  "SELECT * FROM dw_record_metadata WHERE dw_record_metadata.record_id = ? "
                      + (versionId == 0 ? "ORDER BY dw_record_metadata.version_id DESC LIMIT 1"
                          : "AND dw_record_metadata.version_id = ?"),
                  (versionId == 0 ? new Object[] { recordId } : new Object[] { recordId, versionId }),
                  new ResultSetExtractor<RecordDTO>() {
                    @Override
                    public RecordDTO extractData(ResultSet rs2) throws SQLException, DataAccessException {
                      if (rs2 != null && rs2.next()) {
                        final RecordDTO rectmp = (RecordDTO) applicationContext.getBean("RecordDTO");
                        rectmp.setChanged(rs2.getTimestamp("changed").toLocalDateTime());
                        rectmp.setChangeLog(rs2.getString("changeLog"));
                        rectmp.setChangedBy(rs2.getString("changedBy"));
                        rectmp.setVersionId(rs2.getLong("version_id"));
                        rectmp.setMasterRecord(rs2.getBoolean("masterRec"));
                        rectmp.setPassword(rs2.getString("password"));
                        rectmp.setNumberOfVariables(rs2.getInt("numberOfVariables"));
                        rectmp.setNumberOfFileAttributes(rs2.getInt("numberOfFileAttributes"));
                        rectmp.setNumberOfCases(rs2.getLong("numberOfCases"));
                        rectmp.setEstimatedNofCases(rs2.getLong("estimatedNofCases"));
                        rectmp.setCaseSize(rs2.getLong("caseSize"));
                        rectmp.setCaseWeightVar(rs2.getString("caseWeightVar"));
                        rectmp.setCompression(SPSSCompression.fromInt(rs2.getInt("compression")));
                        rectmp.setDateNumOfElements(rs2.getInt("dateNumOfElements"));
                        rectmp.setDateInfo(rs2.getLong("dateInfo"));
                        rectmp.setFileCodePage(rs2.getInt("fileCodePage"));
                        rectmp.setFileEncoding(rs2.getString("fileEncoding"));
                        rectmp.setFileIdString(rs2.getString("fileIdString"));
                        rectmp.setInterfaceEncoding(SPSSPageEncoding.fromInt(rs2.getInt("interfaceEncoding")));
                        rectmp.setMultRespDefsEx(rs2.getString("multRespDefsEx"));
                        return rectmp;
                      }
                      return null;
                    }
                  });
              if (recVersion == null) {
                recVersion = (RecordDTO) applicationContext.getBean("RecordDTO");
              }
              recVersion.setId(recordId);
              recVersion.setStudyId(rs.getLong("study_id"));
              recVersion.setRecordName(rs.getString("name"));
              recVersion.setCreated(rs.getTimestamp("created").toLocalDateTime());
              recVersion.setCreatedBy(rs.getString("createdBy"));
              recVersion.setDescription(rs.getString("description"));
              recVersion.setFileName(rs.getString("filename"));
              return recVersion;
            }
            return null;
          }
        });
    log.debug("leaving findRecordWithID with id: {}", () -> (record != null ? record.getId() : null));
    return record;
  }

  public List<RecordDTO> findRecordVersionList(final long recordId) throws Exception {
    log.trace("Entering findRecordVersionList [recordId: {}]", () -> recordId);
    String sql = "SELECT dw_record_metadata.version_id, dw_record_metadata.record_id, dw_record_metadata.changeLog, "
        + "dw_record_metadata.changed, dw_record_metadata.changedBy, dw_user.title, dw_user.first_name, dw_user.last_name "
        + "FROM dw_record_metadata JOIn dw_user on dw_record_metadata.changedBy = dw_user.email "
        + "WHERE dw_record_metadata.record_id = ? ORDER BY dw_record_metadata.version_id DESC";
    final List<RecordDTO> record = this.jdbcTemplate.query(sql, new Object[] { recordId }, new RowMapper<RecordDTO>() {
      public RecordDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        final RecordDTO rectmp = (RecordDTO) applicationContext.getBean("RecordDTO");
        rectmp.setId(recordId);
        rectmp.setChangeLog(rs.getString("changeLog"));
        String title = rs.getString("title");
        String firstname = rs.getString("first_name");
        String lastname = rs.getString("last_name");
        String changedBy = rs.getString("changedBy");
        String changedByLink;
        if ((title != null && !title.isEmpty())
            && ((firstname != null && !firstname.isEmpty()) || (lastname != null && !lastname.isEmpty())))
          changedByLink = "<a href=\"mailto:" + changedBy + "\">" + title + " " + firstname + " " + lastname + "</a>";
        else
          changedByLink = "<a href=\"mailto:" + changedBy + "\">" + changedBy + "</a>";
        rectmp.setChangedBy(changedByLink);
        rectmp.setChanged((rs.getTimestamp("changed").toLocalDateTime()));
        rectmp.setVersionId(rs.getLong("version_id"));
        rectmp.setId(rs.getLong("record_id"));
        return rectmp;
      }
    });
    log.debug("leaving findRecordVersionList with size: {}", record != null ? record.size() : null);
    return record;
  }

  public List<SPSSVarDTO> findVariablesByVersionID(final long versionId) throws Exception {
    log.trace("Entering findVariablesByVersionID [versionId: {}]", () -> versionId);
    String sql = "SELECT * FROM dw_record_version_variables JOIN dw_record_variables "
        + "ON dw_record_version_variables.var_id = dw_record_variables.id "
        + "WHERE dw_record_version_variables.version_id = ? ORDER BY dw_record_version_variables.position ASC";
    final List<SPSSVarDTO> cVars = this.jdbcTemplate.query(sql, new Object[] { versionId },
        new RowMapper<SPSSVarDTO>() {
          public SPSSVarDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
            SPSSVarDTO var = new SPSSVarDTO();
            var.setId(rs.getLong("id"));
            var.setName(rs.getString("name"));
            var.setType(SPSSVarTypes.fromInt(rs.getInt("type")));
            var.setVarType(rs.getInt("varType"));
            var.setDecimals(rs.getInt("decimals"));
            var.setWidth(rs.getInt("width"));
            var.setLabel(rs.getString("label") == null ? "" : rs.getString("label"));
            var.setMissingFormat(SPSSMissing.fromInt(rs.getInt("missingFormat")));
            var.setMissingVal1(rs.getString("missingVal1"));
            var.setMissingVal2(rs.getString("missingVal2"));
            var.setMissingVal3(rs.getString("missingVal3"));
            var.setColumns(rs.getInt("columns"));
            var.setAligment(SPSSAligment.fromInt(rs.getInt("aligment")));
            var.setMeasureLevel(SPSSMeasLevel.fromInt(rs.getInt("measureLevel")));
            var.setRole(SPSSRoleCodes.fromInt(rs.getInt("role")));
            var.setNumOfAttributes(rs.getInt("numOfAttributes"));
            var.setPosition(rs.getInt("position"));
            return var;
          }
        });
    log.debug("leaving findVariablesByVersionID with size: {}", () -> cVars.size());
    return cVars;
  }

  public List<SPSSValueLabelDTO> findVariableValues(final long varId, final boolean withId) throws Exception {
    log.trace("Entering findVariablesValues [varId: {}]", () -> varId);
    String sql = "SELECT * FROM dw_record_var_vallabel WHERE record_var_id = ?";
    final List<SPSSValueLabelDTO> cVars = this.jdbcTemplate.query(sql, new Object[] { varId },
        new RowMapper<SPSSValueLabelDTO>() {
          public SPSSValueLabelDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
            SPSSValueLabelDTO var = new SPSSValueLabelDTO();
            if (withId)
              var.setId(rs.getLong("id"));
            var.setLabel(rs.getString("label"));
            var.setValue(rs.getString("value"));
            return var;
          }
        });
    log.debug("leaving findVariablesValues with size: {}", () -> cVars.size());
    return cVars;
  }

  public List<SPSSValueLabelDTO> findVariableAttributes(final long varId, final boolean withId) throws Exception {
    log.trace("Entering findVariablesAttributes [varId: {}]", () -> varId);
    String sql = "SELECT * FROM dw_record_attributes WHERE var_id = ?";
    final List<SPSSValueLabelDTO> cVars = this.jdbcTemplate.query(sql, new Object[] { varId },
        new RowMapper<SPSSValueLabelDTO>() {
          public SPSSValueLabelDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
            SPSSValueLabelDTO var = new SPSSValueLabelDTO();
            if (withId)
              var.setId(rs.getLong("id"));
            var.setLabel(rs.getString("label"));
            var.setValue(rs.getString("text"));
            return var;
          }
        });
    log.debug("leaving findVariablesAttributes with size: {}", () -> cVars.size());
    return cVars;
  }

  public List<SPSSValueLabelDTO> findRecordAttributes(final long versionId, boolean onlyUserAttributes)
      throws Exception {
    log.trace("Entering findRecordAttributes [version: {}]", () -> versionId);
    String sql = "SELECT * FROM dw_record_attributes WHERE version_id = ? AND var_id IS NULL "
        + (onlyUserAttributes ? "AND text LIKE '@%'" : "");
    final List<SPSSValueLabelDTO> cVars = this.jdbcTemplate.query(sql, new Object[] { versionId },
        new RowMapper<SPSSValueLabelDTO>() {
          public SPSSValueLabelDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
            SPSSValueLabelDTO var = new SPSSValueLabelDTO();
            var.setId(rs.getLong("id"));
            var.setLabel(rs.getString("label"));
            var.setValue(rs.getString("text"));
            return var;
          }
        });
    log.debug("leaving findRecordAttributes with size: {}", () -> cVars.size());
    return cVars;
  }

  /**
   * Insert the important SPSS MetaData to dw_record_metadata. File Metadata such as fileSize is saved in dw_files
   * 
   * @param record
   * @return
   * @throws Exception
   */
  public int insertCodeBookMetaData(final RecordDTO record) throws Exception {
    log.trace("Entering insertCodeBookMetaData [recordId: {}]", () -> record.getId());
    KeyHolder holder = new GeneratedKeyHolder();
    final String stmt = "INSERT INTO dw_record_metadata (record_id, changeLog, changed, changedBy, masterRec, password, "
        + "numberOfVariables, numberOfFileAttributes, numberOfCases, estimatedNofCases, caseSize, caseWeightVar,  compression, "
        + "dateNumOfElements, dateInfo, fileCodePage, fileEncoding, fileIdString, interfaceEncoding, multRespDefsEx) "
        + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    int res = this.jdbcTemplate.update(new PreparedStatementCreator() {
      @Override
      public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
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
        return ps;
      }
    }, holder);
    final long key = (holder.getKey().intValue() > 0) ? holder.getKey().longValue() : -1;
    record.setVersionId(key);
    log.debug("Transaction for insertMetaData returned [res: {}, key: {}]", () -> res, () -> key);
    return res;
  }

  /**
   * 
   * @param record
   * @return
   * @throws Exception
   */
  public int insertMatrix(final RecordDTO record) throws Exception {
    log.trace("Entering insertRecordMatrix [recordId: {}, versionId: {}]", () -> record.getId(),
        () -> record.getVersionId());
    final int ret = this.jdbcTemplate.update("INSERT INTO dw_record_matrix (version_id, datamatrix) VALUES (?,?)",
        record.getVersionId(), record.getDataMatrixJson());
    log.debug("Transaction for insertMatrix returned [{}]", () -> ret);
    return ret;
  }

  public int updateRecordMetaData(final RecordDTO record) throws Exception {
    log.trace("Entering updateRecordMetaData [recordId: {}]", () -> record.getId());
    final int ret = this.jdbcTemplate.update("UPDATE dw_record SET name= ?, description = ? WHERE id = ?",
        record.getRecordName(), record.getDescription(), record.getId());
    log.debug("Transaction for updateRecordMetaData returned [{}]", () -> ret);
    return ret;
  }

  public int insertRecordMetaData(final RecordDTO record) throws Exception {
    log.trace("Entering insertRecordMetaData [studyid: {}]", () -> record.getStudyId());
    KeyHolder holder = new GeneratedKeyHolder();
    final String stmt = "INSERT INTO dw_record (study_id, name, created, createdBy, description, filename) VALUES (?,?,?,?,?,?)";
    int res = this.jdbcTemplate.update(new PreparedStatementCreator() {
      @Override
      public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(stmt, Statement.RETURN_GENERATED_KEYS);
        ps.setLong(1, record.getStudyId());
        ps.setString(2, record.getRecordName());
        ps.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
        ps.setString(4, record.getCreatedBy());
        ps.setString(5, record.getDescription());
        ps.setString(6, record.getFileName());
        System.err.println(record.getDescription());
        return ps;
      }
    }, holder);
    final long key = (holder.getKey().intValue() > 0) ? holder.getKey().longValue() : -1;
    record.setId(key);
    log.debug("Transaction for insertRecordMetaData returned [res: {}, key: {}]", () -> res, () -> key);
    return res;
  }

  public String findMatrixByVersionId(final long versionId) throws Exception {
    log.trace("Entering findMatrixByVersionId [versionId: {}]", () -> versionId);
    String sql = "SELECT datamatrix FROM dw_record_matrix WHERE version_id = ?";
    String matrixJSON = jdbcTemplate.query(sql, new Object[] { versionId }, new ResultSetExtractor<String>() {
      @Override
      public String extractData(ResultSet rs) throws SQLException, DataAccessException {
        if (rs.next()) {
          return new String(rs.getBytes("datamatrix"), Charset.forName("UTF-8"));
        }
        return null;
      }
    });
    log.debug("leaving findMatrixByVersionId with size: {}", () -> (matrixJSON != null ? matrixJSON.length() : "null"));
    return matrixJSON;
  }

  /**
   * 
   * @param attr
   * @param versionId
   * @param varId
   * @return
   * @throws Exception
   */
  public int insertAttributes(final List<SPSSValueLabelDTO> attr, final long versionId, final long varId)
      throws Exception {
    log.trace("execute insertAttributes [size: {}]", () -> attr.size());
    int[] ret = this.jdbcTemplate.batchUpdate(
        "INSERT INTO dw_record_attributes (version_id, var_id, label, text) VALUES (?,?,?,?)",
        new BatchPreparedStatementSetter() {
          public void setValues(PreparedStatement ps, int i) throws SQLException {
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
    return (sum == attr.size() ? 1 : 0);
  }

  /**
   * 
   * @param var
   * @param versionId
   * @return
   * @throws Exception
   */
  public long insertVariable(final SPSSVarDTO var) throws Exception {
    log.trace("Entering insertVariable [varName: {}, versionId: {}]", () -> var.getName());
    KeyHolder holder = new GeneratedKeyHolder();
    final String stmt = "INSERT INTO dw_record_variables (name, type, varType, decimals, width, label, missingFormat, "
        + "missingVal1, missingVal2, missingVal3, columns, aligment, measureLevel, role, numOfAttributes) "
        + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    int res = this.jdbcTemplate.update(new PreparedStatementCreator() {
      @Override
      public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
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
      }
    }, holder);
    final long key = (holder.getKey().intValue() > 0) ? holder.getKey().longValue() : -1;
    log.debug("Transaction for insertVariable returned [res: {}, key: {}]", () -> res, () -> key);
    return key;
  }

  public long insertVariableVersionRelation(final long varId, final long versionId, final int position,
      final String changelog) throws Exception {
    log.trace("Entering insertVariableVersionRelation[versionId: {}, varid: {}, position: {}, changelog: {}]",
        () -> versionId, () -> varId, () -> position, () -> changelog);
    final int ret = this.jdbcTemplate.update(
        "INSERT INTO dw_record_version_variables (version_id, var_id, position, changelog) VALUES (?,?,?,?)", versionId,
        varId, position, changelog);
    log.debug("Transaction for insertVariableVersionRelation returned [{}]", () -> ret);
    return ret;
  }

  /**
   * 
   * @param valLabel
   * @param varId
   * @return
   * @throws Exception
   */
  public int insertVarLabels(final List<SPSSValueLabelDTO> valLabel, final long varId) throws Exception {
    log.trace("execute insertVarLabels [size: {}]", () -> valLabel.size());
    int[] ret = this.jdbcTemplate.batchUpdate(
        "INSERT INTO dw_record_var_vallabel (record_var_id, label, value) VALUES (?,?,?)",
        new BatchPreparedStatementSetter() {
          public void setValues(PreparedStatement ps, int i) throws SQLException {
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
    return (sum == valLabel.size() ? 1 : 0);
  }

}
