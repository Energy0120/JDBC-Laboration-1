package se.kth.jdbclab.labb.model;

import java.sql.SQLException;
import java.util.List;

/**
 * Interface defining the operations for interacting with the database.
 */
public interface IDatabase {

    /**
     * Manages book-related operations such as borrowing or returning a book.
     *
     * @param mode   {@code true} for borrowing a book, {@code false} for returning a book.
     * @param userID The ID of the user performing the operation.
     * @param book   The book to be borrowed or returned.
     * @throws SQLException If a database access error occurs.
     */
    void manageBook(boolean mode, int userID, Book book) ;

    /**
     * Loads all books from the database.
     *
     * @return A list of books available in the database.
     * @throws SQLException If a database access error occurs.
     */
    List<Book> loadBooks() ;

    /**
     * Loads books from the database based on a specific criteria.
     *
     * @return A list of books matching the criteria.
     * @throws SQLException If a database access error occurs.
     */
    List<Book> loadBooksByISBN(String ISBN) ;
    List<Book> loadBooksByTitle(String Title) ;
    List<Book> loadBooksByGenre(String Genre) ;
    List<Book> loadBooksByAuthor(String authorName) ;

    /**
     * Loads reviews for a specific book identified by its ISBN.
     *
     * @param isbn The ISBN of the book.
     * @return A list of reviews for the book.
     * @throws SQLException If a database access error occurs.
     */
    List<Review> loadReviews(String isbn) ;

    /**
     * Loads all authors from the database.
     *
     * @return A list of authors available in the database.
     * @throws SQLException If a database access error occurs.
     */
    List<Author> loadAuthors() ;

    /**
     * Creates a new user account in the database.
     *
     * @param userName The name of the user.
     * @param email    The email address of the user.
     * @param password The password for the account.
     * @return The created user object.
     * @throws SQLException If a database access error occurs.
     */
    User createAccount(String userName, String email, String password) ;

    /**
     * Authenticates a user by their email and password.
     *
     * @param mail     The email address of the user.
     * @param password The password of the user.
     * @return The authenticated user object.
     * @throws SQLException If a database access error occurs.
     */
    User loginAccount(String mail, String password) ;

    /**
     * Inserts a new review for a specific book into the database.
     *
     * @param review The review to be inserted.
     * @param isbn   The ISBN of the book being reviewed.
     * @throws SQLException If a database access error occurs.
     */
    void insertReview(Review review, String isbn) ;

    /**
     * Inserts a new author into the database, associated with a specific user.
     *
     * @param userID The ID of the user adding the author.
     * @param author The author to be inserted.
     * @throws SQLException If a database access error occurs.
     */
    void insertAuthor(int userID, Author author) ;
}
