package com.RitCapstone.GradingApp.controller;

import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.RitCapstone.GradingApp.ProfessorAndGrader;
import com.RitCapstone.GradingApp.validator.AuthenticationValidator;

@Controller
@RequestMapping("/professor")
public class ProfessorController {

	private static Logger log = Logger.getLogger(SubmissionController.class);

	@Autowired
	AuthenticationValidator authValidator;

	/**
	 * This method will trim all the strings received from form data
	 */
	@InitBinder
	public void stringTrimmer(WebDataBinder dataBinder) {

		StringTrimmerEditor trimmer = new StringTrimmerEditor(true);
		// If after trimming the string is empty, it is converted to null {Set by true}

		dataBinder.registerCustomEditor(String.class, trimmer);
	}

	@GetMapping("/authenticate")
	public String showFormForAuthentication(Model model) {
		String jspToDisplay = "professor-authenticate";
		String log_prepend = "[GET /authenticate]";

		if (!model.containsAttribute("professor")) {
			model.addAttribute("professor", new ProfessorAndGrader());
			log.debug(log_prepend + "adding professor to model");
		}

		log.debug(log_prepend + "model: " + model);
		log.debug(log_prepend + "Displaying " + jspToDisplay);
		return jspToDisplay;
	}

	@PostMapping("/authenticate")
	public String authenticateProfessor(@Valid @ModelAttribute("professor") ProfessorAndGrader professor,
			BindingResult bindingResult, RedirectAttributes redirectAttributes) {
		String log_prepend = "[POST /authenticate]";

		authValidator.setUser(AuthenticationValidator.professorString);
		authValidator.validate(professor, bindingResult);
		
		if (bindingResult.hasErrors()) {

			redirectAttributes.addFlashAttribute("professor", professor);
			redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.professor",
					bindingResult);

			log.debug(log_prepend + "Redirecting to /authenticate");
			return "redirect:authenticate";
		}
		log.debug(log_prepend + " Login successful, Redirecting to /authenticate");
		return "redirect:showForm";

	}

	@GetMapping("/showForm")
	public String showForm(Model model) {
		String jspToDisplay = "professor-create-edit-hw";
		String log_prepend = "[GET /showForm]";

//		String jspToDisplay = "TEMP";

//		if (!model.containsAttribute("submisssion")) {
//			model.addAttribute("submisssion", new Submisssion());
//			log.debug(log_prepend + "adding submisaxsxssion to model");
//		}
//
//		if (!model.containsAttribute("hw")) {
//			model.addAttribute("hw", new Homework());
//			log.debug(log_prepend + "adding hw to model");
//		}

		log.debug(log_prepend + "model: " + model);
		log.debug(log_prepend + "Displaying " + jspToDisplay);

		return jspToDisplay;
	}

}
