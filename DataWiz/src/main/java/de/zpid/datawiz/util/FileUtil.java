package de.zpid.datawiz.util;

import de.zpid.datawiz.dto.FileDTO;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author Ronny Boelter
 * @version 1.0
 */
@Component
public class FileUtil {

    private static Logger log = LogManager.getLogger(FileUtil.class);
    private final static String OS = System.getProperty("os.name").toLowerCase();
    @Autowired
    protected ClassPathXmlApplicationContext applicationContext;
    @Autowired
    private Environment env;


    /**
     * This function creates a checksum by the passed String and the file from which the Checksum has to be created.
     *
     * @param digest MessageDigest String (MD5/SHA-1/SHA-256)
     * @param is     The file as byte array
     * @return The checksum as String
     */
    public String getFileChecksum(final String digest, final BufferedInputStream is) {
        try {
            switch (digest) {
                case "MD5":
                    return DigestUtils.md5Hex(is).toLowerCase();
                case "SHA-1":
                    return DigestUtils.sha1Hex(is).toLowerCase();
                case "SHA-256":
                    return DigestUtils.sha256Hex(is).toLowerCase();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * This function scales the passed image to the passed width and height.
     *
     * @param src The image (as BufferedImage), which has to be scaled
     * @param w   the new width
     * @param h   the new height
     * @return the scaled images (as BufferedImage)
     */
    public BufferedImage scaleImage(final BufferedImage src, final int w, final int h) {
        log.trace("Entering scaleImage from [X: {}; Y: {}] to [X: {};Y: {}]", () -> src.getWidth(), () -> src.getHeight(), () -> w, () -> h);
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
        log.trace("Leaving scaleImage with result: [X: {};Y: {}]", () -> img.getWidth(), () -> img.getHeight());
        return img;
    }

    /**
     * If the transferred file is an image, this function creates a thumbnail of it. Before the image is resized to its new size, the correct scale is
     * calculated using the maximum height and width. After scaling, the image is transferred to the response.
     *
     * @param response  The response, for transferring the scaled image to the view.
     * @param file      The file object, which includes the original image
     * @param maxHeight The maximum height of the thumbnail
     * @param maxWidth  The maximum width of the thumbnail
     * @throws IOException
     */
    public void buildThumbNailAndSetToResponse(final HttpServletResponse response, final FileDTO file, final int maxHeight, final int maxWidth)
            throws IOException {
        if (file.getContentType().toLowerCase().contains("image") && file.getContent() != null && !file.getContentType().toLowerCase().contains("icon")) {
            log.trace("Entering buildThumbNailAndSetToResponse for file[name: {}; type: {}] to [X: {};Y: {}]", () -> file.getFileName(),
                    () -> file.getContentType(), () -> maxWidth, () -> maxHeight);
            OutputStream sos = response.getOutputStream();
            BufferedImage bImage = ImageIO.read(new ByteArrayInputStream(file.getContent()));
            int scale = 1;
            if (bImage.getHeight() > maxHeight)
                scale = bImage.getHeight() / maxHeight;
            if (bImage.getWidth() / scale > maxWidth)
                scale = bImage.getWidth() / maxWidth;
            if (scale > 0) {
                BufferedImage bf = scaleImage(bImage, bImage.getWidth() / scale, bImage.getHeight() / scale);
                response.setContentType(file.getContentType());
                ImageIO.write(bf, "jpg", sos);
                sos.flush();
            }
            log.trace("Leaving buildThumbNailAndSetToResponse");
            sos.close();
        }
    }

    /**
     * This function creates a new FileDTO form an uploaded MultiPartFile. All checksums are generated by the passed MultiPartFile and the upload date
     * is set to LocalDateTime.now().
     *
     * @param projectID Project identifier
     * @param studyID   Study identifier
     * @param recordID  Record identifier
     * @param version   Version identifier
     * @param userID    User identifier (file creator)
     * @param mpf       Uploaded file
     * @return The created FileDTO
     * @throws IOException
     */
    public FileDTO buildFileDTO(final long projectID, final long studyID, final long recordID, final long version, final long userID,
                                final MultipartFile mpf) throws IOException {
        FileDTO file = (FileDTO) applicationContext.getBean("FileDTO");
        file.setMd5checksum(getFileChecksum("MD5", new BufferedInputStream(mpf.getInputStream())));
        file.setSha1Checksum(getFileChecksum("SHA-1", new BufferedInputStream(mpf.getInputStream())));
        file.setSha256Checksum(getFileChecksum("SHA-256", new BufferedInputStream(mpf.getInputStream())));
        file.setProjectId(projectID);
        file.setStudyId(studyID);
        file.setRecordID(recordID);
        file.setVersion(version);
        file.setUserId(userID);
        file.setUploadDate(LocalDateTime.now());
        if (file.getContentType() == null || file.getContentType().isEmpty())
            file.setContentType(mpf.getContentType());
        if (file.getFileSize() == 0)
            file.setFileSize(mpf.getSize());
        if (file.getFileName() == null || file.getFileName().isEmpty())
            file.setFileName(mpf.getOriginalFilename());
        if (file.getContent() == null || file.getContent().length <= 0)
            file.setContent(mpf.getBytes());
        return file;
    }

    public FileDTO buildFileDTO(final String content) {
        FileDTO file = (FileDTO) applicationContext.getBean("FileDTO");
        file.setContent(content.getBytes());
        return file;
    }

    /**
     * This function creates a new FileDTO form an uploaded MultiPartFile. All checksums are generated by the passed MultiPartFile and the upload date
     * is set to LocalDateTime.now().
     *
     * @param projectID Project identifier
     * @param studyID   Study identifier
     * @param recordID  Record identifier
     * @param version   Version identifier
     * @param userID    User identifier (file creator)
     * @param mpf       Uploaded file
     * @return The created FileDTO
     * @throws IOException
     */
    public FileDTO buildFileDTOLarge(final long projectID, final long studyID, final long recordID, final long version, final long userID,
                                     final MultipartFile mpf) throws IOException {
        FileDTO file = (FileDTO) applicationContext.getBean("FileDTO");
        file.setMd5checksum(getFileChecksum("MD5", new BufferedInputStream(mpf.getInputStream())));
        file.setSha1Checksum(getFileChecksum("SHA-1", new BufferedInputStream(mpf.getInputStream())));
        file.setSha256Checksum(getFileChecksum("SHA-256", new BufferedInputStream(mpf.getInputStream())));
        file.setProjectId(projectID);
        file.setStudyId(studyID);
        file.setRecordID(recordID);
        file.setVersion(version);
        file.setUserId(userID);
        file.setUploadDate(LocalDateTime.now());
        if (file.getContentType() == null || file.getContentType().isEmpty())
            file.setContentType(mpf.getContentType());
        if (file.getFileSize() == 0)
            file.setFileSize(mpf.getSize());
        if (file.getFileName() == null || file.getFileName().isEmpty())
            file.setFileName(mpf.getOriginalFilename());
        return file;
    }


    /**
     * This function saves a file to the file system. At the moment, it is only be used from validateSPSSFile function of the ImportService, because the
     * SPSS files which are created by this function have to be saved temporary onto the file system.
     *
     * @param file   The file, which has to be saved
     * @param tmpDir True, if the file should have saved into the temporary directory, otherwise the directory and sub-directory name is created from the
     *               given identifier
     * @return A string, including the storage path and the file name
     */
    public String saveFile(final FileDTO file, final boolean tmpDir) {
        log.trace("Entering saveFile(on FileSystem) for file [name: {}] and Project [id: {}]", () -> file.getFileName(), () -> file.getProjectId());
        String ret = null;
        List<String> subfolders = new ArrayList<String>();
        if (!tmpDir) {
            if (file.getProjectId() > 0) {
                subfolders.add(String.valueOf(file.getProjectId()));
                if (file.getStudyId() > 0) {
                    subfolders.add(String.valueOf(file.getStudyId()));
                    if (file.getRecordID() > 0) {
                        subfolders.add(String.valueOf(file.getRecordID()));
                        if (file.getVersion() > 0) {
                            subfolders.add(String.valueOf(file.getVersion()));
                        }
                    }
                }
            }
        } else {
            subfolders.add(env.getProperty("folder.temp.dir"));
        }
        String dir = setFolderPath(subfolders.stream().toArray(String[]::new));
        String randomizedFileName = UUID.randomUUID().toString();
        if (dir != null && !dir.isEmpty())
            try {
                Files.createDirectories(Paths.get(dir));
                BufferedOutputStream buffStream = new BufferedOutputStream(new FileOutputStream(new File(dir + randomizedFileName)));
                buffStream.write(file.getContent());
                buffStream.close();
                ret = dir + randomizedFileName;
            } catch (IOException e) {
                log.error("ERROR: Saving file on local filesystem aborted and null returned! Exception: {}", () -> e);
            }
        log.trace("Leaving saveFile(on FileSystem) for file [name: {}] and Project [id: {}]", () -> file.getFileName(), () -> file.getProjectId());
        return ret;
    }

    /**
     * This function set the entire directory path for the passed varargs, including root directory and sub-directories.
     *
     * @param args folder names as varargs
     * @return Complete directory path
     */
    public String setFolderPath(final String... args) {
        StringBuffer ret = new StringBuffer();
        if (OS.contains("win")) {
            ret.append(env.getProperty("folder.root.windows"));
            for (String s : args) {
                ret.append(s + "\\");
            }
        } else if (OS.contains("mac") || OS.contains("nix") || OS.contains("nux") || OS.contains("aix") || OS.contains("sunos")) {
            ret.append(env.getProperty("folder.root.unix"));
            for (String s : args) {
                ret.append(s + "/");
            }
        } else {
            return null;
        }
        return ret.toString();
    }

    /**
     * This function tries to delete a file and the parent folder (if empty).
     *
     * @param path Path to the file
     * @return true is file was deleted, otherwise false
     */
    public boolean deleteFile(final Path path) {
        boolean ret = false;
        try {
            Files.delete(path);
            ret = true;
        } catch (NoSuchFileException x) {
            log.warn("NoSuchFileException: no such file or directory path: {}", () -> path);
        } catch (DirectoryNotEmptyException x) {
            log.warn("DirectoryNotEmptyException path: {}", () -> path, () -> x);
        } catch (IOException x) {
            log.warn("IOException during deleteFile; Exception: {}", () -> x);
        }
        try {
            DirectoryStream<Path> ds = Files.newDirectoryStream(path.getParent());
            if (!ds.iterator().hasNext())
                Files.deleteIfExists(path.getParent());
            ds.close();
        } catch (IOException e) {
            log.warn("IOException during deleteFile; Exception: {}", () -> e);
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * This function deletes files and folders recursively.
     *
     * @param path Path to the file
     * @return true is folder was deleted, otherwise false
     */
    public boolean deleteFolderRecursive(String path) {
        try {
            Files.walkFileTree(Paths.get(path), new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            log.warn("IOException during deleteFolderRecursive; Exception: {}", () -> e);
            return false;
        }
        return true;
    }
}
