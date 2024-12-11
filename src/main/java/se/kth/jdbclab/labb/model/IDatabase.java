package se.kth.jdbclab.labb.model;

import java.sql.SQLException;
import java.util.List;

public interface IDatabase {
    void insertBook(int userID, Book book) throws SQLException;
    void deleteBook(Book book) throws SQLException;
    List<Book> loadBooks() throws SQLException;
    List<Book> loadBooks(String criteria, String value) throws SQLException;
    List<Review> loadReviews(String isbn) throws SQLException;
    User createAccount(String userName, String email, String password) throws SQLException;
    User loginAccount(String mail, String password) throws SQLException;
    void insertReview(Review review, String isbn) throws SQLException;
}
