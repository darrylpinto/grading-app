package com.RitCapstone.GradingApp.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Scanner;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;

import com.RitCapstone.GradingApp.mongo.MongoFactory;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;

@Service
public class OnlineCompileAPIService {

	private static final String python_JSON = "pythonCode.json";

	private static Logger log = Logger.getLogger(OnlineCompileAPIService.class);
	private static String log_prepend = String.format("[%s]", "OnlineCompileAPIService");

	/**
	 * Method to call src/main/python script to convert the student code to
	 * jsonValidString
	 * 
	 * @param dir_name       directory where code files are there
	 * @param main_file_name file with the main method, {Generally it should be the
	 *                       Question Submission name}. For example, If the question
	 *                       is checkPrime, the student should submit
	 *                       checkPrime.java, checkPrime.cpp and this file should
	 *                       have the main method
	 * 
	 * @return JSONValidString
	 */
	public String getJSONValidStringCode(String dir_name, String main_file_name) {

		ClassLoader classLoader = MongoFactory.class.getClassLoader();
		File file = new File(classLoader.getResource(python_JSON).getFile());
		JSONParser parser = new JSONParser();

		String codeLoc = "", outputLoc = "";

		try {
			// Get Python code and output location from JSON file
			JSONObject jsonObject = (JSONObject) parser.parse(new FileReader(file));
			codeLoc = (String) jsonObject.get("codeLocation");
			outputLoc = (String) jsonObject.get("outputLocation");

		} catch (IOException | ParseException e) {
			log.error(log_prepend + " Error in reading from JSON file [getJSONValidStringCode]: " + e.getMessage());
			return null;
		}

		try {

			String command = String.format("python3 %s %s %s %s", codeLoc, dir_name, main_file_name, outputLoc);
			log.debug(log_prepend + " COMMAND: " + command);

			// Run the command
			Process p = Runtime.getRuntime().exec(command);
			p.waitFor();
			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

			String line = "", codeOutput = "";
			while ((line = reader.readLine()) != null) {
				codeOutput += line;
			}
			reader.close();

			log.debug(log_prepend + " Python code output: " + codeOutput);

			File jsonValidStringFile = new File(outputLoc);
			String fileOutput = "";
			// Retrieve the JSON valid String from outputLoc
			Scanner sc = new Scanner(jsonValidStringFile);
			sc.useDelimiter("\\Z");
			fileOutput = sc.next();
			sc.close();

			// delete outputLoc
			jsonValidStringFile.delete();
			log.debug(log_prepend + " jsonValidString deleted");

			return fileOutput;

		} catch (Exception e) {
			log.error(log_prepend + " Error while running Python code [getJSONValidStringCode]: " + e.getMessage());
			return null;
		}
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

	public static void main(String args[]) {

		OnlineCompileAPIService api = new OnlineCompileAPIService();
		System.out.println("hi");
		String code = api.getJSONValidStringCode("/home/darryl/uploads_from_springMVC/hw99/_kja1/1",
				"MaxRectanglePerimeter");

		String _input = api.getJSONValidTestCase(new File("src/main/python/input-2.3"));
		String output = api.useJudge0API(code, ".java", _input);
		System.out.println(output);
		System.out.println("bye");
	}

}
