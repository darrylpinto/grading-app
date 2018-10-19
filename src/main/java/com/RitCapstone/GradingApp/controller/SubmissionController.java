package com.RitCapstone.GradingApp.controller;

import java.util.List;

import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.RitCapstone.GradingApp.FileValidator;
import com.RitCapstone.GradingApp.Homework;
import com.RitCapstone.GradingApp.Submission;
import com.RitCapstone.GradingApp.service.GradingService;

@Controller
@RequestMapping("/submission")
@SessionAttributes("submission")
public class SubmissionController {

	Submission submission;

	@Autowired
	FileValidator fileValidator;

	@Autowired
	GradingService gradingService;

	private static Logger log = Logger.getLogger(SubmissionController.class);

	/**
	 * This method will trim all the strings
	 */
	@InitBinder
	public void stringTrimmer(WebDataBinder dataBinder) {

		StringTrimmerEditor trimmer = new StringTrimmerEditor(true);
		// If after trimming the string is empty, it is converted to null {Set by true}

		dataBinder.registerCustomEditor(String.class, trimmer);
	}

	/**
	 * This method displays the initial form
	 * 
	 * @param model Initially model will be empty.If there are validation problems,
	 *              the model will consist of submission, bindingResult. This is due
	 *              to RedirectAttributes
	 * @return .jsp file to display
	 */
	@GetMapping("/showForm")
	public String showForm(Model model) {
		String log_prepend = "[GET /showForm]";
		String jspToDisplay = "student-submission";

		if (!model.containsAttribute("submission")) {
			model.addAttribute("submission", new Submission());
			log.debug(log_prepend + "adding submission to model");
		}

		if (!model.containsAttribute("hw")) {
			model.addAttribute("hw", new Homework());
			log.debug(log_prepend + "adding hw to model");
		}

		log.debug(log_prepend + "model: " + model);
		log.debug(log_prepend + "Displaying " + jspToDisplay);

		return jspToDisplay;
	}

	/**
	 * This method handles the POST method for submission
	 * 
	 * @param submission         Model Attribute from Submission class
	 * @param bindingResult      Shows if there are errors
	 * @param redirectAttributes Adds attributes if there are validation errors
	 * @return page to be displayed, if there are validation errors, showForm is
	 *         displayed else showForm2 is displayed
	 */
	@PostMapping("/showForm")
	public String validateShowForm(@Valid @ModelAttribute("submission") Submission submission,
			BindingResult bindingResult, RedirectAttributes redirectAttributes) {

		String log_prepend = "[POST /showForm]";

		// we want to ignore Errors on question field
		List<FieldError> homework_err = bindingResult.getFieldErrors("homework");
		List<FieldError> username_err = bindingResult.getFieldErrors("username");

		if (homework_err.size() + username_err.size() > 0) {
			redirectAttributes.addFlashAttribute("submission", submission);
			redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.submission",
					bindingResult);

			log.debug(log_prepend + "Redirecting to showForm");
			return "redirect:showForm";
		}

		log.debug(log_prepend + "Redirecting to showForm2");
		return "redirect:showForm2";
	}

	/**
	 * Method to handle remaining form, This form consists of question to select
	 * 
	 * @return .jsp file to display
	 */
	@GetMapping("/showForm2")
	public String showRemainingForm(@SessionAttribute("submission") Submission submission, Model model) {

		String log_prepend = "[GET /showForm2]";
		if (!model.containsAttribute("hw")) {
			model.addAttribute("hw", new Homework());
			log.debug(log_prepend + "adding hw to model");
		}
		log.debug(log_prepend + "Session:" + submission);
		log.debug(log_prepend + "Model:" + model);
		String jspToDisplay = "student-submission-remaining";
		log.debug(log_prepend + "Displaying " + jspToDisplay);
		return jspToDisplay;

	}

	/**
	 * This method handles the POST method for submission (Part 2)
	 * 
	 * @param submission         Model Attribute from Submission class
	 * @param bindingResult      Shows if there are errors
	 * @param redirectAttributes Adds attributes if there are validation errors
	 * @return page to be displayed, if there are validation errors, showForm2 is
	 *         displayed else showConfirmation is displayed
	 */
	@PostMapping("/showForm2")
	public String validateRemainingForm(@Valid @ModelAttribute("submission") Submission submission,
			BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {

		String log_prepend = "[POST /showForm2]";
		fileValidator.validate(submission, bindingResult);
		log.debug(log_prepend + "Model:" + model);
		log.debug(log_prepend + "Submission:" + submission);
		if (bindingResult.hasErrors()) {
			redirectAttributes.addFlashAttribute("submission", submission);
			redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.submission",
					bindingResult);

			log.debug(log_prepend + "Redirecting to showForm2");
			return "redirect:showForm2";
		}

		log.debug(log_prepend + "Redirecting to showConfirmation");
		return "redirect:showConfirmation";
	}

	/**
	 * Method to display confirmation and list of files submitted Also, files are
	 * saved on the machine
	 * 
	 * @param submission Session Attribute
	 * @return jsp file to display
	 */
	@GetMapping("/showConfirmation")
	public String showConfirmation(@SessionAttribute("submission") Submission submission, Model model) {

		String log_prepend = "[GET /showConfirmation]";
		List<String> codeFiles = gradingService.processAndSaveFiles(submission.getCodeFiles());
		List<String> writeupFiles = gradingService.processAndSaveFiles(submission.getWriteupFiles());

		model.addAttribute("codeFileNames", codeFiles);
		model.addAttribute("writeupFileNames", writeupFiles);

		gradingService.zip(submission.getUsername(), submission.getHomework(), submission.getQuestion(),
				submission.getCodeFiles(), submission.getWriteupFiles());

		log.debug(log_prepend + "Displaying: student-confirmation");
		return "student-confirmation";
	}

	/**
	 * This method redirects to showForm2. Also reinitializes question, codeFiles,
	 * and writeupFiles
	 *
	 * @param submission Session Attribute
	 * @return redirected to showForm2
	 */
	@PostMapping("/showConfirmation")
	public String showFormAgain(@SessionAttribute("submission") Submission submission) {
		String log_prepend = "[POST /showConfirmation]";
		submission.setQuestion(null);
		submission.setCodeFiles(null);
		submission.setWriteupFiles(null);
		log.debug(log_prepend + "Submission:" + submission);
		return "redirect:showForm2";
	}

	/**
	 * This method reinitializes all the fields in submission session attribute
	 * 
	 * It should redirect to validate page
	 * @param submission Session Attribute
	 * @return redirected to showForm
	 */
	@PostMapping("/redirectHome")
	public String redirectHome(@SessionAttribute("submission") Submission submission) {
		String log_prepend = "[POST /redirectHome]";
		submission.setQuestion(null);
		submission.setCodeFiles(null);
		submission.setWriteupFiles(null);
		submission.setUsername(null);
		submission.setHomework(null);
		log.debug(log_prepend + "Submission:" + submission);
		return "redirect:showForm";
	}

}