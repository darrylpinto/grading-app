package com.RitCapstone.GradingApp.mongo;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.bson.Document;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class MongoFactory {

	private static Logger log = Logger.getLogger(MongoFactory.class);
	
	static String MONGO_JSON = "mongo.json";
	
	private static MongoClient mongo;

	private MongoFactory() {
	}

	public static MongoClient getMongo() {

		if (mongo == null) {

			ClassLoader classLoader = MongoFactory.class.getClassLoader();
			File file = new File(classLoader.getResource(MONGO_JSON).getFile());

			JSONParser parser = new JSONParser();

			try {
				JSONObject jsonObject = (JSONObject) parser.parse(new FileReader(file));
				String hostname = (String) jsonObject.get("hostname");
				long port = (Long) jsonObject.get("port");
				
				mongo = new MongoClient(hostname, (int) port);

			} catch (IOException | ParseException | MongoException e) {
				log.error(e);
			}
		}
		return mongo;

	}

	public static MongoDatabase getDB(String db_name) {
		return getMongo().getDatabase(db_name);
	}

	public static MongoCollection<Document> getCollection(String db_name, String db_collection) {
		return getDB(db_name).getCollection(db_collection);
	}

}
