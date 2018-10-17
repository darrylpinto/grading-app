package com.RitCapstone.GradingApp.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.RitCapstone.GradingApp.FileUploader;
import com.RitCapstone.GradingApp.FileValidator;

@Controller
@RequestMapping("/upload")
public class UploadController {

	@Autowired
	FileValidator fileValidator;

	@RequestMapping(value = "/uploadPage", method = RequestMethod.GET)
	public ModelAndView uploadPage() {

		ModelAndView model = new ModelAndView("upload_page");
		model.addObject("formUpload", new FileUploader());

		return model;
	}

	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	public ModelAndView upload(@ModelAttribute("formUpload") FileUploader fileUpload, BindingResult bindingResult) {

		fileValidator.validate(fileUpload, bindingResult);

		if (bindingResult.hasErrors()) {
			return new ModelAndView("upload_page");
		}

		return new ModelAndView("upload_success", "fileNames", processUpload(fileUpload));
	}

	private List<String> processUpload(FileUploader files) {
		List<String> fileNames = new ArrayList<String>();

		CommonsMultipartFile[] commonsMultipartFiles = files.getFiles();

		for (CommonsMultipartFile multipartFile : commonsMultipartFiles) {
			try {

				String new_dir = "uploads_from_springMVC";
				String path_sep = File.separator;
				String chosen_dir = System.getProperty("user.dir") + path_sep + new_dir + path_sep;

				// To create the directory if it is not there
				new File(chosen_dir + ".tmp").mkdirs();

				FileCopyUtils.copy(multipartFile.getBytes(),
						new File(chosen_dir + multipartFile.getOriginalFilename()));

				fileNames.add(multipartFile.getOriginalFilename());

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return fileNames;
	}

}
