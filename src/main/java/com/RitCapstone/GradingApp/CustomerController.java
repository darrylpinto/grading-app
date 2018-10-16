package com.RitCapstone.GradingApp;

import javax.validation.Valid;

import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/customer")
public class CustomerController {

	// add initBinder to resolve whitespace problem

	@InitBinder
	public void stringTrimmer(WebDataBinder dataBinder) {

		StringTrimmerEditor trimmer = new StringTrimmerEditor(true);
		dataBinder.registerCustomEditor(String.class, trimmer);
	}

	@RequestMapping("/showForm")
	public String showForm(Model model) {
		System.out.println("in showForm CUSTOMER");
		model.addAttribute("customer", new Customer());

		return "customer-form";
	}

	@RequestMapping("/processForm")
	public String processForm(@Valid @ModelAttribute("customer") Customer customer, BindingResult bindingResult) {
		System.out.println("*: "+bindingResult);
		if (bindingResult.hasErrors()) {
			System.out.println(bindingResult);
			System.out.println("Has errors!!");

			return "customer-form";

		} else {
			System.out.printf("Proceed to confirmation %s %s\n", customer.getFirstName(), customer.getLastName());
			return "customer-confirmation";
		}

	}

}
