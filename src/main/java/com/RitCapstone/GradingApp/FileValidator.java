package com.RitCapstone.GradingApp;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

@Component
public class FileValidator implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {
		return Submission.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {

		String jsonFile = "fileRestrictions.json";
		long sum = 0;
		Submission submission = (Submission) target;
		CommonsMultipartFile[] commonsMultipartFiles = submission.getCodeFiles();

		ClassLoader classLoader = FileValidator.class.getClassLoader();
		File file = new File(classLoader.getResource(jsonFile).getFile());

		// File Restrictions are read from JSON
		JSONParser parser = new JSONParser();

		try {
			JSONObject jsonObject = (JSONObject) parser.parse(new FileReader(file));
			long maxSize = (Long) jsonObject.get("Total maximum Size");
			String maxSizeString = (String) jsonObject.get("Total maximum Size String");

			JSONArray _codeExt = (JSONArray) jsonObject.get("codeExtension");
			JSONArray _writeupExt = (JSONArray) jsonObject.get("writeupExtension");

			HashSet<String> codeExt = new HashSet<>();
			HashSet<String> writeupExt = new HashSet<>();

			Iterator it = _writeupExt.iterator();
			while (it.hasNext())
				writeupExt.add(it.next().toString());

			it = _codeExt.iterator();
			while (it.hasNext())
				codeExt.add(it.next().toString());

			for (CommonsMultipartFile commonsMultipartFile : commonsMultipartFiles) {

				long size = commonsMultipartFile.getSize();
				String filename = commonsMultipartFile.getOriginalFilename();
				String[] _parts = filename.trim().split("\\.");

				String extension = "." + _parts[_parts.length - 1];

				if (size == 0) {
					errors.rejectValue("codeFiles", "missingFile", "No files uploaded");
				} else if (!codeExt.contains(extension)) {
					errors.rejectValue("codeFiles", "incorrectExtension", "Allowed files: " + codeExt);
				} else {
					sum += size;
				}

			}
			if (sum > maxSize) {
				errors.rejectValue("codeFiles", "largeFile", "Total size of uploads is greater than " + maxSizeString);
			}

			commonsMultipartFiles = submission.getWriteupFiles();
			sum = 0;
			for (CommonsMultipartFile commonsMultipartFile : commonsMultipartFiles) {
				long size = commonsMultipartFile.getSize();
				String filename = commonsMultipartFile.getOriginalFilename();
				String[] _parts = filename.trim().split("\\.");
				String extension = "." + _parts[_parts.length - 1];

				if (size == 0) {
					errors.rejectValue("writeupFiles", "missingFile", "No files uploaded");
				} else if (!writeupExt.contains(extension)) {
					errors.rejectValue("writeupFiles", "incorrectExtension", "Allowed files: " + writeupExt);
				} else {
					sum += size;
				}

			}
			if (sum > maxSize) {
				errors.rejectValue("writeupFiles", "largeFile", "File greater than " + maxSizeString);
			}

		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}

	}

}
