package se.kth.jdbclab.labb.controller;
import se.kth.jdbclab.labb.model.*;
import java.util.Date;
import java.util.List;

/**
 * The MainController class manages the operations related to books, users, and reviews.
 * It interacts with the database to load and manipulate data related to books, reviews, and user accounts.
 */
public class MainController {
    private User currentUser;
    Database database;

    /**
     * Constructs a MainController instance with the provided database connection.
     *
     * @param database The database instance used for data operations.
     */
    public MainController(Database database) {
        this.database = database;
    }

    /**
     * Adds a new book to the database.
     *
     * @param book The book to be added.
     */
    public void addBook(Book book) {
        new Thread(() -> database.manageBook(false, currentUser.getUserID(), book)).start();
    }

    /**
     * Removes a book from the database.
     *
     * @param book The book to be removed.
     */
    public void removeBook(Book book) {
        new Thread(() -> database.manageBook(true, currentUser.getUserID(), book)).start();
    }

    /**
     * Adds a review for a book with a specified grade, text, and date.
     *
     * @param isbn The ISBN of the book being reviewed.
     * @param grade The grade given in the review.
     * @param gradeText The text of the review.
     * @param gradeDate The date when the review was written.
     */
    public void addReview(String isbn, int grade, String gradeText, Date gradeDate) {
        new Thread(() -> database.insertReview(new Review(grade, gradeText, gradeDate, currentUser.getUserID()), isbn)).start();
    }

    /**
     * Adds a new Author to the database.
     *
     * @param author The author to be added.
     */
    public void addAuthor(Author author) {
        new Thread(() -> database.insertAuthor(currentUser.getUserID(), author)).start();
    }

    /**
     * Loads all books from the database.
     *
     * @return A list of all books.
     */
    public List<Book> loadBooks(){
        return database.loadBooks();
    }

    /**
     * Loads all reviews for a specific book based on its ISBN.
     *
     * @param isbn The ISBN of the book whose reviews are to be loaded.
     * @return A list of reviews for the specified book.
     */
    public List<Review> loadReviews(String isbn) {
        return database.loadReviews(isbn);
    }

    /**
     * Authenticates the user by checking the provided email and password.
     *
     * @param mail The email address of the user.
     * @param password The password of the user.
     * @return The name of the authenticated user if successful, or null if authentication fails.
     */
    public String login(String mail, String password) {
        currentUser = database.loginAccount(mail, password);
        if(currentUser == null) {
            return null;
        }
        return currentUser.getName();
    }

    /**
     * Creates a new user account with the specified name, email, and password.
     *
     * @param name The name of the user.
     * @param mail The email address of the user.
     * @param pass The password for the new account.
     * @return The name of the newly created user if successful, or null if account creation fails.
     */
    public String createAccount(String name, String mail, String pass) {
        User user = database.createAccount(name, mail, pass);
        if(user != null){
            currentUser = user;
            return currentUser.getName();
        }
        return null;
    }

    /**
     * Loads books from the database based on specific search criteria.
     *
     * @param criteria The search criteria (e.g., "Title", "ID", "Author", "Genre").
     * @param value The value to search for within the selected criteria.
     * @return A list of books matching the search criteria and value.
     */
    public List<Book> loadBooksByCriteria(String criteria, String value) {
        switch (criteria) {
            case "ID" -> {
                return database.loadBooksByISBN(value);
            }
            case "Title" -> {
                return database.loadBooksByTitle(value);
            }
            case "Author" -> {
                return database.loadBooksByAuthor(value);
            }
            case null, default -> {
                return database.loadBooksByGenre(value);
            }
        }
    }

    /**
     * Loads all authors from the database.
     *
     * @return A list of authors.
     */
    public List<Author> loadAuthors(){
        return database.loadAuthors();
    }
}
