package com.RitCapstone.GradingApp.dao;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.bson.Document;
import org.springframework.stereotype.Repository;

import com.RitCapstone.GradingApp.mongo.MongoFactory;
import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;

@Repository
public class QuestionDAOImpl implements QuestionDAO {

	private static final String questionMetadataColl = "questionMetadata";

	private static Logger log = Logger.getLogger(QuestionDAOImpl.class);
	private static String log_prepend = "[QuestionDAOImpl]";

	@Override
	public Map<String, Object> getQuestionMetaData(String homework, String questionNumber) {

		String databaseName = MongoFactory.getDatabaseName();
		Map<String, Object> metadata = new HashMap<>();
		MongoCollection<Document> collection = MongoFactory.getCollection(databaseName, questionMetadataColl);

		BasicDBObject searchQuery = new BasicDBObject();
		searchQuery.put("homework", homework);
		searchQuery.put("question", questionNumber);

		FindIterable<Document> findIterable = collection.find(searchQuery);
		MongoCursor<Document> cursor = findIterable.iterator();
		
		while(cursor.hasNext()) {
			Document doc = cursor.next();
			
			metadata.put("question", doc.get("question", String.class));
			metadata.put("homework", doc.get("homework", String.class));
			metadata.put("dueDate", doc.get("dueDate", Date.class));
			metadata.put("problemName", doc.get("problemName", String.class));
			
		}
		return metadata;
	}

	@Override
	public boolean createQuestionMetaData(String homework, String questionNumber, String problemName,
			String description, Date dueDate) {
		log.info(String.format("%s Creating new questionMetaData, Homework (%s), question (%s)", log_prepend, homework,
				questionNumber));

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("homework", homework);
		map.put("question", questionNumber);

		String databaseName = MongoFactory.getDatabaseName();

		MongoCollection<Document> collection = MongoFactory.getCollection(databaseName, questionMetadataColl);
		BasicDBObject searchQuery = new BasicDBObject(map);
		FindIterable<Document> findIterable = collection.find(searchQuery);
		MongoCursor<Document> cursor = findIterable.iterator();

		if (cursor.hasNext()) {
			log.warn(String.format("%s questionMetaData already exists: Homework (%s), question (%s)", log_prepend,
					homework, questionNumber));
			return false;
		} else {
			try {
				map.put("problemName", problemName);
				map.put("description", description);
				map.put("dueDate", dueDate);
				Document doc = new Document(map);
				collection.insertOne(doc);
				return true;
			} catch (Exception e) {
				log.error(log_prepend + " Exception occurred in createTestCase:" + e.getMessage());
				return false;
			}
		}

	}

	@Override
	public boolean updateQuestionMetaData(String homework, String questionNumber, String problemName,
			String description, Date dueDate) {
		log.info(String.format("%s Creating new questionMetaData, Homework (%s), question (%s)", log_prepend, homework,
				questionNumber));

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("homework", homework);
		map.put("question", questionNumber);

		String databaseName = MongoFactory.getDatabaseName();

		MongoCollection<Document> collection = MongoFactory.getCollection(databaseName, questionMetadataColl);
		BasicDBObject searchQuery = new BasicDBObject(map);
		FindIterable<Document> findIterable = collection.find(searchQuery);
		MongoCursor<Document> cursor = findIterable.iterator();

		if (!cursor.hasNext()) {
			log.warn(String.format("%s questionMetaData does not exist: Homework (%s), question (%s)", log_prepend,
					homework, questionNumber));
			return false;
		} else {
			try {
				map.put("problemName", problemName);
				map.put("description", description);
				map.put("dueDate", dueDate);

				// TODO update
				BasicDBObject newDocument = new BasicDBObject(map);
				BasicDBObject updateObject = new BasicDBObject();
				updateObject.put("$set", newDocument);
				collection.updateOne(searchQuery, updateObject);
				return true;
			} catch (Exception e) {
				log.error(log_prepend + " Exception occurred in createTestCase:" + e.getMessage());
				return false;
			}
		}
	}

	public static void main(String[] args) {
		System.out.println("hi");
		System.out.println(new QuestionDAOImpl().getQuestionMetaData("hwQM", "1"));
//				"MaxPerimeter","a new line\n sond line\n THIRD line", new Date()));

		System.out.println("bye");
	}
}
