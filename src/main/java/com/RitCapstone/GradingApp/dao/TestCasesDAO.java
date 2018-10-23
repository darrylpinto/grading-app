package com.RitCapstone.GradingApp.dao;

import java.io.File;

public interface TestCasesDAO {

	public boolean getTestCaseFiles(String homework, String question, String destLocation);

	public boolean createTestCase(String homework, String question, String testCaseNumber, File testcaseFile);

	public boolean updateTestCase(String homework, String question, String testCaseNumber, File testcaseFile);

}
