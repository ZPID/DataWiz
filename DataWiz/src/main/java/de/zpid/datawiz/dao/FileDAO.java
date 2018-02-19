package de.zpid.datawiz.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import de.zpid.datawiz.dto.FileDTO;
import de.zpid.datawiz.dto.ProjectDTO;

/**
 * This file is part of Datawiz
 * 
 * <b>Copyright 2018, Leibniz Institute for Psychology Information (ZPID), <a href="http://zpid.de" title="http://zpid.de">http://zpid.de</a>.</b><br />
 * <br />
 * <a rel="license" href="http://creativecommons.org/licenses/by-nc-sa/4.0/"><img alt="Creative Commons License" style= "border-width:0" src=
 * "https://i.creativecommons.org/l/by-nc-sa/4.0/80x15.png" /></a><br />
 * <span xmlns:dct="http://purl.org/dc/terms/" property="dct:title">Datawiz</span> by
 * <a xmlns:cc="http://creativecommons.org/ns#" href="zpid.de" property="cc:attributionName" rel="cc:attributionURL"> Leibniz Institute for Psychology
 * Information (ZPID)</a> is licensed under a <a rel="license" href="http://creativecommons.org/licenses/by-nc-sa/4.0/">Creative Commons
 * Attribution-NonCommercial-ShareAlike 4.0 International License</a>. <br />
 * <br />
 * 
 * @author Ronny Boelter
 * @version 1.0
 *
 */
@Repository
@Scope("singleton")
public class FileDAO {

	@Autowired
	private ClassPathXmlApplicationContext applicationContext;
	@Autowired
	private JdbcTemplate jdbcTemplate;
	private static Logger log = LogManager.getLogger(FileDAO.class);

	/**
	 * Instantiates a new file DAO.
	 */
	public FileDAO() {
		super();
		log.info("Loading FileDAO as Singleton and Service");
	}

