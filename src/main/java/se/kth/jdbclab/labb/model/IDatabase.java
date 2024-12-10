package se.kth.jdbclab.labb.model;

import java.sql.SQLException;
import java.util.List;

public interface IDatabase {
    void insertBook(Book book) throws SQLException;
    void deleteBook(Book book) throws SQLException;
    List<Book> loadBooks() throws SQLException;
    List<Review> loadReviews(String isbn) throws SQLException;
    void insertReview(Review review) throws SQLException;
}
