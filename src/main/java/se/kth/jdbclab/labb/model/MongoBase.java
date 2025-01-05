package se.kth.jdbclab.labb.model;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoException;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import org.bson.BsonDocument;
import org.bson.BsonInt64;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MongoBase implements IDatabase {
    private MongoDatabase db;

    public MongoBase() {
        try (MongoClient mongoClient = MongoClients.create(new ConnectionString("mongodb://localhost:3306"))) {
            db = mongoClient.getDatabase("bookvault");
        }
    }
    @Override
    public void manageBook(boolean mode, int userID, Book book) {

    }

    @Override
    public List<Book> loadBooks() {
        MongoCollection<Document> books = db.getCollection("book");
        FindIterable<Document> fi = books.find();
        List<Book> bookList = new ArrayList<>();
        List<Author> authorList = new ArrayList<>();
        List<Genre> genreList = new ArrayList<>();
        MongoCursor<Document> cursor = fi.iterator();
        try {
            while(cursor.hasNext()) {
                String title = cursor.next().getString("title");
                //String[] author = cursor.next().getList("author", String);
                //bookList.add(new Book(1, title, ));
            }
        } finally {
            cursor.close();
        }
        return bookList;
    }

    @Override
    public List<Book> loadBooksByISBN(String ISBN) {
        return List.of();
    }

    @Override
    public List<Book> loadBooksByTitle(String Title) {
        return List.of();
    }

    @Override
    public List<Book> loadBooksByGenre(String Genre) {
        return List.of();
    }

    @Override
    public List<Book> loadBooksByAuthor(String authorName) {
        return List.of();
    }

    @Override
    public List<Review> loadReviews(String isbn) {
        return List.of();
    }

    @Override
    public List<Author> loadAuthors() {
        return List.of();
    }

    @Override
    public User createAccount(String userName, String email, String password) {
        return null;
    }

    @Override
    public User loginAccount(String mail, String password) {
        return null;
    }

    @Override
    public void insertReview(Review review, String isbn) {

    }

    @Override
    public void insertAuthor(int userID, Author author) {

    }
}
