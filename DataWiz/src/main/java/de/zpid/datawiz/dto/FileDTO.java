package de.zpid.datawiz.dto;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Arrays;

import org.springframework.web.multipart.MultipartFile;

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
  public String toString() {
    return "FileDTO [id=" + id + ", projectId=" + projectId + ", studyId=" + studyId + ", recordID=" + recordID
        + ", version=" + version + ", userId=" + userId + ", fileName=" + fileName + ", contentType=" + contentType
        + ", fileSize=" + fileSize + ", sha256Checksum=" + sha256Checksum + ", sha1Checksum=" + sha1Checksum
        + ", md5checksum=" + md5checksum + ", uploadDate=" + uploadDate + ", thumbnail=" + thumbnail + ", filePath="
        + filePath + ", content=" + Arrays.toString(content) + "]";
  }
}