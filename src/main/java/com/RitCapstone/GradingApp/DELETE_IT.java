package com.RitCapstone.GradingApp;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public class DELETE_IT {
	

//	private LinkedHashMap<String, String> countryOptions;
	private String[] options;

	
	public DELETE_IT() {
//		this.countryOptions = new LinkedHashMap<>();	
////		this.countryOptions.put("N/A", "1234" );
//		this.countryOptions.put("IN", "India");
//		this.countryOptions.put("FR", "France");
//		this.countryOptions.put("DE", "Germany");
//		this.countryOptions.put("UK", "United Kingdom");
		
		ArrayList<String> options1 = new ArrayList<>();
		options1.add("a00");
		options1.add("a01");
		options1.add("a02");
		options1.add("a04");
		options1.add("a10");
		
		this.options=new String[options1.size()];
		this.options = options1.toArray(this.options);
	
	}

//	public LinkedHashMap<String, String> getCountryOptions() {
//		return countryOptions;
//	}
//
//	public void setCountryOptions(LinkedHashMap<String, String> countryOptions) {
//		this.countryOptions = countryOptions;
//	}

	public String[] getOptions() {
		return options;
	}
	
	
	
}
