package com.RitCapstone.GradingApp.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;

@Service
public class OnlineCompileAPIService {

	private static final String python_JSON = "pythonCode.json";

	private static Logger log = Logger.getLogger(OnlineCompileAPIService.class);
	private static String log_prepend = String.format("[%s]", "OnlineCompileAPIService");

	public String getJSONValidStringCode(String dir_name, String mainFileName) throws Exception {

		File dir = new File(dir_name);
		File[] listOfFiles = dir.listFiles();

		// Finding the code language
		String extension = "";
		for (File file : listOfFiles) {

			String filename = file.getName();
			String[] _parts = filename.trim().split("\\.");
			extension = "." + _parts[_parts.length - 1];

			if (extension.equals(".java") || extension.equals(".cpp")) {
				break;
			}
		}

		String jsonValidCodeString = "";
		if (extension.equals("")) {
			throw new Exception("unsupported format received: Allowed formats are '.java' and '.cpp'");
		} else if (extension.equals(".java")) {
			jsonValidCodeString = combineJava(listOfFiles, mainFileName);
		} else if (extension.equals(".cpp")) {
			jsonValidCodeString = combineCPP(listOfFiles);
		} else {
			throw new Exception("should not come here");
		}

		return jsonValidCodeString;
	}

	private String combineCPP(File[] listOfFiles) throws IOException {
		System.out.println("In cpp");
		// We have to separate header files and cpp files
		List<File> headerFiles = new ArrayList<>();
		List<File> cppFiles = new ArrayList<>();
		String output = "";

		for (File file : listOfFiles) {
			String filename = file.getName();
			String[] _parts = filename.trim().split("\\.");
			String extension = "." + _parts[_parts.length - 1];

			if (extension.equals(".h")) {
				headerFiles.add(file);
			} else if (extension.equals(".cpp")) {
				cppFiles.add(file);
			} else {
				log.error(log_prepend + " the file supplied is neither .cpp nor .h " + filename);
			}
		}

		List<String> headerFileNames = new ArrayList<>();

		for (File headerFile : headerFiles) {
			headerFileNames.add(headerFile.getName());
			Scanner sc = new Scanner(headerFile);
			sc.useDelimiter("\\Z");
			String currentFileContent = sc.next();
			sc.close();
			output += currentFileContent + "\n";
		}

		for (File cppFile : cppFiles) {
			String line = "";
			BufferedReader reader = new BufferedReader(new FileReader(cppFile));
			String currentFileContent = "";

			// Remove user-defined header:For each line we search of name of header file in
			// the line. If header file name is found we ignore that line
			while ((line = reader.readLine()) != null) {
				for (String headerName : headerFileNames) {
					if (line.indexOf(headerName) == -1) {
						currentFileContent += line + "\n";
					}
				}
			}

			output += currentFileContent + "\n";
			reader.close();
		}

		output = output.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\t", "\\t");

		return output;
	}

	private String combineJava(File[] listOfFiles, String mainFileName) throws IOException {

		String codeLines = "";
		String importLines = "";

		// We have to separate import statements from other rest of code
		for (File file : listOfFiles) {
			String line = "";
			BufferedReader reader = new BufferedReader(new FileReader(file));

			while ((line = reader.readLine()) != null) {
				if (line.indexOf("import") != -1) {
					importLines += line + "\n";
				} else if (line.indexOf("package") != -1) {
					// Ignore package lines
				} else {
					codeLines += line + "\n";
				}

			}

			reader.close();
		}

		String output = importLines + codeLines;
		output = output.replace("\\", "\\\\").replace("\"", "\\\"").replace("public class", "class")
				.replace("class " + mainFileName, "public class Main").replace("\t", "\\t").replace("\n", "\\n");

		return output;
	}

