package com.RitCapstone.GradingApp.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.RitCapstone.GradingApp.dao.GradeHomeworkDAO;

@Service
public class GradeHomeworkService {

	@Autowired
	GradeHomeworkDAO gradeHomeworkDAO;

	public List<String> getListOfQuestions(String homework) {
		return gradeHomeworkDAO.getListOfQuestions(homework);
	}

	public List<String> getListOfStudents(String homework) {
		return gradeHomeworkDAO.getListOfStudents(homework);
	}

	public List<String> getListOfStudentsForQuestion(String homework, String question) {
		return gradeHomeworkDAO.getListOfStudentsForQuestion(homework, question);
	}

	public List<String> getListOfQuestionsForStudent(String homework, String username) {
		return gradeHomeworkDAO.getListOfQuestionsForStudent(homework, username);
	}
}
