package com.RitCapstone.GradingApp;

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

		Submission submission = (Submission) target;
		long sum = 0;
		long max_size = 2 * 1024 * 1024;
		CommonsMultipartFile[] commonsMultipartFiles = submission.getCodeFiles();

		for (CommonsMultipartFile commonsMultipartFile : commonsMultipartFiles) {
			long size = commonsMultipartFile.getSize();

			if (size == 0) {
				errors.rejectValue("codeFiles", "missing.file", "No files uploaded");
			} else {
				sum += size;
			}

		}
		if (sum > max_size) {
			errors.rejectValue("codeFiles", "large.file", "Total size of uploads is greater than 2MB");
		}

		commonsMultipartFiles = submission.getWriteupFiles();
		sum = 0;
		for (CommonsMultipartFile commonsMultipartFile : commonsMultipartFiles) {
			long size = commonsMultipartFile.getSize();

			if (size == 0) {
				errors.rejectValue("writeupFiles", "missing.file", "No files uploaded");
			} else {
				sum += size;
			}

		}
		if (sum > max_size) {
			errors.rejectValue("writeupFiles", "large.file", "Total size of uploads is greater than 2MB");
		}

	}

}
