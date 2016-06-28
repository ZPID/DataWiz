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

import de.zpid.datawiz.dto.FileDTO;
import de.zpid.datawiz.dto.ProjectDTO;

@Repository
@Scope("singleton")
public class FileDAO extends SuperDAO {

  private static Logger log = LogManager.getLogger(FileDAO.class);

  public FileDAO() {
    super();
    if (log.isInfoEnabled())
      log.info("Loading FileDAO as Singleton and Service");
  }

  /**
   * 
   * @param project
   * @return
   * @throws Exception
   */
  public List<FileDTO> findProjectFiles(final ProjectDTO project) throws Exception {
    if (log.isDebugEnabled())
      log.debug("execute getProjectFiles for project [id: " + project.getId() + " name: " + project.getTitle() + "]");
    String sql = "SELECT dw_files.id, dw_files.project_id, dw_files.user_id, dw_files.name, dw_files.size, dw_files.contentType,"
        + "dw_files.sha1, dw_files.md5, dw_files.uploadDate, dw_files.filePath FROM dw_files WHERE dw_files.project_id = ? ORDER BY dw_files.uploadDate DESC";
    return jdbcTemplate.query(sql, new Object[] { project.getId() }, new RowMapper<FileDTO>() {
      public FileDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
        return setFileDTO(rs);
      }
    });
  }

  /**
   * 
   * @param id
   * @return
   * @throws Exception
   */
  public FileDTO findById(final long id) throws Exception {
    if (log.isDebugEnabled())
      log.debug("execute findById with id: " + id);
    return jdbcTemplate.query("SELECT * FROM dw_files WHERE dw_files.id = ?", new Object[] { id },
        new ResultSetExtractor<FileDTO>() {
          @Override
          public FileDTO extractData(ResultSet rs) throws SQLException, DataAccessException {
            if (rs.next()) {
              return setFileDTO(rs);
            }
            return null;
          }
        });
  }

  /**
   * 
   * @param file
   * @return
   * @throws Exception
   */
  public int saveFile(final FileDTO file) throws Exception {
    if (log.isDebugEnabled())
      log.debug("execute saveFile file: " + file.getFileName());
    return this.jdbcTemplate.update(
        "INSERT INTO dw_files (project_id, user_id, name, size, contentType, sha1, md5, uploadDate, filePath) VALUES (?,?,?,?,?,?,?,?,?)",
        file.getProjectId(), file.getUserId(), file.getFileName(), file.getFileSize(), file.getContentType(),
        file.getSha1Checksum(), file.getMd5checksum(), file.getUploadDate(), file.getFilePath());
  }

  /**
   * 
   * @param id
   * @return
   * @throws Exception
   */
  public int deleteFile(final long id) throws Exception {
    if (log.isDebugEnabled())
      log.debug("execute deleteFile id: " + id);
    return this.jdbcTemplate.update("DELETE FROM dw_files WHERE id = ? ", id);
  }

  /**
   * @param rs
   * @return
   * @throws SQLException
   */
  private FileDTO setFileDTO(ResultSet rs) throws SQLException {
    FileDTO file = (FileDTO) context.getBean("FileDTO");
    file.setId(rs.getInt("id"));
    file.setProjectId(rs.getInt("project_id"));
    file.setUserId(rs.getInt("user_id"));
    file.setFileName(rs.getString("name"));
    file.setFileSize(rs.getLong("size"));
    file.setContentType(rs.getString("contentType"));
    file.setSha1Checksum(rs.getString("sha1"));
    file.setMd5checksum(rs.getString("md5"));
    file.setUploadDate(rs.getTimestamp("uploadDate").toLocalDateTime());
    file.setFilePath(rs.getString("filePath"));
    return file;
  }
}