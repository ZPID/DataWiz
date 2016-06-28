package de.zpid.datawiz.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.ConnectException;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.env.Environment;

import de.zpid.datawiz.dto.FileDTO;
import de.zpid.datawiz.enumeration.MinioResult;
import io.minio.MinioClient;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InvalidEndpointException;
import io.minio.errors.InvalidPortException;

public class MinioDAO {

  private Environment env;
  private static Logger log = LogManager.getLogger(MinioDAO.class);
  private MinioClient minioClient;

  public MinioDAO(Environment env) {
    super();
    this.env = env;
    log.info("Loading MinioDAO with ServerAddress: {}", () -> env.getRequiredProperty("minio.url"));
    try {
      this.minioClient = new MinioClient(env.getRequiredProperty("minio.url"),
          env.getRequiredProperty("minio.access.key"), env.getRequiredProperty("minio.secret.key"));
    } catch (InvalidEndpointException | InvalidPortException | IllegalStateException e) {
      log.error("ERROR: Creating new MinioClient was not successful: Message: {}", () -> e);
    }
  }

  /**
   * 
   * @param minioClient
   * @param file
   * @return
   */
  public MinioResult storeFileIntoMinio(final FileDTO file) {
    log.trace("Entering storeFileIntoMinio for file: [name: {}]", () -> file.getFileName());
    final String bucket = env.getRequiredProperty("minio.bucket.prefix") + "." + file.getProjectId();
    String filePath = UUID.randomUUID().toString();
    try {
      if (!this.minioClient.bucketExists(bucket)) {
        log.debug("Bucket [name: {}] does not exists - new bucket created", () -> bucket);
        this.minioClient.makeBucket(bucket);
      }
      while (true) {
        try {
          this.minioClient.statObject(bucket, filePath);
          filePath = UUID.randomUUID().toString();
          log.debug("File [filePath: {}] exists - new filePath created", filePath);
        } catch (Exception e) {
          file.setFilePath(filePath);
          break;
        }
      }
      ByteArrayInputStream bais = new ByteArrayInputStream(file.getContent());
      this.minioClient.putObject(bucket, filePath, bais, bais.available(), file.getContentType());
      bais.close();
    } catch (Exception e) {
      log.error("ERROR: Saving file [name: {}; filePath: {}; bucket: {}] to Minio wasn't successful Message: {}",
          () -> file.getFileName(), () -> file.getFilePath(), () -> bucket, () -> e);
      if (e instanceof ConnectException)
        return MinioResult.CONNECTION_ERROR;
      return MinioResult.ERROR;
    }
    log.debug("Transaction for storeFileIntoMinio sucessful and set file.setFilePath(filePath) to {}",
        () -> file.getFilePath());
    return MinioResult.OK;
  }

  /**
   * 
   * @param minioClient
   * @param file
   * @return
   * @throws Exception
   */
  public MinioResult getFileFromMinio(final FileDTO file) {
    log.trace("Entering getFileFromMinio for file: [name: {}; path: {}]", () -> file.getFileName(),
        () -> file.getFilePath());
    String bucket = env.getRequiredProperty("minio.bucket.prefix") + "." + file.getProjectId();
    try {
      if (!this.minioClient.bucketExists(bucket)) {
        log.fatal("FATAL: Bucket [name: {}] not exists in Minio FileSystem - Please check file system consistency!",
            () -> bucket);
        return MinioResult.BUCKET_NOT_FOUND;
      }
      this.minioClient.statObject(bucket, file.getFilePath());
      InputStream stream = this.minioClient.getObject(bucket, file.getFilePath());
      ByteArrayOutputStream buffer = new ByteArrayOutputStream();
      int nRead;
      byte[] data = new byte[1024];
      while ((nRead = stream.read(data, 0, data.length)) != -1) {
        buffer.write(data, 0, nRead);
      }
      buffer.flush();
      byte[] byteArray = buffer.toByteArray();
      buffer.close();
      stream.close();
      if (byteArray != null && byteArray.length != 0) {
        file.setContent(byteArray);
        return MinioResult.OK;
      }
    } catch (Exception e) {
      if (e instanceof ErrorResponseException && e.toString().contains("Object does not exist")) {
        log.fatal(
            "FATAL: File [id: {}; bucket: {}; filePath: {}] not exists in Minio FileSystem, but in Database - Please check file system and database consistency! Message: {}",
            () -> file.getId(), () -> bucket, () -> file.getFilePath(), () -> e);
        return MinioResult.FILE_NOT_FOUND;
      }
      log.error("ERROR: File [id: {}; bucket: {}; filePath: {}] not loaded from Minio - Message: {}",
          () -> file.getId(), () -> bucket, () -> file.getFilePath(), () -> e);
    }
    return MinioResult.ERROR;
  }

  /**
   * 
   * @param file
   * @return
   */
  public MinioResult deleteFileFromMinio(FileDTO file) {
    String bucket = env.getRequiredProperty("minio.bucket.prefix") + "." + file.getProjectId();
    try {
      if (!this.minioClient.bucketExists(bucket)) {
        log.fatal("FATAL: Bucket [name: {}] not exists in Minio FileSystem - Please check file system consistency!",
            () -> bucket);
        return MinioResult.BUCKET_NOT_FOUND;
      }
      this.minioClient.statObject(bucket, file.getFilePath());
      System.out.println("file exsists");
      this.minioClient.removeObject(bucket, file.getFilePath());
      return MinioResult.OK;
    } catch (Exception e) {
      if (e instanceof ErrorResponseException && e.toString().contains("Object does not exist")) {
        log.fatal(
            "FATAL: File [id: {}; bucket: {}; filePath: {}] not exists in Minio FileSystem, but in Database - Please check file system and database consistency! Message: {}",
            () -> file.getId(), () -> bucket, () -> file.getFilePath(), () -> e);
        return MinioResult.FILE_NOT_FOUND;
      }
      log.error("ERROR: File [id: {}; bucket: {}; filePath: {}] not saved in Minio - Message: {}", () -> file.getId(),
          () -> bucket, () -> file.getFilePath(), () -> e);
      return MinioResult.ERROR;
    }
  }
}
