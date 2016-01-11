package de.zpid.datawiz.dto;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDateTime;

import org.springframework.web.multipart.MultipartFile;

public class FileDTO implements Serializable {

  private static final long serialVersionUID = 8870297487540539010L;

  private int id;
  private int projectId;
  private int userId;

  private String fileName;
  private String contentType;
  private long fileSize;
  private String sha256Checksum;
  private String sha1Checksum;
  private String md5checksum;
  private LocalDateTime uploadDate;
  private BufferedImage thumbnail;
  private byte[] content;

  public FileDTO() {
    super();
  }

  public FileDTO(int id, int projectId, int userId, String filename, String contentType, long fileSize,
      String sha256Checksum, String sha1Checksum, String md5checksum, LocalDateTime uploadDate, BufferedImage thumbnail,
      byte[] content) {
    super();
    this.id = id;
    this.projectId = projectId;
    this.userId = userId;
    this.fileName = filename;
    this.contentType = contentType;
    this.fileSize = fileSize;
    this.sha256Checksum = sha256Checksum;
    this.sha1Checksum = sha1Checksum;
    this.md5checksum = md5checksum;
    this.uploadDate = uploadDate;
    this.thumbnail = thumbnail;
    this.content = content;
  }

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

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getProjectId() {
    return projectId;
  }

  public void setProjectId(int projectId) {
    this.projectId = projectId;
  }

  public int getUserId() {
    return userId;
  }

  public void setUserId(int userId) {
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
}
