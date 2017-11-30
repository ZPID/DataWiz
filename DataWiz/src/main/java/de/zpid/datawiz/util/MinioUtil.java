package de.zpid.datawiz.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.ConnectException;
import java.util.List;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.env.Environment;

import de.zpid.datawiz.dto.FileDTO;
import de.zpid.datawiz.enumeration.MinioResult;
import io.minio.MinioClient;
import io.minio.Result;
import io.minio.errors.ErrorResponseException;
import io.minio.messages.Bucket;
import io.minio.messages.Item;
import io.minio.messages.Upload;

/**
 * This file is part of Datawiz.<br />
 * 
 * <b>Copyright 2016, Leibniz Institute for Psychology Information (ZPID),
 * <a href="http://zpid.de" title="http://zpid.de">http://zpid.de</a>.</b><br />
 * <br />
 * <a rel="license" href="http://creativecommons.org/licenses/by-nc-sa/4.0/"><img alt="Creative Commons License" style= "border-width:0" src=
 * "https://i.creativecommons.org/l/by-nc-sa/4.0/80x15.png" /></a><br />
 * <span xmlns:dct="http://purl.org/dc/terms/" property="dct:title">Datawiz</span> by
 * <a xmlns:cc="http://creativecommons.org/ns#" href="zpid.de" property="cc:attributionName" rel="cc:attributionURL"> Leibniz Institute for Psychology
 * Information (ZPID)</a> is licensed under a <a rel="license" href="http://creativecommons.org/licenses/by-nc-sa/4.0/">Creative Commons
 * Attribution-NonCommercial-ShareAlike 4.0 International License</a>.<br />
 * <br />
 * This class includes all functions to connect to a given Minio-Fileserver, and to PUT,GET,and DELETE files.
 * 
 * @author Ronny Boelter
 * @version 1.0
 *
 */
public class MinioUtil {

	private static final Logger log = LogManager.getLogger(MinioUtil.class);
	private MinioClient minioClient;
	private final String bucketPrefix;

	/**
	 * MinioUtil Constructor - build a MinioClient Object which is used for the connection to the Minio Server. Environment is required, because it
	 * includes the Connection setup
	 * 
	 * @param env
	 */
	public MinioUtil(Environment env, boolean cleanIncomplete) {
		super();
		this.bucketPrefix = env.getRequiredProperty("minio.bucket.prefix") + ".";
		log.info("Loading MinioDAO with [ServerAddress: {}; Bucket_Prefix: {}]", () -> env.getRequiredProperty("minio.url"),
		    () -> env.getRequiredProperty("minio.bucket.prefix"));
		try {
			this.minioClient = new MinioClient(env.getRequiredProperty("minio.url"), env.getRequiredProperty("minio.access.key"),
			    env.getRequiredProperty("minio.secret.key"));
			if (cleanIncomplete) {
				log.info("Minio: cleaning incomplete Uploads");
				List<Bucket> bucketList = minioClient.listBuckets();
				bucketList.parallelStream().forEach(bucket -> {
					Iterable<Result<Upload>> myObjects;
					try {
						myObjects = minioClient.listIncompleteUploads(bucket.name());
						for (Result<Upload> result : myObjects) {
							Upload upload = result.get();
							log.info("Minio: incomplete upload found [Bucked: {}, name: {}]", () -> bucket.name(), () -> upload.objectName());
							minioClient.removeIncompleteUpload(bucket.name(), upload.objectName());
						}
					} catch (Exception e) {
						log.warn("WARN: cleaning incomplete Uploads - Exception during parallelStream().forEach(): ", () -> e);
					}
				});
			}
		} catch (Exception e) {
			log.error("ERROR: Creating MinioClient was not successful: Message: {}", () -> e);
		}
	}

	public void close() {
		this.minioClient = null;
	}

