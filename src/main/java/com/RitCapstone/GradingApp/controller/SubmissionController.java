package com.RitCapstone.GradingApp.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
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
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.RitCapstone.GradingApp.FileValidator;
import com.RitCapstone.GradingApp.Homework;
import com.RitCapstone.GradingApp.Submission;

@Controller
@RequestMapping("/submission")
@SessionAttributes("submission")
public class SubmissionController {

	Submission submission;

	@Autowired
	FileValidator fileValidator;

	// This method will trim all the strings
	@InitBinder
	public void stringTrimmer(WebDataBinder dataBinder) {

		StringTrimmerEditor trimmer = new StringTrimmerEditor(true); 
		// If after trimming the string is empty, it is converted to null {Set by true} 
		
		dataBinder.registerCustomEditor(String.class, trimmer);
	}

	private static Logger log = Logger.getLogger(SubmissionController.class);

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

		String jspToDisplay = "student-submission";

		if (!model.containsAttribute("submission")) {
			model.addAttribute("submission", new Submission());
			log.debug("adding submission to model");
		}

		if (!model.containsAttribute("hw")) {
			model.addAttribute("hw", new Homework());
			log.debug("adding hw to model");
		}

		log.debug("model in showForm: " + model);
		log.debug("[/showForm] Displaying " + jspToDisplay);

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

		// we want to ignore Errors on question field
		List<FieldError> homework_err = bindingResult.getFieldErrors("homework");
		List<FieldError> username_err = bindingResult.getFieldErrors("username");

		if (homework_err.size() + username_err.size() > 0) {
			redirectAttributes.addFlashAttribute("submission", submission);
			redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.submission",
					bindingResult);

			log.debug("[{POST} of /showForm] Redirecting to showForm");
			return "redirect:showForm";
		}

		log.debug("[{POST} of /showForm] Redirecting to showForm2");
		return "redirect:showForm2";
	}

	/**
	 * Method to handle remaining form, This form consists of question to select
	 * 
	 * @return .jsp file to display
	 */
	@GetMapping("/showForm2")
	public String showRemainingForm(@SessionAttribute("submission") Submission submission, Model model) {

		if (!model.containsAttribute("hw")) {
			model.addAttribute("hw", new Homework());
			log.debug("adding hw to model");
		}
		log.debug("[GET showForm2 SESSION]:" + submission);
		log.debug("[GET showForm2 MODEL]:" + model);
		String jspToDisplay = "student-submission-remaining";
		log.debug("[/showForm2] Displaying " + jspToDisplay);
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

		fileValidator.validate(submission, bindingResult);
		log.debug("Model in POST validateRemainingForm " + model);

		if (bindingResult.hasErrors()) {
			redirectAttributes.addFlashAttribute("submission", submission);
			redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.submission",
					bindingResult);

			log.debug("[{POST} of /showForm2] Redirecting to showForm2");
			return "redirect:showForm2";
		}

		log.debug("Model in POST validateRemainingForm " + model);

		log.debug("[{POST} of /showForm2] Redirecting to showConfirmation");
		return "redirect:showConfirmation";
	}

	@RequestMapping(value = "/showConfirmation")
	public String showConfirmation(@SessionAttribute("submission") Submission submission, Model model) {
		log.debug("[GET showConfirmation] Displaying student-confirmation");
		log.debug("Model in showConfirmation():" + model);

		List<String> codeFiles = processAndSaveFiles(submission.getCodeFiles(), "CODE_");
		List<String> writeupFiles = processAndSaveFiles(submission.getWriteupFiles(), "WRITEUP_");

		model.addAttribute("codeFileNames", codeFiles);
		model.addAttribute("writeupFileNames", writeupFiles);
		
		log.debug("Displaying: student-confirmation");
		return "student-confirmation";
	}

	private List<String> processAndSaveFiles(CommonsMultipartFile[] commonsMultipartFiles, String prepend) {
		List<String> fileNames = new ArrayList<String>();

		for (CommonsMultipartFile multipartFile : commonsMultipartFiles) {
			try {

				String new_dir = "uploads_from_springMVC";
				String path_sep = File.separator;
				String chosen_dir = System.getProperty("user.dir") + path_sep + new_dir + path_sep;

				// To create the directory if it is not there
				new File(chosen_dir + ".tmp").mkdirs();

				FileCopyUtils.copy(multipartFile.getBytes(),
						new File(chosen_dir + prepend + multipartFile.getOriginalFilename()));

				fileNames.add(multipartFile.getOriginalFilename());

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return fileNames;
	}
}