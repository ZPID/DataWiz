package de.zpid.test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.GridFSUploadStream;
import com.mongodb.client.gridfs.model.GridFSUploadOptions;

public class MongoDBTest {

  static MongoClient mongoClient = new MongoClient("localhost", 27017);

  public static void main(String[] args) {

    MongoDatabase database = mongoClient.getDatabase("datawiz");
//    MongoCollection<Document> collection = database.getCollection("ds_1234");
//    Document doc = new Document("name", "MongoDB").append("type", "database").append("count", 1).append("info",
//        new Document("x", 203).append("y", 102));
//    MongoIterable<String> de = mongoClient.listDatabaseNames();
//    for (String s : de) {
//      System.out.println(s);
//    }
//    MongoIterable<String> d = database.listCollectionNames();
//    for (String s : d) {
//      System.out.println(s);
//    }
//    collection.insertOne(doc);
    saveFileToMongoDB(database);
  }

  private static void saveFileToMongoDB(MongoDatabase db) {
    GridFSBucket gridFSBucket = GridFSBuckets.create(db, "project_files");
    Path path = Paths.get("C:\\Users\\ronny\\Downloads\\P1011594.psd");
    byte[] data = null;
    try {
      data = Files.readAllBytes(path);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    GridFSUploadOptions options = new GridFSUploadOptions().metadata(new Document("type", "presentation").append("version", 1));
    GridFSUploadStream uploadStream = gridFSBucket.openUploadStream(path.getFileName().toString(), options);
    uploadStream.write(data);
    uploadStream.close();
    System.out.println("The fileId of the uploaded file is: " + uploadStream.getFileId().toHexString());
  }

}