	/**
	 * Function to put a file to the Minio storage To not get problems Unix/Windows file naming convention, this function Changes the Filename to a
	 * generated random UUID. This name is saved as file path in the DataWiz database, and it's unique in a project.
	 * 
	 * @param minioClient
	 * @param file
	 * @return
	 */
	public MinioResult putFile(final FileDTO file, final boolean isMatrix) {
		log.trace("Entering putFile for file: [name: {}]", () -> file.getFileName());
		String bucket = setBucket(file);
		String filePath = UUID.randomUUID().toString();
		if (file.getVersion() > 0) {
			filePath += "_" + file.getVersion();
		}
		if (isMatrix) {
			bucket += ".matrix";
		}
		try {
			if (!this.minioClient.bucketExists(bucket)) {
				log.debug("Bucket [name: {}] does not exists - new bucket created", bucket);
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
			BufferedInputStream bais = new BufferedInputStream(new ByteArrayInputStream(file.getContent()));
			this.minioClient.putObject(bucket, filePath, bais, bais.available(), file.getContentType());
			bais.close();
		} catch (Exception e) {
			log.error("ERROR: Saving file [name: {}; filePath: {}; bucket: {}] to Minio wasn't successful Message: {}", file.getFileName(),
			    file.getFilePath(), bucket, e);
			if (e instanceof ConnectException)
				return MinioResult.CONNECTION_ERROR;
			return MinioResult.ERROR;
		}
		log.debug("Transaction for storeFileIntoMinio sucessful and set file.setFilePath(filePath) to {}", () -> file.getFilePath());
		return MinioResult.OK;
	}

	/**
	 * Get a file from the Minio file system. The project identifier and the file path must be given in the FileDTO, because the is is used as bucket
	 * identifier and the unique path as file identifier.
	 * 
	 * @param minioClient
	 * @param file
	 * @return
	 * @throws Exception
	 */
	public MinioResult getFile(final FileDTO file, final boolean isMatrix) {
		log.trace("Entering getFile for file: [name: {}; path: {}]", () -> file.getFileName(), () -> file.getFilePath());
		String bucket = setBucket(file);
		if (isMatrix)
			bucket += ".matrix";
		try {
			if (!this.minioClient.bucketExists(bucket)) {
				log.fatal("FATAL: Bucket [name: {}] not exists in Minio FileSystem - Please check file system consistency!", bucket);
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
				    file.getId(), bucket, file.getFilePath(), e);
				return MinioResult.FILE_NOT_FOUND;
			}
			log.error("ERROR: File [id: {}; bucket: {}; filePath: {}] not loaded from Minio - Message: {}", file.getId(), bucket, file.getFilePath(), e);
		}
		return MinioResult.ERROR;
	}

	/**
	 * @param file
	 * @return
	 */
	private String setBucket(final FileDTO file) {
		StringBuffer sb = new StringBuffer();
		sb.append(this.bucketPrefix);
		sb.append(file.getProjectId());
		if (file.getStudyId() > 0) {
			sb.append(".");
			sb.append(file.getStudyId());
			if (file.getRecordID() > 0) {
				sb.append(".");
				sb.append(file.getRecordID());
			}
		}
		final String bucket = sb.toString();
		return bucket;
	}

	/**
	 * Delete a file from Minio file system. The project identifier and the file path must be given in the FileDTO, because the is is used as bucket
	 * identifier and the unique path as file identifier.
	 * 
	 * @param file
	 * @return
	 */
	public MinioResult deleteFile(final FileDTO file) {
		log.trace("Entering deleteFile  file: [name: {}; path: {}]", () -> file.getFileName(), () -> file.getFilePath());
		final String bucket = setBucket(file);
		try {
			if (!this.minioClient.bucketExists(bucket)) {
				log.fatal("FATAL: Bucket [name: {}] not exists in Minio FileSystem - Please check file system consistency!", () -> bucket);
				return MinioResult.BUCKET_NOT_FOUND;
			}
			this.minioClient.removeObject(bucket, file.getFilePath());
			Iterable<Result<Item>> blist = this.minioClient.listObjects(bucket);
			if (blist == null || !blist.iterator().hasNext()) {
				log.debug("deleteFile: Bucket empty and deleted");
				this.minioClient.removeBucket(bucket);
			}
			return MinioResult.OK;
		} catch (Exception e) {
			log.error("ERROR: File [id: {}; bucket: {}; filePath: {}] not deleted from Minio - Message: {}", () -> file.getId(), () -> bucket,
			    () -> file.getFilePath(), () -> e);
			return MinioResult.ERROR;
		}
	}

	public MinioResult cleanAndRemoveBucket(final long projectId, final long studyId, final long recordId, final boolean isMatrix) {
		FileDTO file = new FileDTO();
		file.setProjectId(projectId);
		file.setStudyId(studyId);
		file.setRecordID(recordId);
		String bucket = setBucket(file);
		if (isMatrix) {
			bucket += ".matrix";
		}
		try {
			if (this.minioClient.bucketExists(bucket)) {
				Iterable<Result<Item>> blist = this.minioClient.listObjects(bucket);
				for (Result<Item> result : blist) {
					Item item = result.get();
					this.minioClient.removeObject(bucket, item.objectName());
				}
				this.minioClient.removeBucket(bucket);
			}
		} catch (Exception e) {
			log.error("ERROR: Bucket [projectId: {}; studyId: {}; recordId: {}] not deleted from Minio - Message: {}", () -> projectId, () -> studyId,
			    () -> recordId, () -> e);
			return MinioResult.ERROR;
		}
		return MinioResult.OK;
	}
}
