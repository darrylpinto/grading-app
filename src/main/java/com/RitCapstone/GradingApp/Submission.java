package com.RitCapstone.GradingApp;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class Submission {

	@NotBlank(message="RIT username is required")
	private String username;
	
	@NotNull(message="homework is required")
	private String homework;
	
	@NotNull(message="question is required")
	private String question;
	
	private FileUploader codeSubmission;
	
	private FileUploader writeupSubmission;
	
	public Submission() {
		this.username = null;
		this.homework = null;
		this.question = null;
		this.codeSubmission = null;
		this.writeupSubmission = null;
	}
		

//	public Submission(@NotNull(message = "RIT username is required") String username, String homework, String question,
//			FileUploader codeSubmission, FileUploader writeupSubmission) {
//		super();
//		this.username = username;
//		this.homework = homework;
//		this.question = question;
//		this.codeSubmission = codeSubmission;
//		this.writeupSubmission = writeupSubmission;
//	}

	@Override
	public String toString() {
		return "Submission [username=" + username + ", homework=" + homework + ", question=" + question + "]";
	}


	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getHomework() {
		return homework;
	}

	public void setHomework(String homework) {
		this.homework = homework;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public FileUploader getCodeSubmission() {
		return codeSubmission;
	}

	public void setCodeSubmission(FileUploader codeSubmission) {
		this.codeSubmission = codeSubmission;
	}

	public FileUploader getWriteupSubmission() {
		return writeupSubmission;
	}

	public void setWriteupSubmission(FileUploader writeupSubmission) {
		this.writeupSubmission = writeupSubmission;
	}
	
	
	
}

