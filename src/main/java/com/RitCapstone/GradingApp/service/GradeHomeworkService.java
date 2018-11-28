package com.RitCapstone.GradingApp.service;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.RitCapstone.GradingApp.dao.GradeHomeworkDAO;

@Service
public class GradeHomeworkService {

	@Autowired
	GradeHomeworkDAO gradeHomeworkDAO;

	
	private List<String> sortList(List<String> list){
		Collections.sort(list);
		return list;
	}
	
	public List<String> getListOfQuestions(String homework) {
		return sortList(gradeHomeworkDAO.getListOfQuestions(homework));
	}

	public List<String> getListOfStudents(String homework) {
		return sortList(gradeHomeworkDAO.getListOfStudents(homework));
	}

	public List<String> getListOfStudentsForQuestion(String homework, String question) {
		return sortList(gradeHomeworkDAO.getListOfStudentsForQuestion(homework, question));
	}

	public List<String> getListOfQuestionsForStudent(String homework, String username) {
		return sortList(gradeHomeworkDAO.getListOfQuestionsForStudent(homework, username));
	}
}
