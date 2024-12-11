package se.kth.jdbclab.labb.controller;

import se.kth.jdbclab.labb.model.Book;
import se.kth.jdbclab.labb.model.Database;
import se.kth.jdbclab.labb.model.Review;
import se.kth.jdbclab.labb.model.User;

import javax.swing.*;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class MainController {
    private User currentUser;
    Database database;

    public MainController(Database database) {
        this.database = database;
    }

    public List<Book> loadBooks(){
        return database.loadBooks();
    }

    public void addBook(Book book) {
        database.insertBook(currentUser.getUserID(), book);
    }

    public List<Review> loadReviews(String isbn) {
        return database.loadReviews(isbn);
    }

    public String login(String mail, String password) {
        currentUser = database.loginAccount(mail, password);
        if(currentUser == null) {
            return null;
        }
        return currentUser.getName();
    }

    public String createAccount(String name, String mail, String pass) {
        User user = database.createAccount(name, mail, pass);
        if(user != null){
            currentUser = user;
            return currentUser.getName();
        }
        return null;
    }

    public List<Book> loadBooksByCriteria(String criteria, String value) {
        return database.loadBooks(criteria, value);
    }

    public void addReview(String isbn, int grade, String gradeText, Date gradeDate) {
        database.insertReview(new Review(grade, gradeText, gradeDate, currentUser.getUserID()), isbn);
    }
}
