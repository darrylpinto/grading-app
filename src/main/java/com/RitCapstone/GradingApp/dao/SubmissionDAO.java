package com.RitCapstone.GradingApp.dao;

import java.io.File;

public interface SubmissionDAO {
	public File getSubmission(String homework, String username, String question);

	public boolean createSubmission(String homework, String username, String question, String zipPath, String zipFile);

	public boolean updateSubmission(String homework, String username, String question, String zipPath,
			String zipFileName);

}
