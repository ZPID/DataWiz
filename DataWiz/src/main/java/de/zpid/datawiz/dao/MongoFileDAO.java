package de.zpid.datawiz.dao;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoGridFSException;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.GridFSFindIterable;
import com.mongodb.client.gridfs.GridFSUploadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.client.gridfs.model.GridFSUploadOptions;

import de.zpid.datawiz.dto.FileDTO;


public class MongoFileDAO extends SuperDAO {

  @Autowired
  MongoDatabase mongoDatabase;

  public MongoFileDAO() {
    super();
    if (log.isInfoEnabled())
      log.info("Loading FileDAO as Singleton and Service");
  }

  /**
   * 
   * @param id
   * @return
   * @throws Exception
   */
  public int deleteFile(final String id, final long projectID) throws Exception {
    log.trace("Entering deleteFile(MongoDB) [id: {}] from project [id: {}]", () -> id, () -> projectID);
    GridFSBucket bucket = GridFSBuckets.create(mongoDatabase, "project_" + projectID + "_files");
    bucket.delete(new ObjectId(id));
    if (!bucket.find().iterator().hasNext()) {
      bucket.drop();
    }
    return 1;
  }

  public String saveFileToMongo(FileDTO file, long projectID) throws MongoGridFSException {
    log.trace("Entering saveFile(MongoDB) file: {}", () -> file.getFileName());
    GridFSBucket gridFSBucket = GridFSBuckets.create(mongoDatabase, "project_" + projectID + "_files");
    Document doc = new Document("contentType", file.getContentType());
    doc.append("projectId", file.getProjectId());
    doc.append("studyId", file.getStudyId());
    doc.append("recordID", file.getRecordID());
    doc.append("version", file.getVersion());
    doc.append("userId", file.getUserId());
    doc.append("fileName", file.getFileName());
    doc.append("fileSize", file.getFileSize());
    doc.append("sha256Checksum", file.getSha256Checksum());
    doc.append("sha1Checksum", file.getSha1Checksum());
    doc.append("md5checksum", file.getMd5checksum());
    GridFSUploadStream uploadStream = gridFSBucket.openUploadStream(file.getFileName().toString(),
        new GridFSUploadOptions().metadata(doc));
    uploadStream.write(file.getContent());
    uploadStream.close();
    return uploadStream.getFileId().toHexString();
  }

  public FileDTO findById(final String id, final long projectID) throws MongoGridFSException, IOException {
    log.trace("Entering findById(MongoDB) for FileID [id: {}]", () -> id);
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    GridFSBucket gridFSBucket = GridFSBuckets.create(mongoDatabase, "project_" + projectID + "_files");
    GridFSDownloadStream dstream = gridFSBucket.openDownloadStream(new ObjectId(id));
    GridFSFile file = dstream.getGridFSFile();
    int fileLength = (int) dstream.getGridFSFile().getLength();
    System.out.println(fileLength);
    gridFSBucket.downloadToStream(new ObjectId(id), bos);
    dstream.close();
    FileDTO fdto = setFileDTO(file);
    fdto.setContent(bos.toByteArray());
    bos.close();
    return fdto;
  }

  public List<FileDTO> findProjectFiles(final long projectID, final long studyID, final long recordID,
      final boolean allVersions, final long version) throws MongoGridFSException {
    log.trace("Entering findProjectFile(MongoDB) for project [projectID: {}; studyID: {}; recordID: {}; version: {}]",
        () -> projectID, () -> studyID, () -> recordID, () -> version);
    GridFSBucket gridFSBucket = GridFSBuckets.create(mongoDatabase, "project_" + projectID + "_files");
    BasicDBObject query = new BasicDBObject("metadata.projectId", projectID).append("metadata.studyId", studyID)
        .append("metadata.recordID", recordID);
    if (!allVersions)
      query.append("metadata.version", version);
    GridFSFindIterable fItt = gridFSBucket.find(query);
    List<FileDTO> fList = new ArrayList<FileDTO>();
    if (fItt != null)
      for (GridFSFile file : fItt) {
        fList.add(setFileDTO(file));
      }
    return fList;
  }

  /**
   * @param file
   * @return
   */
  private FileDTO setFileDTO(final GridFSFile file) {
    FileDTO fdto = (FileDTO) context.getBean("FileDTO");
    //fdto.setId(file.getObjectId().toHexString());
    fdto.setProjectId(file.getMetadata().getLong("projectId"));
    fdto.setUserId(file.getMetadata().getLong("studyId"));
    fdto.setUserId(file.getMetadata().getLong("recordID"));
    fdto.setUserId(file.getMetadata().getLong("version"));
    fdto.setUserId(file.getMetadata().getLong("userId"));
    fdto.setFileName(file.getMetadata().getString("fileName"));
    fdto.setContentType(file.getMetadata().getString("contentType"));
    fdto.setFileSize(file.getMetadata().getLong("fileSize"));
    fdto.setSha1Checksum(file.getMetadata().getString("sha256Checksum"));
    fdto.setSha1Checksum(file.getMetadata().getString("sha1Checksum"));
    fdto.setMd5checksum(file.getMetadata().getString("md5checksum"));
    fdto.setUploadDate(LocalDateTime.ofInstant(file.getUploadDate().toInstant(), ZoneId.systemDefault()));
    return fdto;
  }

  // TODO find funktionen = merke: {"metadata.projectId" : 1}
}
