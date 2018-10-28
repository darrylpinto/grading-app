package com.RitCapstone.GradingApp.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.apache.commons.io.FileUtils;
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
import com.RitCapstone.GradingApp.service.FileService;
import com.RitCapstone.GradingApp.service.OnlineCompileAPIService;
import com.RitCapstone.GradingApp.service.SubmissionDBService;
import com.RitCapstone.GradingApp.service.TestCaseDBService;

@Controller
@RequestMapping("/submission")
@SessionAttributes("submission")
public class SubmissionController {

	Submission submission;

	@Autowired
	FileValidator fileValidator;

	@Autowired
	FileService fileService;

	@Autowired
	SubmissionDBService submissionDBService;

	@Autowired
	TestCaseDBService testCaseDBService;

	@Autowired
	OnlineCompileAPIService compileAPIService;

	private static Logger log = Logger.getLogger(SubmissionController.class);

	/**
	 * This method will trim all the strings received from form data
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

		// we want to focus on errors on homework and username field
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
		return "redirect:pleaseWait";
	}

	@GetMapping("/pleaseWait")
	public String displayWait() {
		// This is display the loader
		return "please-wait";
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
		String homework = submission.getHomework();
		String username = submission.getUsername();
		String question = submission.getQuestion();
		String language = submission.getLanguage();

		// save the files uploaded my student to local machine
		String zipPath = fileService.saveFiles(homework, username, question, submission.getCodeFiles(),
				submission.getWriteupFiles());

		// save the information of homework, question, username, and submission location
		// to mongoDB
		boolean savedSubmission = submissionDBService.saveSubmission(homework, username, question, zipPath,
				question + ".zip");
		if (!savedSubmission) {
			log.error(String.format("%s Submission not saved:Homework (%s), username (%s), question (%s)", log_prepend,
					homework, username, question));
		}

		// Unzip the test case files to local
		String unzipTestCaseLoc = zipPath + "testCases" + question;
		boolean testcaseToLocal = testCaseDBService.getTestCases(homework, question, unzipTestCaseLoc);
		if (!testcaseToLocal) {
			log.error(String.format("%s Test case not found :Homework (%s), question (%s)", log_prepend, homework,
					username));
		}

		String zipFile = zipPath + question + ".zip";
		String unzipDest = zipPath + question;

		boolean unzipped = fileService.unzip(zipFile, unzipDest);
		// It will unzip to directory question directory

		if (!unzipped) {
			log.error(String.format("%s Unzip Failed for %s: Homework (%s), question (%s)", log_prepend, zipFile,
					homework, username));
		}

		// delete non code-file from unzip dir
		boolean deleteNonCodeFiles = fileService.deleteNonCodeFiles(unzipDest);

		if (!deleteNonCodeFiles) {
			log.error(String.format("%s deleteNonCodeFiles Failed %s: Homework (%s), question (%s)", log_prepend,
					zipFile, homework, username));
		}

		// TODO main_file_name needs to be resolved here
		String jsonValidString = null;
		try {
			jsonValidString = compileAPIService.getJSONValidStringCode(unzipDest, "MaxRectanglePerimeter", language);
		} catch (Exception e) {
			log.error(log_prepend + " Error in getting json valid string: " + e.getMessage());
		}

		// Get all the test case files
		File[] testCaseFiles = fileService.getFiles(unzipTestCaseLoc);

		ArrayList<String> outputList = new ArrayList<>();

		// All the files in test cases need to be run in API
		for (File testCaseFile : testCaseFiles) {
			String testCaseString = compileAPIService.getJSONValidTestCase(testCaseFile);
			outputList.add(compileAPIService.useJudge0API(jsonValidString, language, testCaseString));
		}

		// delete unzip folder, testcases and code
		try {
			FileUtils.deleteDirectory(new File(unzipDest));
			log.debug(log_prepend + " deleted: " + unzipDest);
		} catch (IOException e) {
			log.error(log_prepend + " Unable to delete: " + unzipDest);
		}

		try {
			FileUtils.deleteDirectory(new File(unzipTestCaseLoc));
			log.debug(log_prepend + " deleted: " + unzipTestCaseLoc);
		} catch (IOException e) {
			log.error(log_prepend + " Unable to delete: " + unzipTestCaseLoc);
		}

		// Show confirmation to students of submitted file
		List<String> codeFileNames = submission.getFileNames(submission.codeFileType);
		List<String> writeupFileNames = submission.getFileNames(submission.writeupFileType);

		model.addAttribute("codeFileNames", codeFileNames);
		model.addAttribute("writeupFileNames", writeupFileNames);
		model.addAttribute("outputList", outputList);

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
	 * 
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