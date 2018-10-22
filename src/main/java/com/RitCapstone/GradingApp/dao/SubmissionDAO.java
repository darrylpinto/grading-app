package com.RitCapstone.GradingApp.dao;

public interface SubmissionDAO {
	public String getSubmissionLocation(String homework, String username, String question);

	public boolean createSubmission(String homework, String username, String question, String zipPath, String zipFile);

	public boolean updateSubmission(String homework, String username, String question, String zipPath,
			String zipFileName);

}
