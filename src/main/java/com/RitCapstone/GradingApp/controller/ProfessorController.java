package com.RitCapstone.GradingApp.controller;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.validation.Valid;

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
import com.RitCapstone.GradingApp.validator.AuthenticationValidator;
import com.RitCapstone.GradingApp.validator.TestCaseValidator;

@Controller
@RequestMapping("/professor")
@SessionAttributes(value = { "homework", "currentQuestion" })
public class ProfessorController {

	private static Logger log = Logger.getLogger(SubmissionController.class);

	@Autowired
	AuthenticationValidator authValidator;

	@Autowired
	TestCaseValidator testCasevalidator;

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
	public String createHWAddQuestions(@SessionAttribute("homework") Homework homework, Model model) {
		
		String log_prepend = "[GET /createNewHomework2]";
		if (!model.containsAttribute("question")) {
			model.addAttribute("question",new Question());
			log.debug(log_prepend + "adding professor to model");
		}
		if (!model.containsAttribute("currentQuestion")) {
			model.addAttribute("currentQuestion", 1);
			log.debug(log_prepend + "adding professor to model");
		}
		
		String jspToDisplay = "create-hw-add-questions";
		log.debug(log_prepend + "Displaying " + jspToDisplay);
		log.debug(log_prepend + "Model: " + model);

		return jspToDisplay;
	}

	@PostMapping("/createNewHomework2")
	public String questionValidation(@Valid @ModelAttribute("question") Question question, BindingResult bindingResult,
			RedirectAttributes redirectAttributes, @SessionAttribute("currentQuestion") int currentQuestion) {

		String log_prepend = "[POST /createNewHomework2]";

		log.debug(log_prepend + "**bindingResult: " + bindingResult);
		log.debug(log_prepend + "**question: " + question);
		
		testCasevalidator.validate(question, bindingResult);
		if (bindingResult.hasErrors()) {

			redirectAttributes.addFlashAttribute("question", question);
			redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.question",
					bindingResult);

			log.debug(log_prepend + "Redirecting to /createNewHomework2");
			return "redirect:createNewHomework2";
		}

		// TODO Save files to MongoDB

		return "TEMP";
	}

}
