package de.zpid.datawiz.dao;

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
import de.zpid.spss.dto.SPSSVarTDO;

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

  public RecordDTO findRecordWithID(final long recordId) throws Exception {
    log.trace("Entering findRecordWithID [recordId: {}]", () -> recordId);
    final RecordDTO cRecords = jdbcTemplate.query("SELECT * FROM dw_record WHERE dw_record.id  = ?",
        new Object[] { recordId }, new ResultSetExtractor<RecordDTO>() {
          @Override
          public RecordDTO extractData(ResultSet rs) throws SQLException, DataAccessException {
            if (rs.next()) {
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
            return null;
          }
        });
    log.debug("leaving findRecordWithID with id: {}", () -> cRecords.getId());
    return cRecords;
  }

  /**
   * Insert the important SPSS MetaData to dw_record_metadata. File Metadata such as fileSize is saved in dw_files
   * 
   * @param record
   * @return
   * @throws Exception
   */
  public int insertMetaData(final RecordDTO record) throws Exception {
    log.trace("Entering insertMetaData [recordId: {}]", () -> record.getId());
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
   * @throws SQLException
   */
  public int insertMatrix(final RecordDTO record) throws SQLException {
    log.trace("Entering insertRecordMatrix [recordId: {}, versionId: {}]", () -> record.getId(),
        () -> record.getVersionId());
    final int ret = this.jdbcTemplate.update("INSERT INTO dw_record_matrix (version_id, datamatrix) VALUES (?,?)",
        record.getVersionId(), record.getDataMatrixJson());
    log.debug("Transaction for insertMatrix returned [{}]", () -> ret);
    return ret;
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
   * @throws SQLException
   */
  public long insertVariable(final SPSSVarTDO var, final long versionId) throws SQLException {
    log.trace("Entering insertVariable [varName: {}, versionId: {}]", () -> var.getName(), () -> versionId);
    KeyHolder holder = new GeneratedKeyHolder();
    final String stmt = "INSERT INTO dw_record_variables (version_id, name, type, varType, decimals, width, missingFormat, "
        + "missingVal1, missingVal2, missingVal3, aligment, measureLevel, role, numOfAttributes) "
        + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    int res = this.jdbcTemplate.update(new PreparedStatementCreator() {
      @Override
      public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(stmt, Statement.RETURN_GENERATED_KEYS);
        ps.setLong(1, versionId);
        ps.setString(2, var.getName());
        ps.setInt(3, var.getType().getNumber());
        ps.setInt(4, var.getVarType());
        ps.setInt(5, var.getDecimals());
        ps.setInt(6, var.getWidth());
        ps.setInt(7, var.getMissingFormat() == null ? 0 : var.getMissingFormat().getNumber());
        ps.setString(8, var.getMissingVal1());
        ps.setString(9, var.getMissingVal2());
        ps.setString(10, var.getMissingVal3());
        ps.setInt(11, var.getAligment() == null ? 0 : var.getAligment().getNumber());
        ps.setInt(12, var.getMeasureLevel() == null ? 0 : var.getMeasureLevel().getNumber());
        ps.setInt(13, var.getRole() == null ? 0 : var.getRole().getNumber());
        ps.setInt(14, var.getNumOfAttributes());
        return ps;
      }
    }, holder);
    final long key = (holder.getKey().intValue() > 0) ? holder.getKey().longValue() : -1;
    log.debug("Transaction for insertVariable returned [res: {}, key: {}]", () -> res, () -> key);
    return key;
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