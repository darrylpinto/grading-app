package com.RitCapstone.GradingApp.dao;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.bson.Document;

import org.springframework.stereotype.Repository;

import com.RitCapstone.GradingApp.mongo.MongoFactory;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;

@Repository
public class TestCasesDAOImpl implements TestCasesDAO {

	private static final String testCaseCollection = "testCase";
	private static final String FILE_SYSTEM = "fs";

	private static Logger log = Logger.getLogger(TestCasesDAOImpl.class);
	private static String log_prepend = "[TestCasesDAOImpl]";

	@Override
	public boolean testCaseExists(String homework, String question, String testCaseNumber) {
		String databaseName = MongoFactory.getDatabaseName();

		try {
			MongoCollection<Document> collection = MongoFactory.getCollection(databaseName, testCaseCollection);

			BasicDBObject searchQuery = new BasicDBObject();
			searchQuery.put("homework", homework);
			searchQuery.put("question", question);
			searchQuery.put("testCaseNumber", testCaseNumber);

			FindIterable<Document> findIterable = collection.find(searchQuery);
			MongoCursor<Document> cursor = findIterable.iterator();

			if (!cursor.hasNext()) {
				log.warn(String.format("%s No testCaseFile found: Homework (%s), question (%s) testCaseNumber (%s)",
						log_prepend, homework, question, testCaseNumber));
				return false;
			} else {
				log.debug(String.format("%s TestCases Found: : Homework (%s), question (%s) testCaseNumber (%s)",
						log_prepend, homework, question, testCaseNumber));
				return true;
			}
		} catch (Exception e) {
			log.error(log_prepend + " Exception occurred in testCasesExists:" + e.getMessage());
			return false;
		}

	}

	@Override
	public boolean getTestCaseFilesToLocal(String homework, String question, String destLocation) {

		log.info(String.format("%s Retrieving testCases, Homework (%s), question (%s)", log_prepend, homework,
				question));

		String databaseName = MongoFactory.getDatabaseName();

		try {
			MongoCollection<Document> collection = MongoFactory.getCollection(databaseName, testCaseCollection);

			BasicDBObject searchQuery = new BasicDBObject();
			searchQuery.put("homework", homework);
			searchQuery.put("question", question);

			FindIterable<Document> findIterable = collection.find(searchQuery);
			MongoCursor<Document> cursor = findIterable.iterator();

			if (!cursor.hasNext()) {
				log.warn(String.format("%s No testCaseFiles found, Homework (%s), question (%s)", log_prepend, homework,
						question));
				return false;
			} else {

				DB db = MongoFactory.getDB(databaseName);
				GridFS gfs = new GridFS(db, FILE_SYSTEM);
				int count = 0;
				while (cursor.hasNext()) {

					Document doc = cursor.next();
					String inputFileName = doc.get("inputFileName", String.class);
					GridFSDBFile DBFileInput = gfs.findOne(inputFileName);

					String outputFileName = doc.get("outputFileName", String.class);
					GridFSDBFile DBFileOutput = gfs.findOne(inputFileName);

					new File(destLocation).mkdirs();

					String pathInput, pathOutput;
					if (destLocation.endsWith(File.separator)) {
						pathInput = destLocation + inputFileName;
						pathOutput = destLocation + outputFileName;
					} else {
						pathInput = destLocation + File.separator + inputFileName;
						pathOutput = destLocation + File.separator + outputFileName;
					}

					DBFileInput.writeTo(pathInput);
					DBFileOutput.writeTo(pathOutput);
					count++;
				}
				log.info(String.format("%s %d testCaseFiles saved to %s: Homework (%s), question (%s)", log_prepend,
						count, destLocation, homework, question));
				return true;

			}

		} catch (Exception e) {
			log.error(log_prepend + " Exception occurred in getTestCaseFiles:" + e.getMessage());
			return false;
		}

	}

