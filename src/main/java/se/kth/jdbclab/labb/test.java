package se.kth.jdbclab.labb;

import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.BsonDocument;
import org.bson.BsonInt64;
import org.bson.Document;
import org.bson.conversions.Bson;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

import java.util.ArrayList;

public class test {
    public static void main(String[] args) {
        try (MongoClient mongoClient = MongoClients.create(new ConnectionString("mongodb://localhost:3306"))) {
            MongoDatabase database = mongoClient.getDatabase("bookvault");
            MongoCollection<Document> books = database.getCollection("book");
            Bson filter = Filters.eq("title", "Hackers");
            books.find(filter).forEach(doc -> System.out.println(doc.get("genre")));
        }
    }
}
