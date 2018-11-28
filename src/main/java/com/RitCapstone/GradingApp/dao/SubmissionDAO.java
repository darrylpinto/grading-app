package com.RitCapstone.GradingApp.dao;

import java.util.List;

public interface SubmissionDAO {
	public String getSubmissionLocation(String homework, String username, String question);

	public boolean createSubmission(String homework, String username, String question, String zipPath, String zipFile);

	public boolean updateSubmission(String homework, String username, String question, String zipPath,
			String zipFileName);

	public boolean addOutputAndTestCaseResults(String homework, String username, String question, List<String> codeOutput,
			List<String> codeStatus);
	
	public boolean updateOutputAndTestCaseResults(String homework, String username, String question, List<String> codeOutput,
			List<String> codeStatus);

}
