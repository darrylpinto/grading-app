package com.RitCapstone.GradingApp;

import java.util.ArrayList;
import java.util.Date;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


public class Homework {

	@NotBlank(message = "Homework name is required")
	private String id;

	@Min(value = 1,message = "Number of questions should be greater than zero")
	private Integer numberOfQuestions;

	@NotNull(message = "Due date is required")
	private Date dueDate;
	
	private Question[] questions;
	
	private String[] homeworkOptions;

	private String[] questionOptions;

	private String[] languageOptions;

	public Homework() {
//		this.countryOptions = new LinkedHashMap<>();	
//		this.countryOptions.put(null, "Select country" );
//		this.countryOptions.put("IN", "India");
//		this.countryOptions.put("FR", "France");
//		this.countryOptions.put("DE", "Germany");
//		this.countryOptions.put("UK", "United Kingdom");

		this.id = "";
		this.numberOfQuestions = 0;
		
		ArrayList<String> options1 = new ArrayList<>();
		options1.add("hw0");
		options1.add("hw1");
		options1.add("hw2");
		options1.add("hw3");
		options1.add("hw99");

		this.homeworkOptions = new String[options1.size()];
		this.homeworkOptions = options1.toArray(this.homeworkOptions);

		ArrayList<String> options2 = new ArrayList<>();
		options2.add("0");
		options2.add("1");
		options2.add("2");
		options2.add("3");
		options2.add("4");

		this.questionOptions = new String[options2.size()];
		this.questionOptions = options2.toArray(this.questionOptions);

		ArrayList<String> options3 = new ArrayList<>();
		options3.add(".java");
		options3.add(".cpp");

		this.languageOptions = new String[options3.size()];
		this.languageOptions = options3.toArray(this.languageOptions);

	}

	
	@Override
	public String toString() {
		return "Homework [id=" + id + ", numberOfQuestions=" + numberOfQuestions + ", dueDate=" + dueDate + "]";
	}


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Integer getNumberOfQuestions() {
		return numberOfQuestions;
	}

	public void setNumberOfQuestions(Integer numberOfQuestions) {
		this.numberOfQuestions = numberOfQuestions;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	public Question[] getQuestions() {
		return questions;
	}


	public void setQuestions(Question[] questions) {
		this.questions = questions;
	}


	public String[] getHomeworkOptions() {
		return homeworkOptions;
	}

	public String[] getQuestionOptions() {
		return questionOptions;
	}

	public String[] getLanguageOptions() {
		return languageOptions;
	}
}
