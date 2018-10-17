package com.RitCapstone.GradingApp.controller;

import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.RitCapstone.GradingApp.Customer;
import com.RitCapstone.GradingApp.DELETE_IT;
import com.RitCapstone.GradingApp.Submission;

@Controller
@RequestMapping("/submission")
public class SubmissionController {

	Submission submission;

	private static Logger log = Logger.getLogger(SubmissionController.class);

	// add initBinder to resolve whitespace problem
	@InitBinder
	public void stringTrimmer(WebDataBinder dataBinder) {

		StringTrimmerEditor trimmer = new StringTrimmerEditor(true);
		dataBinder.registerCustomEditor(String.class, trimmer);
	}

	@RequestMapping(value = "/showForm", method = RequestMethod.GET)
	public String showForm(Model model) {

		String jspToDisplay = "student-submission";

		if (!model.containsAttribute("submission")) {
			model.addAttribute("submission", new Submission());
			log.debug("adding submission to model");
		}

		if (!model.containsAttribute("delete")) {
			model.addAttribute("delete", new DELETE_IT());
			log.debug("adding delete to model");
		}

		log.debug("model in showForm: " + model);
		log.debug("[/showForm]redirecting to " + jspToDisplay);

		return jspToDisplay;
	}

	@RequestMapping(value = "/showForm", method = RequestMethod.POST)
	public String validateShowForm(@Valid @ModelAttribute("submission") Submission submission, BindingResult bindingResult,
			RedirectAttributes redirectAttributes) {
	
		if (bindingResult.hasErrors()) {
			redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.submission",
					bindingResult);
			redirectAttributes.addFlashAttribute("submission", submission);
			log.debug("[{POST} of /showForm] Redirecting to showForm");
			return "redirect:showForm";
		}
		log.debug("[{POST} of /showForm] Redirecting to showForm2");
		return "redirect:showForm2";
	}

	@RequestMapping("/showForm2")
	public String showRemainingForm() {

		String jspToDisplay = "";

			jspToDisplay = "student-submission-remaining";
			log.debug("[/showForm2]redirecting to " + jspToDisplay);
			return jspToDisplay;

	}

}