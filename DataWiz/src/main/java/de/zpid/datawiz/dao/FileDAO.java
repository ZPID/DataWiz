package de.zpid.datawiz.dao;

import de.zpid.datawiz.dto.FileDTO;
import de.zpid.datawiz.dto.ProjectDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * DAO Class for File
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
public class FileDAO {

    private final ClassPathXmlApplicationContext applicationContext;
    private final JdbcTemplate jdbcTemplate;
    private static final Logger log = LogManager.getLogger(FileDAO.class);

    /**
     * Instantiates a new file DAO.
     */
    @Autowired
    public FileDAO(ClassPathXmlApplicationContext applicationContext, JdbcTemplate jdbcTemplate) {
        super();
        log.info("Loading FileDAO as @Scope(\"singleton\") and @Repository");
        this.applicationContext = applicationContext;
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * This function searches for all matching project file entities in the table dmp_files by the passed identifiers.
     *
     * @param project {@link ProjectDTO} with the required project identifier
     * @return {@link List} of {@link FileDTO}, which contains the selected subset
     */
    public List<FileDTO> findProjectMaterialFiles(final ProjectDTO project) {
        log.trace("Entering findProjectMaterialFiles for project [id: {}; name: {}]", project::getId, project::getTitle);
        String sql = "SELECT * FROM dw_files WHERE dw_files.project_id = ? AND dw_files.study_id IS NULL "
                + "AND dw_files.record_id IS NULL AND dw_files.version_id IS NULL ORDER BY dw_files.id DESC";
        List<FileDTO> files = jdbcTemplate.query(sql, new Object[]{project.getId()}, (resultSet, rowNum) -> setFileDTO(resultSet));
        log.debug("Transaction \"findProjectMaterialFiles\" terminates with result: [length: {}]", files::size);
        return files;
    }

    /**
     * This function searches for all matching study file entities in the table dmp_files by the passed identifiers.
     *
     * @param pid     Project identifier as long
     * @param studyId Study identifier as long
     * @return {@link List} of {@link FileDTO}, which contains the selected subset
     */
    public List<FileDTO> findStudyMaterialFiles(final long pid, final long studyId) {
        log.trace("Entering findStudyMaterialFiles for study [id: {}; pid: {}]", () -> studyId, () -> pid);
        String sql = "SELECT * FROM dw_files WHERE dw_files.project_id = ? AND dw_files.study_id = ? "
                + "AND dw_files.record_id IS NULL AND dw_files.version_id IS NULL ORDER BY dw_files.id DESC";
        List<FileDTO> files = jdbcTemplate.query(sql, new Object[]{pid, studyId}, (resultSet, rowNum) -> setFileDTO(resultSet));
        log.debug("Transaction \"findStudyMaterialFiles\" terminates with result: [length: {}]", files::size);
        return files;
    }

    /**
     * This function searches for a file entity in the table dmp_files by the passed identifier. If an entity has been found a FileDTO object will be returned,
     * otherwise null.
     *
     * @param id File identifier as long
     * @return {@link FileDTO}, which contains all attributes if entity has been found, otherwise null
     */
    public FileDTO findById(final long id) {
        log.trace("Entering findById for study [id: {}]", () -> id);
        FileDTO file;
        try {
            file = jdbcTemplate.queryForObject("SELECT * FROM dw_files WHERE dw_files.id = ?", new Object[]{id}, (resultSet, rowNum) -> setFileDTO(resultSet));
        } catch (EmptyResultDataAccessException e) {
            file = null;
        }
        log.debug("Transaction \"findById\" terminates with result: [file: {}]", file != null ? file.getId() : "null");
        return file;
    }

    /**
     * This function saves a new file entity into the table dw_files.
     *
     * @param file Contains all file attributes
     */
    public void saveFile(final FileDTO file) {
        log.trace("Entering saveFile for file [name:{}]", file::getFileName);
        int ret = this.jdbcTemplate.update(
                "INSERT INTO dw_files (project_id, study_id, record_id, version_id, user_id, name, size, description, contentType, sha1, sha256, md5, uploadDate, filePath) "
                        + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
                file.getProjectId(), file.getStudyId() == 0 ? null : file.getStudyId(), file.getRecordID() == 0 ? null : file.getRecordID(),
                file.getVersion() == 0 ? null : file.getVersion(), file.getUserId(), file.getFileName(), file.getFileSize(), file.getDescription(), file.getContentType(),
                file.getSha1Checksum(), file.getSha256Checksum(), file.getMd5checksum(), file.getUploadDate(), file.getFilePath());
        log.debug("Transaction \"saveFile\" terminates with result: [result: {}]", () -> ret);
    }

    /**
     * This function updates a description of a file entity which belongs to the passed id.
     *
     * @param id   File identifier as long
     * @param desc {@link String} File description
     * @return Number of Changes - should be > 0
     */
    public int updateFileDescription(final long id, final String desc) {
        log.trace("Entering updateFileDescription for file [id:{}]", () -> id);
        int ret = this.jdbcTemplate.update("UPDATE dw_files SET description =? WHERE id = ?", desc, id);
        log.debug("Transaction \"updateFileDescription\" terminates with result: [result: {}]", () -> ret);
        return ret;
    }

    /**
     * This function deletes a file entity from the table dw_files with the passed identifier.
     *
     * @param id File identifier as long
     */
    public void deleteFile(final long id) {
        log.trace("Entering deleteFile for file [id: {}]", () -> id);
        int ret = this.jdbcTemplate.update("DELETE FROM dw_files WHERE id = ? ", id);
        log.debug("Transaction \"deleteFile\" terminates with result: [result: {}]", () -> ret);
    }

    /**
     * This function transfers the values from the ResultSet to a FileDTO
     *
     * @param rs {@link ResultSet}
     * @return {@link FileDTO}
     * @throws SQLException DBS Exceptions
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
        file.setDescription(rs.getString("description"));
        file.setContentType(rs.getString("contentType"));
        file.setSha1Checksum(rs.getString("sha1"));
        file.setSha256Checksum(rs.getString("sha256"));
        file.setMd5checksum(rs.getString("md5"));
        file.setUploadDate(rs.getTimestamp("uploadDate").toLocalDateTime());
        file.setFilePath(rs.getString("filePath"));
        return file;
    }
}