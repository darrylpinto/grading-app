package com.RitCapstone.GradingApp.controller;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.validation.Valid;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
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
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.RitCapstone.GradingApp.Homework;
import com.RitCapstone.GradingApp.ProfessorAndGrader;
import com.RitCapstone.GradingApp.Question;
import com.RitCapstone.GradingApp.service.FileService;
import com.RitCapstone.GradingApp.service.TestCaseDBService;
import com.RitCapstone.GradingApp.validator.AuthenticationValidator;
import com.RitCapstone.GradingApp.validator.TestCaseValidator;

@Controller
@RequestMapping("/professor")
@SessionAttributes(value = { "homework", "currentQuestion", "question" })
public class ProfessorController {

	private static Logger log = Logger.getLogger(SubmissionController.class);

	@Autowired
	AuthenticationValidator authValidator;

	@Autowired
	TestCaseValidator testCasevalidator;

	@Autowired
	FileService fileService;

	@Autowired
	TestCaseDBService testCaseDBService;

	/**
	 * This method will trim all the strings received from form data
	 */
	@InitBinder
	public void dataBinder(WebDataBinder dataBinder) {

		dataBinder.registerCustomEditor(Date.class, new CustomDateEditor(new SimpleDateFormat("MM/dd/yyyy"), true, 10));
		// exact length of the string is 10

		dataBinder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
		// If after trimming the string is empty, it is converted to null {Set by true}

		dataBinder.registerCustomEditor(Integer.class, new CustomNumberEditor(Integer.class, false));

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
		log.debug(log_prepend + "Displaying " + jspToDisplay);

		return jspToDisplay;
	}

	@GetMapping("/createNewHomework")
	public String createHomework(Model model) {

		String log_prepend = "[GET /createNewHomework]";

		String jspToDisplay = "create-hw";
		if (!model.containsAttribute("homework")) {
			model.addAttribute("homework", new Homework());
			log.debug(log_prepend + "adding professor to model");
		}

		log.debug(log_prepend + "model: " + model);
		log.debug(log_prepend + "Displaying " + jspToDisplay);
		return jspToDisplay;
	}

	@PostMapping("/createNewHomework")
	public String homeworkValidation(@Valid @ModelAttribute("homework") Homework homework, BindingResult bindingResult,
			RedirectAttributes redirectAttributes) {

		String log_prepend = "[POST /createNewHomework]";

		if (bindingResult.hasErrors()) {

			redirectAttributes.addFlashAttribute("homework", homework);
			redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.homework",
					bindingResult);

			log.debug(log_prepend + "Redirecting to /createNewHomework");
			return "redirect:createNewHomework";
		}
		log.debug(log_prepend + " Redirecting: /showForm");

		return "redirect:createNewHomework2";

	}

	@GetMapping("/createNewHomework2")
	public String createHWAddQuestions(@SessionAttribute("homework") Homework homework, Map<String, Object> model) {

		String log_prepend = "[GET /createNewHomework2]";
		if (!model.containsKey("question")) {
			model.put("question", new Question());
		}
		model.put("currentQuestion", 1);

		String jspToDisplay = "create-hw-add-questions";
		log.debug(log_prepend + "Displaying " + jspToDisplay);
		log.debug(log_prepend + "Model: " + model);
		return jspToDisplay;
	}

	@PostMapping("/createNewHomework2")
	public String questionValidation(@Valid @ModelAttribute("question") Question question, BindingResult bindingResult,
			RedirectAttributes redirectAttributes) {

		String log_prepend = "[POST /createNewHomework2]";

		log.debug(log_prepend + "bindingResult: " + bindingResult);

		testCasevalidator.validate(question, bindingResult);
		if (bindingResult.hasErrors()) {

			redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.question",
					bindingResult);

			log.debug(log_prepend + "Redirecting to /createNewHomework2");
			return "redirect:createNewHomework2";
		}

		log.debug(log_prepend + "Redirecting to /showConfirmation");
		return "redirect:showConfirmProcessing";

	}

	@GetMapping("/showConfirmProcessing")
	public String showConfirmProcessing(@SessionAttribute("homework") Homework homework,
			@SessionAttribute("currentQuestion") int currentQuestion, Map<String, Object> model,
			RedirectAttributes redirectAttributes) {

		String log_prepend = "[GET /showConfirmProcessing]";

		Question question = (Question) model.get("question");
		int numberOfTestCases = question.getTestCases().length;

		question.setQuestionNumber(currentQuestion);

		// add question names
		question.setTestCaseNames(fileService.getMultipartFileNames(question.getTestCases()));
		question.setOutputTestCaseNames(fileService.getMultipartFileNames(question.getOutputTestCases()));

		homework.addQuestion(question);

		// update the session attribute
		currentQuestion++;
		model.put("currentQuestion", currentQuestion);
		model.put("question", question);

		log.info(log_prepend + " testCaseInput:" + model.get("testCaseInput"));
		log.info(log_prepend + " testCaseOutput:" + model.get("testCaseOutput"));

		// Save uploaded test cases to local
		String testCaseLoc = fileService.saveTestCasesToLocal(question.getTestCases(), question.getOutputTestCases());
		log.info(log_prepend + " Saved uploaded files to local at location: " + testCaseLoc);

		// TODO Save files to MongoDB
		for (int i = 1; i <= numberOfTestCases; i++) {
			File testcaseInput = new File(testCaseLoc + "input_" + i);
			File testcaseOutput = new File(testCaseLoc + "output_" + i);
			testCaseDBService.saveTestCases(homework.getId(), "" + question.getQuestionNumber(), "" + i, testcaseInput,
					testcaseOutput);
		}
		// Delete testCaseLoc
		FileUtils.deleteQuietly(new File(testCaseLoc));
		log.info(log_prepend + "MODEL:" + model);
		return "redirect:showConfirmation";
	}

	// Post - redirect - get pattern
	@GetMapping("/showConfirmation")
	public String showConfirmation(Map<Object, String> model) {
		model.put("testCaseInput", model.get("testCaseInput"));
		model.put("testCaseOutput", model.get("testCaseInput"));
		return "hw-upload-confirmation";
	}

	@GetMapping("/createHomeworkNextQuestion")
	public String createHomeworkNextQuestion(@SessionAttribute("homework") Homework homework,
			Map<String, Object> model) {

		String log_prepend = "[GET /createHomeworkNextQuestion]";

		if (!model.containsKey("org.springframework.validation.BindingResult.question")) {
			model.put("question", new Question());
		}

		String jspToDisplay = "create-hw-add-questions";
		log.debug(log_prepend + "Displaying " + jspToDisplay);
		log.debug(log_prepend + "Model: " + model);
		return jspToDisplay;
	}

	@PostMapping("/createHomeworkNextQuestion")
	public String nextQuestionValidation(@Valid @ModelAttribute("question") Question question,
			BindingResult bindingResult, RedirectAttributes redirectAttributes) {

		String log_prepend = "[POST /createHomeworkNextQuestion]";

		log.debug(log_prepend + "bindingResult: " + bindingResult);

		testCasevalidator.validate(question, bindingResult);
		if (bindingResult.hasErrors()) {

			redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.question",
					bindingResult);

			log.debug(log_prepend + "Redirecting to /createHomeworkNextQuestion");
			return "redirect:createHomeworkNextQuestion";
		}

		log.debug(log_prepend + "Redirecting to /showConfirmProcessing");
		return "redirect:showConfirmProcessing";

	}
}
