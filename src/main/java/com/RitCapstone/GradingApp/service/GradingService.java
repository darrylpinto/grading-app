package com.RitCapstone.GradingApp.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

@Service
public class GradingService {

	private static final String DIRECTORY_TO_SAVE = "uploads_from_springMVC";
	private static final String chosen_dir = System.getProperty("user.dir") + File.separator + DIRECTORY_TO_SAVE
			+ File.separator;

	/**
	 * Method to zip the submission
	 * 
	 * @param username     RIT username
	 * @param homework     Homework number
	 * @param question     Question number
	 * @param codeFiles    code files uploaded
	 * @param writeupFiles writeup files uploaded
	 */
	public void zip(String username, String homework, String question, CommonsMultipartFile[] codeFiles,
			CommonsMultipartFile[] writeupFiles) {

		// TODO Auto-generated method stub

	}

	/**
	 * Method to process the files, get file names and save the files on local
	 * machine
	 * 
	 * @param commonsMultipartFiles files that are uploaded
	 * @return list of filenames
	 */
	public List<String> processAndSaveFiles(CommonsMultipartFile[] commonsMultipartFiles) {
		List<String> fileNames = new ArrayList<String>();

		for (CommonsMultipartFile multipartFile : commonsMultipartFiles) {
			try {

				// To create the directory if it is not there
				new File(chosen_dir + ".tmp").mkdirs();

				FileCopyUtils.copy(multipartFile.getBytes(),
						new File(chosen_dir + multipartFile.getOriginalFilename()));

				fileNames.add(multipartFile.getOriginalFilename());

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return fileNames;
	}

}
