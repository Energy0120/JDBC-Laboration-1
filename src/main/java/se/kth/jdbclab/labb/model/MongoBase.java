package se.kth.jdbclab.labb.model;

import com.mongodb.ConnectionString;
import com.mongodb.MongoException;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.DeleteResult;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import org.bson.Document;
import org.bson.conversions.Bson;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class MongoBase implements IDatabase {
    private MongoClient mongoClient;
    private Alert alertError;

    public MongoBase() {
        alertError = new Alert(Alert.AlertType.ERROR);
        try {
            mongoClient = MongoClients.create(new ConnectionString("mongodb://localhost:3306/?retryWrites=false"));
        } catch (MongoException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void manageBook(boolean mode, int userID, Book book) {
        if(book != null){
            MongoDatabase db = mongoClient.getDatabase("bookvault");
            MongoCollection<Document> books = db.getCollection("book");
            MongoCollection<Document> bookLogs = db.getCollection("log");
            if(mode){
                Bson filter = Filters.eq("isbn", book.getIsbn());
                DeleteResult deleteResult = books.deleteOne(filter);
                if (deleteResult.getDeletedCount() > 0) {
                    Document logDoc = new Document("userid", userID)
                            .append("isbn", book.getIsbn())
                            .append("type", "delete");
                    bookLogs.insertOne(logDoc);
                    System.out.println("Book deleted successfully!");
                }
            } else {
                List<String> authorNames = new ArrayList<>();
                List<String> genreNames = new ArrayList<>();
                for (Author author : book.getAuthors())
                    authorNames.add(author.getName());
                for (Genre genre : book.getGenre())
                    genreNames.add(genre.getGenre());
                Document bookDoc = new Document("title", book.getTitle())
                        .append("author", authorNames)
                        .append("genre", genreNames)
                        .append("isbn", book.getIsbn());

                Document logDoc = new Document("userid", userID)
                        .append("isbn", book.getIsbn())
                        .append("type", "add");

                books.insertOne(bookDoc);
                bookLogs.insertOne(logDoc);
                System.out.println("Book added successfully!");
            }
        }

    }

    private List<Book> loader(List<Book> bookList, Document document){
        List<Author> authorList = new ArrayList<>();
        List<Genre> genreList = new ArrayList<>();
        String isbn = document.getString("isbn");
        String title = document.getString("title");
        List<String> author = (List<String>) document.get("author");
        List<String> genre = (List<String>) document.get("genre");
        for(String s : author)
            authorList.add(new Author(s));
        for(String g : genre)
            genreList.add(new Genre(g));
        Book newBook = new Book(isbn, title, authorList, genreList);
        bookList.add(newBook);
        return bookList;
    }

    @Override
    public List<Book> loadBooks() {
        List<Book> bookList = new ArrayList<>();
        MongoDatabase db = mongoClient.getDatabase("bookvault");
        MongoCollection<Document> books = db.getCollection("book");
        try (MongoCursor<Document> cursor = books.find().iterator()) {
            cursor.forEachRemaining(document -> {
                loader(bookList, document);
            });
        }
        for(Book book : bookList){
            Bson filter = Filters.eq("isbn", book.getIsbn());
            db.getCollection("review").find(filter).forEach(review -> {
                if(book.getReviews() != null)
                    book.getReviews().add(new Review(review.getInteger("grade")));
            });
        }
        return bookList;
    }

    @Override
    public List<Book> loadBooksByISBN(String ISBN) {
        List<Book> bookList = new ArrayList<>();
        MongoDatabase db = mongoClient.getDatabase("bookvault");
        MongoCollection<Document> books = db.getCollection("book");
        Bson filter = Filters.regex("isbn", ".*" + Pattern.quote(ISBN) + ".*", "i");
        books.find(filter).forEach(document -> {
            loader(bookList, document);
        });
        return bookList;
    }


    @Override
    public List<Book> loadBooksByTitle(String Title) {
        List<Book> bookList = new ArrayList<>();
        MongoDatabase db = mongoClient.getDatabase("bookvault");
        MongoCollection<Document> books = db.getCollection("book");
        Bson filter = Filters.regex("title", ".*" + Pattern.quote(Title) + ".*", "i");
        books.find(filter).forEach(document -> {
            loader(bookList, document);
        });
        return bookList;
    }

    @Override
    public List<Book> loadBooksByGenre(String Genre) {
        List<Book> bookList = new ArrayList<>();
        MongoDatabase db = mongoClient.getDatabase("bookvault");
        MongoCollection<Document> books = db.getCollection("book");
        Bson filter = Filters.regex("genre", ".*" + Pattern.quote(Genre) + ".*", "i");
        books.find(filter).forEach(document -> {
            loader(bookList, document);
        });
        return bookList;
    }

    @Override
    public List<Book> loadBooksByAuthor(String authorName) {
        List<Book> bookList = new ArrayList<>();
        MongoDatabase db = mongoClient.getDatabase("bookvault");
        MongoCollection<Document> books = db.getCollection("book");
        Bson filter = Filters.regex("author", ".*" + Pattern.quote(authorName) + ".*", "i");
        books.find(filter).forEach(document -> {
            loader(bookList, document);
        });
        return bookList;
    }

    @Override
    public List<Review> loadReviews(String isbn) {
        List<Review> reviewList = new ArrayList<>();
        MongoDatabase db = mongoClient.getDatabase("bookvault");
        MongoCollection<Document> reviews = db.getCollection("review");
        Bson filter = Filters.eq("isbn", isbn);
        reviews.find(filter).forEach(document -> {
            MongoCollection<Document> users = db.getCollection("user");
            Bson find = Filters.eq("userid", document.getInteger("userid"));
            String username = users.find(find).first().getString("name");
            reviewList.add(new Review(document.getInteger("grade"), document.getString("text"), document.getDate("date"), username));
        });
        return reviewList;
    }

    @Override
    public List<Author> loadAuthors() {
        List<Author> authorList = new ArrayList<>();
        MongoDatabase db = mongoClient.getDatabase("bookvault");
        MongoCollection<Document> authors = db.getCollection("author");
        try (MongoCursor<Document> cursor = authors.find().iterator()) {
            cursor.forEachRemaining(document -> {
                authorList.add(new Author(document.getString("name"), document.getDate("dob"), document.getDate("dod")));
            });
        }
        return authorList;
    }

    @Override
    public User createAccount(String userName, String email, String password) {
        MongoDatabase db = mongoClient.getDatabase("bookvault");
        MongoCollection<Document> users = db.getCollection("user");
        MongoCollection<Document> counters = db.getCollection("counters");
        Bson filter = Filters.eq("email", email);
        if(users.find(filter).first() != null) {
            alertError.setContentText("The email is already registered!");
            alertError.show();
            return null;
        }
        Document counterDoc = counters.find(Filters.eq("_id", "users")).first();
        int userID = counterDoc.getInteger("sequence_value");
        counters.updateOne(Filters.eq("_id", "users"),
                Updates.set("sequence_value", counterDoc.getInteger("sequence_value") + 1));

        Document user = new Document("name", userName)
                .append("email", email)
                .append("password", password)
                .append("userid", userID);
        users.insertOne(user);

        return new User(userID, userName, email, password);
    }

    @Override
    public User loginAccount(String email, String password) {
        MongoDatabase db = mongoClient.getDatabase("bookvault");
        MongoCollection<Document> users = db.getCollection("user");
        Bson filter = Filters.and( Filters.eq("email", email), Filters.eq("password", password));
        Document document = users.find(filter).first();

        if (document != null) {
            int userID = document.getInteger("userid");
            String userName = document.getString("name");
            System.out.println("Login successful! UserID: " + userID);
            return new User(userID, userName, email, password);
        } else {
            alertError.setContentText("Invalid email or password");
            alertError.show();
            return null;
        }
    }

    @Override
    public void insertReview(Review review, String isbn) {
        MongoDatabase db = mongoClient.getDatabase("bookvault");
        MongoCollection<Document> reviews = db.getCollection("review");
        if(reviews.find(Filters.eq("userid", review.getUserID())).first() != null) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    alertError.setContentText("You have already reviewed this book.");
                    alertError.show();
                }
            });

            return;
        }
        MongoCollection<Document> counters = db.getCollection("counters");

        Document counterDoc = counters.find(Filters.eq("_id", "review")).first();
        if (counterDoc == null) {
            counterDoc = new Document("_id", "review")
                    .append("sequence_value", 1);
            counters.insertOne(counterDoc);
        }

        counters.updateOne(Filters.eq("_id", "review"),
                Updates.set("sequence_value", counterDoc.getInteger("sequence_value") + 1));

        Document authorDoc = new Document("userid", review.getUserID())
                .append("isbn", isbn)
                .append("grade", review.getGrade())
                .append("text", review.getText())
                .append("date", review.getGrade_date());

        reviews.insertOne(authorDoc);
    }

    @Override
    public void insertAuthor(int userID, Author author) {
        MongoDatabase db = mongoClient.getDatabase("bookvault");
        MongoCollection<Document> authors = db.getCollection("author");
        MongoCollection<Document> counters = db.getCollection("counters");

        Document counterDoc = counters.find(Filters.eq("_id", "author")).first();
        if (counterDoc == null) {
            counterDoc = new Document("_id", "author")
                    .append("sequence_value", 1);
            counters.insertOne(counterDoc);
        }

        counters.updateOne(Filters.eq("_id", "author"),
                Updates.set("sequence_value", counterDoc.getInteger("sequence_value") + 1));

        Document authorDoc = new Document("name", author.getName())
                .append("dob", author.getDateOfBirth())
                .append("dod", author.getDateOfDeath());

        authors.insertOne(authorDoc);
    }
}
