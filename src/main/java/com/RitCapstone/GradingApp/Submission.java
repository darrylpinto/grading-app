package com.RitCapstone.GradingApp;

import java.util.Arrays;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

public class Submission {

	@NotBlank(message = "RIT username is required")
	private String username;

	@NotNull(message = "homework is required")
	private String homework;

	@NotNull(message = "question is required")
	private String question;

	private CommonsMultipartFile[] codeFiles;

	private CommonsMultipartFile[] writeupFiles;

	public Submission() {
		this.username = null;
		this.homework = null;
		this.question = null;
	}

	@Override
	public String toString() {
		try {
			return "Submission [username=" + username + ", homework=" + homework + ", question=" + question
					+ ", codeFiles=" + Arrays.toString(codeFiles) + ", writeupFiles=" + Arrays.toString(writeupFiles)
					+ "]";
		} catch (NullPointerException e) {
			return "Submission [username=" + username + ", homework=" + homework + ", question=" + question + "]";
		}
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

	public CommonsMultipartFile[] getCodeFiles() {
		return codeFiles;
	}

	public void setCodeFiles(CommonsMultipartFile[] codeFiles) {
		this.codeFiles = codeFiles;
	}

	public CommonsMultipartFile[] getWriteupFiles() {
		return writeupFiles;
	}

	public void setWriteupFiles(CommonsMultipartFile[] writeupFiles) {
		this.writeupFiles = writeupFiles;
	}

}