	@Override
	public boolean createTestCase(String homework, String question, String testCaseNumber, File testcaseInput,
			File testcaseOutput) {

		log.info(String.format("%s Creating new testCase, Homework (%s), question (%s), testCaseNumber (%s)",
				log_prepend, homework, question, testCaseNumber));

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("homework", homework);
		map.put("question", question);
		map.put("testCaseNumber", testCaseNumber);

		String databaseName = MongoFactory.getDatabaseName();
		// if no document in testCaseCollection, we create new document, else we return
		// false as document exists
		try {
			MongoCollection<Document> collection = MongoFactory.getCollection(databaseName, testCaseCollection);
			BasicDBObject searchQuery = new BasicDBObject(map);

			FindIterable<Document> findIterable = collection.find(searchQuery);
			MongoCursor<Document> cursor = findIterable.iterator();

			if (cursor.hasNext()) {
				log.error(String.format(
						"%s TestCaseNumber Document already exists: Homework (%s), question (%s), testCaseNumber (%s)",
						log_prepend, homework, question, testCaseNumber));
				return false;

			} else {

				// Save file to GridFS
				DB db = MongoFactory.getDB(databaseName);
				String inputFileName = String.format("%s_%s_%s_input", homework, question, testCaseNumber);
				String outputFileName = String.format("%s_%s_%s_output", homework, question, testCaseNumber);

				GridFS gfs = new GridFS(db, FILE_SYSTEM);
				GridFSInputFile gfsInputFile = gfs.createFile(testcaseInput);
				GridFSInputFile gfsOutputFile = gfs.createFile(testcaseOutput);

				gfsInputFile.setFilename(inputFileName);
				gfsInputFile.save();
				map.put("inputFileName", inputFileName);

				gfsOutputFile.setFilename(outputFileName);
				gfsOutputFile.save();
				map.put("outputFileName", outputFileName);

				Document doc = new Document(map);
				collection.insertOne(doc);

				log.info(String.format(
						"%s created new TestCaseNumber Document [fileName: %s]: Homework (%s), question (%s), testCaseNumber (%s)",
						log_prepend, inputFileName, homework, question, testCaseNumber));
				return true;
			}
		} catch (Exception e) {
			log.error(log_prepend + " Exception occurred in createTestCase:" + e.getMessage());
			return false;
		}

	}

	@Override
	public boolean updateTestCase(String homework, String question, String testCaseNumber, File testcaseInput,
			File testcaseOutput) {

		log.info(String.format("%s Updating testCase, Homework (%s), question (%s), testCaseNumber (%s)", log_prepend,
				homework, question, testCaseNumber));

		String inputFileName = String.format("%s_%s_%s_input", homework, question, testCaseNumber);
		String outputFileName = String.format("%s_%s_%s_output", homework, question, testCaseNumber);

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("homework", homework);
		map.put("question", question);
		map.put("testCaseNumber", testCaseNumber);

		String databaseName = MongoFactory.getDatabaseName();

		MongoCollection<Document> collection = MongoFactory.getCollection(databaseName, testCaseCollection);
		BasicDBObject searchQuery = new BasicDBObject(map);

		FindIterable<Document> findIterable = collection.find(searchQuery);
		MongoCursor<Document> cursor = findIterable.iterator();

		if (!cursor.hasNext()) {
			log.error(String.format(
					"%s TestCaseNumber Document does not exist: Homework (%s), question (%s), testCaseNumber (%s)",
					log_prepend, homework, question, testCaseNumber));
			return false;
		}
		try {

			// Retrieve file from GridFS and Delete it
			DB db = MongoFactory.getDB(databaseName);

			GridFS gfs = new GridFS(db, FILE_SYSTEM);
			gfs.remove(inputFileName);
			gfs.remove(outputFileName);
			// oldFileName will be the same as newFileName as logic for naming files is
			// homework_question_testCaseNumber_[input or output]

			log.debug(log_prepend + " stale testCaseFiles removed");

			GridFSInputFile gfsInputFile = gfs.createFile(testcaseInput);
			gfsInputFile.setFilename(inputFileName);
			gfsInputFile.save();

			GridFSInputFile gfsOutputFile = gfs.createFile(testcaseOutput);
			gfsOutputFile.setFilename(outputFileName);
			gfsOutputFile.save();
			log.debug(log_prepend + " added new testCaseFiles: " + inputFileName + ", " + outputFileName);

			BasicDBObject newDocument = new BasicDBObject();
			newDocument.put("inputFileName", inputFileName);
			newDocument.put("outputFileName", outputFileName);

			BasicDBObject updateObject = new BasicDBObject();
			updateObject.put("$set", newDocument);

			// update submission to MongoDB
			collection.updateOne(searchQuery, updateObject);

			return true;

		} catch (Exception e) {
			log.error(log_prepend + " Exception occurred in updateTestCase:" + e.getMessage());
			return false;
		}
	}

	public static void main(String[] args) {
		System.out.println("hi");
		String c= "6";
		File fin = new File("/home/darryl/cTestCases/input-2."+c);
		File fout = new File("/home/darryl/cTestCases/answer-2."+c);
		boolean created = new TestCasesDAOImpl().createTestCase("hw99", "1", c, fin, fout);
		System.out.println(created);
	}

}
