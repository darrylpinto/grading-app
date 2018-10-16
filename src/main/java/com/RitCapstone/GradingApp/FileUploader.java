package com.RitCapstone.GradingApp;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

public class FileUploader {

	private CommonsMultipartFile[] files;

	 public CommonsMultipartFile[] getFiles() {
	  return files;
	 }

	 public void setFiles(CommonsMultipartFile[] files) {
	  this.files = files;
	 }
}


