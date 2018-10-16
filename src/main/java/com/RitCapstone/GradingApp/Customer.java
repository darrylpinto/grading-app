package com.RitCapstone.GradingApp;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

//VALIDATION OF FORMS using Bean Validation API
public class Customer {

	private String firstName;

	@NotNull(message = "is required")
	@Size(min = 1, message = "Size should be at least 1 character")
	private String lastName;

	@NotNull
	@Min(value = 0, message = "should be greater than 0")
	@Max(value = 10, message = "Should be less than 10")
	private Integer freePasses;

	public Customer() {

	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Integer getFreePasses() {
		return freePasses;
	}

	public void setFreePasses(Integer freePasses) {
		this.freePasses = freePasses;
	}

}
