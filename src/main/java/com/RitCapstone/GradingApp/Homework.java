package com.RitCapstone.GradingApp;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class Homework {
	

	private LinkedHashMap<String, String> countryOptions;
	private String[] homeworkOptions;

	private String[] questionOptions;
	
	private String[] languageOptions;
	
	public Homework() {
		this.countryOptions = new LinkedHashMap<>();	
		this.countryOptions.put(null, "Select country" );
		this.countryOptions.put("IN", "India");
		this.countryOptions.put("FR", "France");
		this.countryOptions.put("DE", "Germany");
		this.countryOptions.put("UK", "United Kingdom");
		
		ArrayList<String> options1 = new ArrayList<>();
		options1.add("hw0");
		options1.add("hw1");
		options1.add("hw2");
		options1.add("hw3");
		options1.add("hw99");
		
		this.homeworkOptions=new String[options1.size()];
		this.homeworkOptions = options1.toArray(this.homeworkOptions);
		
		
		ArrayList<String> options2 = new ArrayList<>();
		options2.add("0");
		options2.add("1");
		options2.add("2");
		options2.add("3");
		options2.add("4");
		
		this.questionOptions =new String[options2.size()];
		this.questionOptions = options2.toArray(this.questionOptions);
	
		ArrayList<String> options3 = new ArrayList<>();
		options3.add(".java");
		options3.add(".cpp");
		
		this.languageOptions = new String[options3.size()];
		this.languageOptions = options3.toArray(this.languageOptions);
		
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

	public String[] getLanguageOptions() {
		return languageOptions;
	}
}
