package de.zpid.datawiz.util;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;

import javax.xml.bind.annotation.adapters.HexBinaryAdapter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import de.zpid.datawiz.dto.FileDTO;

@Repository
@Scope("singleton")
public class FileUtil {

  private static Logger log = LogManager.getLogger(FileUtil.class);
  final static String OS = System.getProperty("os.name").toLowerCase();
  @Autowired
  protected ClassPathXmlApplicationContext applicationContext;

  /**
   * 
   * @param digest
   * @param hash
   * @return
   */
  public String getFileChecksum(MessageDigest digest, final byte[] hash) {
    return (new HexBinaryAdapter()).marshal(digest.digest(hash)).toLowerCase();
  }

  /**
   * 
   * @param src
   * @param w
   * @param h
   * @return
   */
  public BufferedImage scaleImage(BufferedImage src, int w, int h) {
    BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
    int x, y;
    int ww = src.getWidth();
    int hh = src.getHeight();
    for (x = 0; x < w; x++) {
      for (y = 0; y < h; y++) {
        int col = src.getRGB(x * ww / w, y * hh / h);
        img.setRGB(x, y, col);
      }
    }
    return img;
  }

  public FileDTO buildFileDTO(final long projectID, final long studyID, final long recordID, final long version,
      final long userID, final MultipartFile mpf) throws IOException, NoSuchAlgorithmException {
    FileDTO file = (FileDTO) applicationContext.getBean("FileDTO");
    file.setMultipartFile(mpf);
    file.setMd5checksum(getFileChecksum(MessageDigest.getInstance("MD5"), file.getContent()));
    file.setSha1Checksum(getFileChecksum(MessageDigest.getInstance("SHA-1"), file.getContent()));
    file.setSha256Checksum(getFileChecksum(MessageDigest.getInstance("SHA-256"), file.getContent()));
    file.setProjectId(projectID);
    file.setStudyId(studyID);
    file.setRecordID(recordID);
    file.setVersion(version);
    file.setUserId(userID);
    file.setUploadDate(LocalDateTime.now());
    return file;
  }

  // public String saveFile(final FileDTO file) {
  // log.trace("Entering saveFile(on FileSystem) for file [name: {}] and Project [id: {}]", () -> file.getFileName(),
  // () -> file.getProjectId());
  // List<String> subfolders = new ArrayList<String>();
  // if (file.getProjectId() > 0) {
  // subfolders.add(String.valueOf(file.getProjectId()));
  // if (file.getStudyId() > 0) {
  // subfolders.add(String.valueOf(file.getStudyId()));
  // if (file.getRecordID() > 0) {
  // subfolders.add(String.valueOf(file.getRecordID()));
  // if (file.getVersion() > 0) {
  // subfolders.add(String.valueOf(file.getVersion()));
  // }
  // }
  // }
  // }
  // String dir = setFolderPath(subfolders.stream().toArray(String[]::new));
  // String randomizedFileName = UUID.randomUUID().toString();
  // if (dir == null || dir.isEmpty())
  // return null;
  // try {
  // Files.createDirectories(Paths.get(dir));
  // BufferedOutputStream buffStream = new BufferedOutputStream(
  // new FileOutputStream(new File(dir + randomizedFileName)));
  // buffStream.write(file.getContent());
  // buffStream.close();
  // } catch (IOException e) {
  // e.printStackTrace();
  // return null;
  // }
  // return dir + randomizedFileName;
  // }
  //
  // public void setFileBytes(final FileDTO file) {
  // try {
  // file.setContent(Files.readAllBytes(Paths.get(file.getFilePath())));
  // } catch (IOException e) {
  // // TODO Auto-generated catch block NoSuchFileException abfangen!!!
  // e.printStackTrace();
  // }
  // }
  //
  // public String setFolderPath(final String... args) {
  // StringBuffer ret = new StringBuffer();
  // if (OS.contains("win")) {
  // ret.append("C:\\DataWiz\\");
  // for (String s : args) {
  // ret.append(s + "\\");
  // }
  // } else if (OS.contains("mac") || OS.contains("nix") || OS.contains("nux") || OS.contains("aix")
  // || OS.contains("sunos")) {
  // ret.append("/DataWiz/");
  // for (String s : args) {
  // ret.append(s + "/");
  // }
  // } else {
  // return null;
  // }
  // return ret.toString();
  // }
  //
  // public boolean deleteFile(FileDTO file) {
  // Path path = Paths.get(file.getFilePath());
  //
  // try {
  // Files.delete(path);
  // } catch (NoSuchFileException x) {
  // System.err.format("%s: no such" + " file or directory%n", path);
  // } catch (DirectoryNotEmptyException x) {
  // System.err.format("%s not empty%n", path);
  // } catch (IOException x) {
  // // File permission problems are caught here.
  // System.err.println(x);
  // }
  // try {
  // DirectoryStream<Path> ds = Files.newDirectoryStream(path.getParent());
  // if (!ds.iterator().hasNext())
  // Files.deleteIfExists(path.getParent());
  // ds.close();
  // } catch (IOException e) {
  // // TODO Auto-generated catch block
  // e.printStackTrace();
  // }
  // return true;
  // }
  //
  // public boolean deleteFolderRecursive(String path) {
  // try {
  // Files.walkFileTree(Paths.get(path), new SimpleFileVisitor<Path>() {
  // @Override
  // public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
  // Files.delete(file);
  // return FileVisitResult.CONTINUE;
  // }
  //
  // @Override
  // public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
  // Files.delete(dir);
  // return FileVisitResult.CONTINUE;
  // }
  //
  // });
  // } catch (IOException e) {
  // // TODO Auto-generated catch block
  // e.printStackTrace();
  // return false;
  // }
  // return true;
  // }
}
