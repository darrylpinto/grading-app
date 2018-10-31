package com.RitCapstone.GradingApp;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

public class ProfessorAndGrader {

	@NotBlank(message = "username is required")
	@Email(message = "not an email id")
	private String username;

	@NotBlank(message = "password is required")
	private String password;

	private String incorrectCredentials;

	public ProfessorAndGrader() {
		this.username = "";
		this.password = "";
		this.incorrectCredentials = "";
	}

	@Override
	public String toString() {
		return "ProfessorAndGrader [username=" + username + ", password=" + password + ", incorrectCred="
				+ incorrectCredentials + "]";
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getIncorrectCredentials() {
		return incorrectCredentials;
	}

	public void setIncorrectCredentials(String incorrectCredentials) {
		this.incorrectCredentials = incorrectCredentials;
	}

}
