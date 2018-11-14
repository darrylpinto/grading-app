package com.RitCapstone.GradingApp.service;

import java.io.File;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.RitCapstone.GradingApp.dao.TestCasesDAO;

@Service
public class TestCaseDBService {

	@Autowired
	TestCasesDAO testCasesDAO;

	private static Logger log = Logger.getLogger(TestCaseDBService.class);
	private static String log_prepend = "[TestCaseDBService]";

	public boolean getTestCases(String homework, String question, String destLocation) {

		log.debug(String.format("%s Get TestCase: Homework (%s), question (%s)", log_prepend, homework, question));
		return testCasesDAO.getTestCaseFilesToLocal(homework, question, destLocation);

	}

	public boolean deleteTestCases(String homework, String question) {

		try {
			return testCasesDAO.deleteTestCases(homework, question);
		} catch (Exception e) {
			log.error(log_prepend + " Exception occurred in deleteTestCases:" + e.getMessage());
			return false;
		}

	}

	public boolean saveTestCase(String homework, String question, String testCaseNumber, File testcaseInput,
			File testcaseOutput) {

		log.debug(String.format("%s Save TestCase: Homework (%s), question (%s), testCaseNumber (%s)", log_prepend,
				homework, question, testCaseNumber));

		try {
			if (testCasesDAO.testCaseExists(homework, question, testCaseNumber)) {
				return testCasesDAO.updateTestCase(homework, question, testCaseNumber, testcaseInput, testcaseOutput);
			} else {
				return testCasesDAO.createTestCase(homework, question, testCaseNumber, testcaseInput, testcaseOutput);
			}
		} catch (Exception e) {
			log.error(log_prepend + " Exception occurred in saveTestCases:" + e.getMessage());
			return false;
		}

	}

}