	/**
	 * This function searches for all matching project file entities in the table dmp_files by the passed identifiers.
	 * 
	 * @param ProjectDTO
	 *          ProjectDTO with the required project identifier
	 * @return List of Files, which contains the selected subset
	 * @throws Exception
	 */
	public List<FileDTO> findProjectMaterialFiles(final ProjectDTO project) throws Exception {
		log.trace("Entering findProjectMaterialFiles for project [id: {}; name: {}]", () -> project.getId(), () -> project.getTitle());
		String sql = "SELECT * FROM dw_files WHERE dw_files.project_id = ? AND dw_files.study_id IS NULL "
		    + "AND dw_files.record_id IS NULL AND dw_files.version_id IS NULL ORDER BY dw_files.uploadDate DESC";
		List<FileDTO> files = jdbcTemplate.query(sql, new Object[] { project.getId() }, new RowMapper<FileDTO>() {
			public FileDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
				return setFileDTO(rs);
			}
		});
		log.debug("Transaction \"findProjectMaterialFiles\" terminates with result: [length: {}]", () -> ((files != null) ? files.size() : "null"));
		return files;
	}

	/**
	 * This function searches for all matching study file entities in the table dmp_files by the passed identifiers.
	 * 
	 * @param pid
	 *          Project identifier
	 * @param studyId
	 *          Study identifier
	 * @return List of Files, which contains the selected subset
	 * @throws Exception
	 */
	public List<FileDTO> findStudyMaterialFiles(final long pid, final long studyId) throws Exception {
		log.trace("Entering findStudyMaterialFiles for study [id: {}; pid: {}]", () -> studyId, () -> pid);
		String sql = "SELECT * FROM dw_files WHERE dw_files.project_id = ? AND dw_files.study_id = ? "
		    + "AND dw_files.record_id IS NULL AND dw_files.version_id IS NULL ORDER BY dw_files.uploadDate DESC";
		List<FileDTO> files = jdbcTemplate.query(sql, new Object[] { pid, studyId }, new RowMapper<FileDTO>() {
			public FileDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
				return setFileDTO(rs);
			}
		});
		log.debug("Transaction \"findStudyMaterialFiles\" terminates with result: [length: {}]", () -> ((files != null) ? files.size() : "null"));
		return files;
	}

	/**
	 * This function searches for a file entity in the table dmp_files by the passed identifier. If an entity has been found a FileDTO object will be returned,
	 * otherwise null.
	 * 
	 * @param id
	 *          File identifier
	 * @return FileDTO, which contains all attributes if entity has been found, otherwise null
	 * @throws Exception
	 */
	public FileDTO findById(final long id) throws Exception {
		log.trace("Entering findById for study [id: {}]", () -> id);
		FileDTO file = jdbcTemplate.query("SELECT * FROM dw_files WHERE dw_files.id = ?", new Object[] { id }, new ResultSetExtractor<FileDTO>() {
			@Override
			public FileDTO extractData(ResultSet rs) throws SQLException, DataAccessException {
				if (rs.next()) {
					return setFileDTO(rs);
				}
				return null;
			}
		});
		log.debug("Transaction \"findById\" terminates with result: [file: {}]", () -> file);
		return file;
	}

	/**
	 * This function saves a new file entity into the table dw_files.
	 * 
	 * @param file
	 *          Contains all file attributes
	 * @return 1 if changes have happened, otherwise 0
	 * @throws Exception
	 */
	public int saveFile(final FileDTO file) throws Exception {
		log.trace("Entering saveFile for file [{}]", () -> file);
		int ret = this.jdbcTemplate.update(
		    "INSERT INTO dw_files (project_id, study_id, record_id, version_id, user_id, name, size, contentType, sha1, sha256, md5, uploadDate, filePath) "
		        + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)",
		    file.getProjectId(), file.getStudyId() == 0 ? null : file.getStudyId(), file.getRecordID() == 0 ? null : file.getRecordID(),
		    file.getVersion() == 0 ? null : file.getVersion(), file.getUserId(), file.getFileName(), file.getFileSize(), file.getContentType(),
		    file.getSha1Checksum(), file.getSha256Checksum(), file.getMd5checksum(), file.getUploadDate(), file.getFilePath());
		log.debug("Transaction \"saveFile\" terminates with result: [result: {}]", () -> ret);
		return ret;
	}

	/**
	 * This function deletes a file entity from the table dw_files with the passed identifier.
	 * 
	 * @param id
	 *          File identifier
	 * @return 1 if changes have happened, otherwise 0
	 * @throws Exception
	 */
	public int deleteFile(final long id) throws Exception {
		log.trace("Entering deleteFile for file [id: {}]", () -> id);
		int ret = this.jdbcTemplate.update("DELETE FROM dw_files WHERE id = ? ", id);
		log.debug("Transaction \"deleteFile\" terminates with result: [result: {}]", () -> ret);
		return ret;
	}

	/**
	 * This function transfers the values from the ResultSet to a FileDTO
	 * 
	 * @param rs
	 *          ResultSet
	 * @return FileDTO
	 * @throws SQLException
	 */
	private FileDTO setFileDTO(final ResultSet rs) throws SQLException {
		FileDTO file = (FileDTO) applicationContext.getBean("FileDTO");
		file.setId(rs.getInt("id"));
		file.setProjectId(rs.getInt("project_id"));
		file.setStudyId(rs.getLong("study_id"));
		file.setRecordID(rs.getLong("record_id"));
		file.setVersion(rs.getLong("version_id"));
		file.setUserId(rs.getInt("user_id"));
		file.setFileName(rs.getString("name"));
		file.setFileSize(rs.getLong("size"));
		file.setContentType(rs.getString("contentType"));
		file.setSha1Checksum(rs.getString("sha1"));
		file.setSha256Checksum(rs.getString("sha256"));
		file.setMd5checksum(rs.getString("md5"));
		file.setUploadDate(rs.getTimestamp("uploadDate").toLocalDateTime());
		file.setFilePath(rs.getString("filePath"));
		return file;
	}
}