	/**
	 * Method to replace line break and tabs with literal values "\n" , "\t" This
	 * ensures that the test case file contents are converted to JSON Valid string
	 * 
	 * @param testCaseFile File containing test case information
	 * @return JSONValidTestCaseString
	 */
	public String getJSONValidTestCase(File testCaseFile) {

		try {

			Scanner sc = new Scanner(testCaseFile);
			sc.useDelimiter("\\Z");
			String fileOutput = sc.next();
			fileOutput = fileOutput.replaceAll("\t", "\\\\t").replaceAll("\n", "\\\\n");
			sc.close();
			log.debug(log_prepend + " Test case file converted to JSON Valid String");
			return fileOutput;

		} catch (IOException e) {
			log.error(log_prepend + " Error in getJSONValidTestCase(): " + e.getMessage());
			return null;

		}

	}

	public String useJudge0API(String sourceCode, String language, String input) {

		HashMap<String, Integer> languageCode = new HashMap<>();

		// The language code used for Judge0 online compiler
		// For complete list of languages, using a REST client (For example: Postman)
		// GET https://api.judge0.com/languages
		languageCode.put(".cpp", 10);
		languageCode.put(".java", 27);
		languageCode.put(".py", 34);

		try {

			int languageID = languageCode.get(language);

			String body = String.format("{\"source_code\": \"%s\", \"language_id\": %d, \"stdin\": \"%s\"}", sourceCode,
					languageID, input);

			HttpResponse<String> postResponse = Unirest.post("https://api.judge0.com/submissions/?base64_encoded=false")
					.header("Content-Type", "application/json").header("cache-control", "no-cache").body(body)
					.asString();

			log.info(log_prepend + "Status of Unirest POST operation: " + postResponse.getStatus());

			// convert string to JSON
			JSONParser parser = new JSONParser();
			JSONObject postResponseJson = (JSONObject) parser.parse(postResponse.getBody());
			String token = (String) postResponseJson.get("token");
			log.debug(log_prepend + " Token: " + token);
			JSONObject getResponseJson = null;

			while (true) {
				Thread.sleep(500);
				String getURL = String.format("https://api.judge0.com/submissions/%s?base64_encoded=false", token);
				HttpResponse<String> getResponse = Unirest.get(getURL).header("Content-Type", "application/json")
						.header("cache-control", "no-cache").asString();

				log.info(log_prepend + "Status of Unirest GET operation: " + getResponse.getStatus());

				String GETResponseBody = getResponse.getBody();

				/*
				 * The structure of getResponse is a dictionary inside a dictionary
				 * response[status][id] tells if the API is still running, finished processing
				 * or error occurred.
				 * 
				 * If id = 1, job is "In Queue"; If id = 2, job is "Processing"; If id = 3, the
				 * output is ready;
				 * 
				 * For complete list of status, using a REST client (For example: Postman)
				 * 
				 * GET https://api.judge0.com/statuses
				 */
				getResponseJson = (JSONObject) parser.parse(GETResponseBody);
				JSONObject getResponseJson_status_ = (JSONObject) getResponseJson.get("status");
				long statusId = (Long) getResponseJson_status_.get("id");

				if ((statusId == 1) || (statusId == 2)) {
					// job is not completed, check again in sometime
				} else {
					break;
				}
			}
			System.out.println(getResponseJson);
			String output = (String) getResponseJson.get("stdout");
			return output;

		} catch (Exception e) {
			log.error(log_prepend + " Error in useJudge0API(): " + e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

	public static void main(String args[]) throws Exception {

//		OnlineCompileAPIService api = new OnlineCompileAPIService();
//		System.out.println("hi");
//		String code = api.getJSONValidStringCode(
//				"/home/darryl/eclipse-workspace/grading-app/src/main/java/com/RitCapstone/GradingApp/service/temp",
//				"MaxRectanglePerimeter");
//		
//		String _input = api.getJSONValidTestCase(new File("src/main/python/input-2.6"));
//		String output = api.useJudge0API(code, ".cpp", _input);
//		System.out.println(output);
//		System.out.println("bye");
	}

}
