package com.RitCapstone.GradingApp;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public class Homework {
	

	private LinkedHashMap<String, String> countryOptions;
	private String[] homeworkOptions;

	private String[] questionOptions;
	
	public Homework() {
		this.countryOptions = new LinkedHashMap<>();	
		this.countryOptions.put(null, "Select country" );
		this.countryOptions.put("IN", "India");
		this.countryOptions.put("FR", "France");
		this.countryOptions.put("DE", "Germany");
		this.countryOptions.put("UK", "United Kingdom");
		
		ArrayList<String> options1 = new ArrayList<>();
		options1.add("a00");
		options1.add("a01");
		options1.add("a02");
		options1.add("a04");
		options1.add("a10");
		
		this.homeworkOptions=new String[options1.size()];
		this.homeworkOptions = options1.toArray(this.homeworkOptions);
		
		
		ArrayList<String> options2 = new ArrayList<>();
		options2.add("b00");
		options2.add("b01");
		options2.add("b02");
		options2.add("b04");
		options2.add("b10");
		
		this.questionOptions =new String[options2.size()];
		this.questionOptions = options2.toArray(this.questionOptions);
	
	}

	public LinkedHashMap<String, String> getCountryOptions() {
		return countryOptions;
	}

	public String[] getHomeworkOptions() {
		return homeworkOptions;
	}

	public String[] getQuestionOptions() {
		return questionOptions;
	}

	
	
	
	
}
