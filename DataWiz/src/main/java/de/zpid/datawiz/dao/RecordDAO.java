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

import de.zpid.datawiz.dto.RecordDTO;

@Repository
@Scope("singleton")
public class RecordDAO extends SuperDAO {

  private static Logger log = LogManager.getLogger(RecordDAO.class);

  public RecordDAO() {
    super();
    if (log.isInfoEnabled())
      log.info("Loading RecordDAO as Singleton and Service");
  }

  public List<RecordDTO> findRecordsWithStudyID(final long studyId) {
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

  public RecordDTO findRecordWithID(final long recordId) {
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

}
