package com.RitCapstone.GradingApp;

import javax.validation.constraints.NotNull;

public class GradeHomework {
	
	@NotNull(message = "homework is required")
	private String homework;
	
	public GradeHomework() {
	}

	public String getHomework() {
		return homework;
	}

	public void setHomework(String homework) {
		this.homework = homework;
	}
	
	
}
