package com.RitCapstone.GradingApp.service;

import java.io.File;

public class SubmissionDBService {

	public File getSubmission(String homework, String username, String question) {
		return null;
	}

	/**
	 * Method to create a new submission. This happens when the student submits the
	 * homework for the first time
	 * 
	 * If the submission is the first one for the homework, we create a new
	 * collection
	 * 
	 * @param homework homework number of the solution
	 * @param username username of student submitting the solution
	 * @param question question number of the solution
	 * @param zipFile  The file that has to be saved to MongoDB
	 */
	public void createSubmission(String homework, String username, String question, File zipFile) {

	}

	public void updateSubmission(String homework, String username, String question, File zipFile) {

	}
}
