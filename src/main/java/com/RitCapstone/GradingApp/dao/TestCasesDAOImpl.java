package com.RitCapstone.GradingApp.dao;

import java.io.File;
import java.util.List;

import org.springframework.stereotype.Repository;

@Repository
public class TestCasesDAOImpl implements TestCasesDAO {

	@Override
	public List<File> getTestCases(String problem_id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveTestCases(String problem_id, List testCases) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateTestCases(String problem_id, List testCases) {
		// TODO Auto-generated method stub

	}

}
