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
	private static String log_prepend = String.format("[%s]", "TestCasesDAOImpl");

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
	public boolean getTestCaseFiles(String homework, String question, String destLocation) {

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
					String fileName = doc.get("fileName", String.class);
					GridFSDBFile DBFile = gfs.findOne(fileName);

					new File(destLocation).mkdirs();

					String path;
					if (destLocation.endsWith(File.separator)) {
						path = destLocation + fileName;
					} else {
						path = destLocation + File.separator + fileName;
					}

					DBFile.writeTo(path);
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
	public boolean createTestCase(String homework, String question, String testCaseNumber, File testcaseFile) {

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
				String newFileName = String.format("%s_%s_%s", homework, question, testCaseNumber);

				GridFS gfs = new GridFS(db, FILE_SYSTEM);
				GridFSInputFile gfsFile = gfs.createFile(testcaseFile);

				gfsFile.setFilename(newFileName);
				gfsFile.save();

				map.put("fileName", newFileName);
				Document doc = new Document(map);
				collection.insertOne(doc);

				log.info(String.format(
						"%s created new TestCaseNumber Document [fileName: %s]: Homework (%s), question (%s), testCaseNumber (%s)",
						log_prepend, newFileName, homework, question, testCaseNumber));
				return true;
			}
		} catch (Exception e) {
			log.error(log_prepend + " Exception occurred in createTestCase:" + e.getMessage());
			return false;
		}

	}

	@Override
	public boolean updateTestCase(String homework, String question, String testCaseNumber, File testcaseFile) {

		log.info(String.format("%s Updating testCase, Homework (%s), question (%s), testCaseNumber (%s)", log_prepend,
				homework, question, testCaseNumber));

		String databaseName = MongoFactory.getDatabaseName();
		String newFileName = String.format("%s_%s_%s", homework, question, testCaseNumber);
		try {

			// Retrieve file from GridFS and Delete it
			DB db = MongoFactory.getDB(databaseName);

			GridFS gfs = new GridFS(db, FILE_SYSTEM);
			gfs.remove(newFileName);
			// oldFileName will be the same as newFileName as logic for naming files is
			// homework_question_testCaseNumber

			log.debug(log_prepend + " stale testCaseFile removed");

			GridFSInputFile gfsFile = gfs.createFile(testcaseFile);
			gfsFile.setFilename(newFileName);
			gfsFile.save();
			log.debug(log_prepend + " added new testCaseFile: " + newFileName);

			MongoCollection<Document> collection = MongoFactory.getCollection(databaseName, testCaseCollection);

			BasicDBObject searchQuery = new BasicDBObject();
			searchQuery.put("homework", homework);
			searchQuery.put("question", question);
			searchQuery.put("testCaseNumber", testCaseNumber);

			BasicDBObject newDocument = new BasicDBObject();
			newDocument.put("fileName", newFileName);

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
		File f = new File("/home/darryl/_testCases/input-2.4");
		boolean created = new TestCasesDAOImpl().createTestCase("hw99", "1", "4", f);
		System.out.println(created);
	}

}
