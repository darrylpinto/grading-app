package com.RitCapstone.GradingApp.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.io.FileExistsException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

@Service
public class FileSaverService {

	private static Logger log = Logger.getLogger(FileSaverService.class);

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
				log.debug(String.format("Zipping into %s: %s", zipName, file.getName()));

				ZipEntry zipEntry = new ZipEntry(file.getName());
				out.putNextEntry(zipEntry);

				byte[] data = IOUtils.toByteArray(new FileInputStream(file));
				out.write(data, 0, data.length);
				out.closeEntry();

			}
		}
		out.close();

		log.info(String.format("Moving %s to %s", zipFile.getAbsolutePath(), zipFileDest));

		// Move the zip folder out of current folder to zipFileDest
		try {
			FileUtils.moveFileToDirectory(zipFile, new File(zipFileDest), false);
			// Throws an exception if zipFile already exists,
			// In that case we delete the zip-file [It is the zip file from previous
			// submission]

		} catch (FileExistsException e) {
			log.info(e.getMessage());
			FileUtils.deleteQuietly(new File(zipFileDest + zipName));
			log.info("Deleted stale zip file");
			FileUtils.moveFileToDirectory(zipFile, new File(zipFileDest), false);
			// false will avoid creating a dir if dir does not exist

		}

		// Delete the question folder as it has been zipped
		FileUtils.deleteDirectory(new File(filesToZipPath));
		log.info("Deleted " + filesToZipPath);

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
				log.warn(destinationDir.getAbsolutePath() + " exists!! Deleting it");
				FileUtils.deleteDirectory(destinationDir);

			} catch (IOException e) {
				log.error("Error deleting " + destinationDir.getName());
				e.printStackTrace();
			}

		}

		// To create the directory if it is not there
		log.debug("Creating " + destinationDir.getAbsolutePath());
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
			e.printStackTrace();
		}
		return zipFileDest;

	}

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

				log.debug("Unzipping to " + newFile.getAbsolutePath());
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
			log.error(e.getMessage());
			return false;
		}

	}

}
