package com.RitCapstone.GradingApp.dao;

import java.io.File;
import java.util.List;

public interface TestCasesDAO {
	
	public List<File> getTestCases(String problem_id);
	
	public void saveTestCases(String problem_id, List<File> testCases);
	
	public void updateTestCases(String problem_id, List<File> testCases);
	

}
