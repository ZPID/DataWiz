package de.zpid.datawiz.dto;

import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;


/**
 * File Data Transfer Object
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
public class FileDTO implements Serializable {

    private static final long serialVersionUID = 8870297487540539010L;

    private long id;
    private long projectId;
    private long studyId;
    private long recordID;
    private long version;
    private long userId;

    private String fileName;
    private String contentType;
    private long fileSize;
    private String sha256Checksum;
    private String sha1Checksum;
    private String md5checksum;
    private LocalDateTime uploadDate;
    private BufferedImage thumbnail;
    private String filePath;
    private byte[] content;

    public void setMultipartFile(MultipartFile multipartFile) throws IOException {
        if (this.content == null || this.content.length <= 0)
            this.content = multipartFile.getBytes();
        if (this.contentType == null || this.contentType.isEmpty())
            this.contentType = multipartFile.getContentType();
        if (this.fileSize == 0)
            this.fileSize = multipartFile.getSize();
        if (this.fileName == null || this.fileName.isEmpty())
            this.fileName = multipartFile.getOriginalFilename();
    }

    public BufferedImage getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(BufferedImage thumbnail) {
        this.thumbnail = thumbnail;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String filename) {
        this.fileName = filename;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getProjectId() {
        return projectId;
    }

    public void setProjectId(long projectId) {
        this.projectId = projectId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getSha256Checksum() {
        return sha256Checksum;
    }

    public void setSha256Checksum(String sha256Checksum) {
        this.sha256Checksum = sha256Checksum;
    }

    public String getSha1Checksum() {
        return sha1Checksum;
    }

    public void setSha1Checksum(String sha1Checksum) {
        this.sha1Checksum = sha1Checksum;
    }

    public String getMd5checksum() {
        return md5checksum;
    }

    public void setMd5checksum(String md5checksum) {
        this.md5checksum = md5checksum;
    }

    public LocalDateTime getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(LocalDateTime uploadDate) {
        this.uploadDate = uploadDate;
    }

    public long getStudyId() {
        return studyId;
    }

    public void setStudyId(long studyId) {
        this.studyId = studyId;
    }

    public long getRecordID() {
        return recordID;
    }

    public void setRecordID(long recordID) {
        this.recordID = recordID;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileDTO fileDTO = (FileDTO) o;
        return id == fileDTO.id &&
                projectId == fileDTO.projectId &&
                studyId == fileDTO.studyId &&
                recordID == fileDTO.recordID &&
                version == fileDTO.version &&
                userId == fileDTO.userId &&
                fileSize == fileDTO.fileSize &&
                Objects.equals(fileName, fileDTO.fileName) &&
                Objects.equals(contentType, fileDTO.contentType) &&
                Objects.equals(sha256Checksum, fileDTO.sha256Checksum) &&
                Objects.equals(sha1Checksum, fileDTO.sha1Checksum) &&
                Objects.equals(md5checksum, fileDTO.md5checksum) &&
                Objects.equals(uploadDate, fileDTO.uploadDate) &&
                Objects.equals(thumbnail, fileDTO.thumbnail) &&
                Objects.equals(filePath, fileDTO.filePath) &&
                Arrays.equals(content, fileDTO.content);
    }

    @Override
    public int hashCode() {

        int result = Objects.hash(id, projectId, studyId, recordID, version, userId, fileName, contentType, fileSize, sha256Checksum, sha1Checksum, md5checksum, uploadDate, thumbnail, filePath);
        result = 31 * result + Arrays.hashCode(content);
        return result;
    }

    @Override
    public String toString() {
        return "FileDTO{" +
                "id=" + id +
                ", projectId=" + projectId +
                ", studyId=" + studyId +
                ", recordID=" + recordID +
                ", version=" + version +
                ", userId=" + userId +
                ", fileName='" + fileName + '\'' +
                ", contentType='" + contentType + '\'' +
                ", fileSize=" + fileSize +
                ", sha256Checksum='" + sha256Checksum + '\'' +
                ", sha1Checksum='" + sha1Checksum + '\'' +
                ", md5checksum='" + md5checksum + '\'' +
                ", uploadDate=" + uploadDate +
                ", thumbnail=" + thumbnail +
                ", filePath='" + filePath + '\'' +
                ", content=" + Arrays.toString(content) +
                '}';
    }
}