package com.RitCapstone.GradingApp.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.io.FileExistsException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.RitCapstone.GradingApp.mongo.MongoFactory;

@Service
public class FileService {

	private static Logger log = Logger.getLogger(FileService.class);
	private static String log_prepend = "[FileService]";

	private static final String fileRestrictionJSON = "fileRestrictions.json";

	private static final String DIRECTORY_TO_SAVE = "uploads_from_springMVC";
	private static final String chosen_dir = System.getProperty("user.dir") + File.separator + DIRECTORY_TO_SAVE
			+ File.separator;

	/**
	 * Method to zip the submission
	 * 
	 * @param filesToZipPath The path to files that are to be zipped
	 * @param zipName        Name of the zip file
	 * @param zipFileDest    Destination of the zip file, After creating the zip
	 *                       file, we move it
	 * @throws IOException
	 */
	private void zip(String filesToZipPath, String zipName, String zipFileDest) throws IOException {

		File dir = new File(filesToZipPath);
		File[] listOfFiles = dir.listFiles();

		File zipFile = new File(filesToZipPath + zipName);

		// Stream to save to zip file
		ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFile));

		// saving all files to zip
		for (File file : listOfFiles) {
			if (file.isFile()) {
				log.debug(String.format("%s Zipping into %s: %s", log_prepend, zipName, file.getName()));

				ZipEntry zipEntry = new ZipEntry(file.getName());
				out.putNextEntry(zipEntry);

				byte[] data = IOUtils.toByteArray(new FileInputStream(file));
				out.write(data, 0, data.length);
				out.closeEntry();

			}
		}
		out.close();

		log.info(String.format("%s Moving %s to %s", log_prepend, zipFile.getAbsolutePath(), zipFileDest));

		// Move the zip folder out of current folder to zipFileDest
		try {
			FileUtils.moveFileToDirectory(zipFile, new File(zipFileDest), false);
			// Throws an exception if zipFile already exists,
			// In that case we delete the zip-file [It is the zip file from previous
			// submission]

		} catch (FileExistsException e) {
			log.info(log_prepend + " " + e.getMessage());
			FileUtils.deleteQuietly(new File(zipFileDest + zipName));
			log.info(log_prepend + " " + "Deleted stale zip file");
			FileUtils.moveFileToDirectory(zipFile, new File(zipFileDest), false);
			// false will avoid creating a dir if dir does not exist

		}

		// Delete the question folder as it has been zipped
		FileUtils.deleteDirectory(new File(filesToZipPath));
		log.info(log_prepend + " Deleted " + filesToZipPath);

	}

	/**
	 * Method to process the files, get file names and save the files on local
	 * machine
	 * 
	 * The file hierarchy is similar to try system. [It is present in:
	 * grading-app/src/main/resources/]
	 * 
	 * @param username     RIT username
	 * @param homework     Homework number
	 * @param question     Question number
	 * @param codeFiles    code files that are uploaded
	 * @param writeupFiles writeup files that are uploaded
	 * @return path of the zipped file
	 */
	public String saveFiles(String homework, String username, String question, CommonsMultipartFile[] codeFiles,
			CommonsMultipartFile[] writeupFiles) {

		String currentPath = chosen_dir + homework + File.separator + username + File.separator + question
				+ File.separator;

		File destinationDir = new File(currentPath);

		// When the user uploads and the question directory already exists, delete the
		// directory
		if (destinationDir.exists()) {
			try {

				// Should not have entered here as we delete the folder after zipping
				log.warn(log_prepend + " " + destinationDir.getAbsolutePath() + " exists!! Deleting it");
				FileUtils.deleteDirectory(destinationDir);

			} catch (IOException e) {
				log.error(log_prepend + " Error deleting " + destinationDir.getName());
				e.printStackTrace();
			}

		}

		// To create the directory if it is not there
		log.debug(log_prepend + " Creating " + destinationDir.getAbsolutePath());
		new File(currentPath + ".tmp").mkdirs();

		CommonsMultipartFile[] files = (CommonsMultipartFile[]) ArrayUtils.addAll(codeFiles, writeupFiles);
		for (CommonsMultipartFile file : files) {
			try {

				// Copy the uploaded file to local machine
				FileCopyUtils.copy(file.getBytes(), new File(currentPath + file.getOriginalFilename()));

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		String zipFileDest = chosen_dir + homework + File.separator + username + File.separator;
		try {
			zip(currentPath, question + ".zip", zipFileDest);
		} catch (IOException e) {
			log.error(log_prepend + "" + e.getMessage());
		}
		return zipFileDest;

	}

	/**
	 * Method to unzip a zip file an d save the files to destDir
	 * 
	 * @param zipFile zip file to unzip
	 * @param destDir the directory where files will be unzipped
	 * @return boolean indicating success/failure of unzip method
	 */
	public boolean unzip(String zipFile, String destDir) {

		File dir = new File(destDir);

		if (!dir.exists())
			dir.mkdirs();

		try {

			ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFile));
			ZipEntry zipEntry = zipInputStream.getNextEntry();

			while (zipEntry != null) {

				String fileName = zipEntry.getName();
				File newFile = new File(destDir + File.separator + fileName);

				log.debug(log_prepend + " Unzipping to " + newFile.getAbsolutePath());
				FileOutputStream fileOutputStream = new FileOutputStream(newFile);

				int len;
				byte[] buffer = new byte[1024];

				while ((len = zipInputStream.read(buffer)) > 0) {
					fileOutputStream.write(buffer, 0, len);
				}

				fileOutputStream.close();
				zipInputStream.closeEntry();

				zipEntry = zipInputStream.getNextEntry();
			}

			zipInputStream.closeEntry();
			zipInputStream.close();
			return true;

		} catch (IOException e) {
			log.error(log_prepend + "" + e.getMessage());
			return false;
		}

	}

	/**
	 * Method to delete non Code files from the directory
	 * 
	 * @param dirName Directory from where the non-code files will be deleted
	 * @return boolean indicating success/failure of deleteNonCodeFiles method
	 */
	public boolean deleteNonCodeFiles(String dirName) {

		File[] listOfFiles = getFiles(dirName);
		if (listOfFiles == null) {
			return false; // as listOfFiles is not a directory
		}
		// get the code extensions from fileRestrictions.json
		ClassLoader classLoader = MongoFactory.class.getClassLoader();
		File jsonFile = new File(classLoader.getResource(fileRestrictionJSON).getFile());

		JSONParser parser = new JSONParser();

		HashSet<String> codeExtensionSet = new HashSet<>();
		try {
			JSONObject jsonObject = (JSONObject) parser.parse(new FileReader(jsonFile));
			JSONArray _codeExt = (JSONArray) jsonObject.get("codeExtension");

			Iterator<?> it = _codeExt.iterator();
			while (it.hasNext())
				codeExtensionSet.add(it.next().toString());

		} catch (Exception e) {
			log.error(log_prepend + " Error while reading jsonFile in deleteFile: " + e.getMessage());
			return false;
		}

		try {
			for (File file : listOfFiles) {

				String filename = file.getName();
				String[] _parts = filename.trim().split("\\.");
				String extension = "." + _parts[_parts.length - 1];

				if (!codeExtensionSet.contains(extension)) {
					file.delete();
					log.info(log_prepend + " File deleted: " + filename);
				}
			}
			return true;

		} catch (Exception e) {
			log.error(log_prepend + " Error in deleteFile: " + e.getMessage());
			return false;
		}

	}

	public File[] getFiles(String dirName) {
		File dir = new File(dirName);
		if (!dir.isDirectory()) {
			log.error(log_prepend + " " + dir.getName() + " is not a directory");
			return null;
		}

		return dir.listFiles();
	}

	public String getExtension(File file) {
		
		String filename = file.getName();
		String[] _parts = filename.trim().split("\\.");
		String extension = "." + _parts[_parts.length - 1];
		return extension;

	}

}
