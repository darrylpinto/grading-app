package com.RitCapstone.GradingApp;

import java.util.Arrays;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

public class Question {

	@NotNull(message = "Question number is required")
	@Min(value= 1, message = "Question number should get greater than zero")	
	private Integer questionNumber;

	private String questionDescription;

	@NotBlank(message = "Problem name is required")
	private String problemName;

	// TODO hidden test cases

	private CommonsMultipartFile[] testCases;

	private CommonsMultipartFile[] outputTestCases;

	public Question() {
	}

	@Override
	public String toString() {
		return "Question [questionNumber=" + questionNumber + ", questionDescription=" + questionDescription
				+ ", problemName=" + problemName + ", testCases=" + Arrays.toString(testCases) + ", outputTestCases="
				+ Arrays.toString(outputTestCases) + "]";
	}

	public Integer getQuestionNumber() {
		return questionNumber;
	}

	public void setQuestionNumber(Integer questionNumber) {
		this.questionNumber = questionNumber;
	}

	public String getProblemName() {
		return problemName;
	}

	public void setProblemName(String problemName) {
		this.problemName = problemName;
	}

	public String getQuestionDescription() {
		return questionDescription;
	}

	public void setQuestionDescription(String questionDescription) {
		this.questionDescription = questionDescription;
	}

	public CommonsMultipartFile[] getTestCases() {
		return testCases;
	}

	public void setTestCases(CommonsMultipartFile[] testCases) {
		this.testCases = testCases;
	}

	public CommonsMultipartFile[] getOutputTestCases() {
		return outputTestCases;
	}

	public void setOutputTestCases(CommonsMultipartFile[] outputTestCases) {
		this.outputTestCases = outputTestCases;
	}

}
