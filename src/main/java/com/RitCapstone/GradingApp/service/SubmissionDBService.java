package com.RitCapstone.GradingApp.service;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.RitCapstone.GradingApp.dao.SubmissionDAO;

@Service
@Transactional
public class SubmissionDBService {

	@Autowired
	SubmissionDAO submissionDAO;

	private static Logger log = Logger.getLogger(SubmissionDBService.class);
	private static String log_prepend = "[SubmissionDBService]";

	public String getSubmission(String homework, String username, String question) {

		log.debug(String.format("%s Get submission: Homework (%s), username (%s), question (%s)", log_prepend, homework,
				username, question));

		return submissionDAO.getSubmissionLocation(homework, username, question);
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
	 */
	public boolean saveSubmission(String homework, String username, String question, String zipPath,
			String zipFileName) {

		// If homework not there then createSubmission
		if (getSubmission(homework, username, question) == null) {

			log.debug(String.format("%s Creating new submission: Homework (%s), username (%s), question (%s)",
					log_prepend, homework, username, question));
			return submissionDAO.createSubmission(homework, username, question, zipPath, zipFileName);
		}
		// If homework already there then updateSubmission
		else {
			log.debug(String.format("%s Updatinng submission: Homework (%s), username (%s), question (%s)", log_prepend,
					homework, username, question));
			return submissionDAO.updateSubmission(homework, username, question, zipPath, zipFileName);
		}

	}

}
