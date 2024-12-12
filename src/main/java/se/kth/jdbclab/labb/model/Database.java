package se.kth.jdbclab.labb.model;

import javafx.collections.FXCollections;
import javafx.scene.control.Alert;
import javafx.scene.control.Dialog;

import javax.swing.*;
import java.sql.*;
import java.util.List;
import java.util.Objects;

/**
 * Handles database operations for the application, including CRUD operations on books,
 * authors, reviews, and user accounts.
 */
public class Database implements IDatabase {
    private Connection connection;
    private Alert alertError;
    String query;

    /**
     * Establishes a connection to the MySQL database and initializes error alerts.
     */
    public Database() {
        alertError = new Alert(Alert.AlertType.ERROR);
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/bookvault", "root", "password");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads books from the database based on the specified query.
     * @param query The SQL query to execute.
     * @return A list of books retrieved from the database.
     */
    private List<Book> loader(String query) {
        List<Book> bookList = FXCollections.observableArrayList();
        try {
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                while (rs.next()) {
                    Book newBook;
                    boolean found = false;
                    String isbn = rs.getString("T_Book.ISBN");
                    String title = rs.getString("T_Book.Title");
                    String authorName = rs.getString("T_Author.AuthorName");
                    int authorId = rs.getInt("T_Author.AuthorId");
                    String genre = rs.getString("T_Book_Genre.Genre");
                    Review grade = new Review(rs.getInt("T_Grade.gradeID"), rs.getInt("T_Grade.grade"), rs.getString("T_Grade.GradeText"), rs.getDate("T_Grade.GradeDate"), rs.getString("T_User.userName"));
                    for (Book book : bookList) {
                        if (book.getIsbn().equals(isbn)) {
                            if(!book.getGenreNames().contains(genre))
                                book.getGenre().add(new Genre(genre));
                            else if (!book.getAuthorNames().contains(authorName))
                                book.getAuthors().add(new Author(authorId, authorName));
                            else{
                                book.getReviews().add(grade);
                            }
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        newBook = new Book(isbn, title);
                        newBook.getAuthors().add(new Author(authorId, authorName));
                        newBook.getGenre().add(new Genre(genre));
                        newBook.getReviews().add(grade);
                        bookList.add(newBook);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookList;
    }

    /**
     * Retrieves all books from the database.
     * @return A list of all books in the database.
     */
    @Override
    public List<Book> loadBooks() {
        return loader("SELECT  T_Book.ISBN,  T_Book.Title, T_Author.AuthorID, T_Author.AuthorName, T_book_genre.genre, T_Grade.gradeID, T_Grade.grade, T_Grade.gradeText, T_Grade.gradeDate, T_User.userName\n" +
                "FROM T_Book\n" +
                "JOIN T_Book_Author ON T_Book.ISBN = T_Book_Author.ISBN\n" +
                "JOIN T_Author ON T_Book_Author.AuthorID = T_Author.AuthorID\n" +
                "JOIN T_Book_Genre ON T_Book.ISBN = T_Book_Genre.ISBN\n" +
                "LEFT JOIN T_Grade ON T_Book.ISBN = T_Grade.ISBN\n" +
                "LEFT JOIN T_User ON T_User.userID = T_Grade.userID;");
    }

    /**
     * Retrieves books from the database based on the specified search criteria and value.
     * @param criteria The field to search by (e.g., "ID", "Title", "Author").
     * @param value The search value.
     * @return A list of books matching the criteria.
     */
    @Override
    public List<Book> loadBooks(String criteria, String value){
        String subQuery = switch (criteria) {
            case "ID" -> "T_Book.ISBN";
            case "Title" -> "T_Book.Title";
            case "Author" -> "T_Author.AuthorName";
            case null, default -> "T_Book_Genre.Genre";
        };
        return loader("SELECT  T_Book.ISBN,  T_Book.Title, T_Author.AuthorID, T_Author.AuthorName, T_book_genre.genre, T_Grade.gradeID, T_Grade.grade, T_Grade.gradeText, T_Grade.gradeDate, T_User.userName\n" +
                "FROM T_Book\n" +
                "JOIN T_Book_Author ON T_Book.ISBN = T_Book_Author.ISBN\n" +
                "JOIN T_Author ON T_Book_Author.AuthorID = T_Author.AuthorID\n" +
                "JOIN T_Book_Genre ON T_Book.ISBN = T_Book_Genre.ISBN\n" +
                "LEFT JOIN T_Grade ON T_Book.ISBN = T_Grade.ISBN\n" +
                "LEFT JOIN T_User ON T_User.userID = T_Grade.userID\n " +
                "WHERE " + subQuery + " LIKE '%" + value + "%';");
    }

    /**
     * Retrieves all authors from the database.
     * @return A list of all authors in the database.
     */
    @Override
    public List<Author> loadAuthors() {
        List<Author> authorList = FXCollections.observableArrayList();
        query = "SELECT  authorID, authorName FROM T_Author;";

        try (ResultSet rs = connection.createStatement().executeQuery(query)) {
            while (rs.next()) {
                authorList.add(new Author(rs.getInt("T_Author.authorID"), rs.getString("T_Author.authorName")));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return authorList;
    }



    /**
     * Inserts or removes a book in the database.
     * @param mode False to insert a book, true to remove a book.
     * @param userID The ID of the user performing the operation.
     * @param selectedBook The book to insert or remove.
     */
    @Override
    public void manageBook(boolean mode, int userID, Book selectedBook) {
        if (selectedBook != null) {
            String[] queries;
            if(mode){
                queries = new String[]{"DELETE FROM T_Book WHERE ISBN = ?", "DELETE FROM T_Book_Author WHERE ISBN = ?", "DELETE FROM T_Book_Genre WHERE ISBN = ?", "INSERT INTO T_BookLog (logISBN, userID, logType) VALUES (?, ?, 'remove')"};
                try {
                    connection.setAutoCommit(false);
                    try (PreparedStatement stmt = connection.prepareStatement(queries[1])) {
                        stmt.setString(1, selectedBook.getIsbn());
                        stmt.executeUpdate();
                    }

                    try (PreparedStatement stmt = connection.prepareStatement(queries[2])) {
                        stmt.setString(1, selectedBook.getIsbn());
                        stmt.executeUpdate();
                    }
                    try (PreparedStatement stmt = connection.prepareStatement(queries[3])) {
                        stmt.setString(1, selectedBook.getIsbn());
                        stmt.setInt(2, userID);
                        stmt.executeUpdate();
                    }

                    try (PreparedStatement stmt = connection.prepareStatement(queries[0])) {
                        stmt.setString(1, selectedBook.getIsbn());
                        stmt.executeUpdate();
                    }
                    connection.commit();
                } catch (SQLException e) {
                    // Rollback in case of an error
                    try {
                        connection.rollback();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                    e.printStackTrace();
                } finally {
                    // Reset auto-commit to true after the transaction
                    try {
                        connection.setAutoCommit(true);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
            else {
                queries = new String[]{"INSERT INTO T_Book (ISBN, Title) VALUES (?, ?)", "INSERT INTO T_Book_Author (ISBN, AuthorID) VALUES (?, ?)", "INSERT INTO T_Book_Genre (ISBN, Genre) VALUES (?, ?)", "INSERT INTO T_BookLog (logISBN, userID, logType) VALUES (?, ?, 'add')"};
                try {
                    connection.setAutoCommit(false);
                    try (PreparedStatement stmt = connection.prepareStatement(queries[0])) {
                        stmt.setString(1, selectedBook.getIsbn());
                        stmt.setString(2, selectedBook.getTitle());
                        stmt.executeUpdate();
                    }

                    try (PreparedStatement stmt = connection.prepareStatement(queries[1])) {
                        for(Author author : selectedBook.getAuthors()){
                            stmt.setString(1, selectedBook.getIsbn());
                            stmt.setInt(2, author.getAuthorID());
                            stmt.executeUpdate();
                        }
                    }

                    try (PreparedStatement stmt = connection.prepareStatement(queries[2])) {
                        for(Genre genre : selectedBook.getGenre()){
                            stmt.setString(1, selectedBook.getIsbn());
                            stmt.setString(2, genre.getGenre());
                            stmt.executeUpdate();
                        }
                    }
                    try (PreparedStatement stmt = connection.prepareStatement(queries[3])) {
                        stmt.setString(1, selectedBook.getIsbn());
                        stmt.setInt(2, userID);
                        stmt.executeUpdate();
                    }
                    connection.commit();
                } catch (SQLException e) {
                    // Rollback in case of an error
                    try {
                        connection.rollback();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                    e.printStackTrace();
                } finally {
                    // Reset auto-commit to true after the transaction
                    try {
                        connection.setAutoCommit(true);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }

    /**
     * Retrieves all reviews for a specific book by its ISBN.
     * @param isbn The ISBN of the book.
     * @return A list of reviews for the book.
     */
    @Override
    public List<Review> loadReviews(String isbn) {
        List<Review> reviewList = FXCollections.observableArrayList();
        query = "SELECT  T_Grade.gradeID,  T_Grade.grade, T_Grade.gradeText, T_Grade.gradeDate, T_User.userName\n" +
                "FROM T_Grade\n" +
                "LEFT JOIN T_User ON T_User.userID = T_Grade.userID\n" +
                "WHERE ISBN = "+ isbn +";";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                int gradeID = rs.getInt("T_Grade.gradeID");
                int grade = rs.getInt("T_Grade.grade");
                String text = rs.getString("T_Grade.gradeText");
                String username = rs.getString("T_User.userName");
                Date date = rs.getDate("T_Grade.gradeDate");

                reviewList.add(new Review(gradeID, grade, text, date, username));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return reviewList;
    }

    /**
     * Creates a new user account in the database.
     * @param userName The username of the new account.
     * @param email The email of the new account.
     * @param password The password of the new account.
     * @return The created user object or null if creation fails.
     */
    @Override
    public User createAccount(String userName, String email, String password) {
        try {

            String checkQuery = "SELECT 1 FROM T_User WHERE email = ?";
            try (PreparedStatement checkStmt = connection.prepareStatement(checkQuery)) {
                checkStmt.setString(1, email);
                if(Objects.equals(email, "") || Objects.equals(userName, "") || Objects.equals(password, "")){
                    alertError.setContentText("Invalid input.");
                    alertError.show();
                    return null;
                } else {
                    try (ResultSet rs = checkStmt.executeQuery()) {
                        if (rs.next()) {
                            alertError.setContentText("This email is already registered: " + email);
                            alertError.show();
                            return null;
                        }
                    }
                }
            }

            // Insert the new user
            String insertQuery = "INSERT INTO T_User (userName, email, userPassword) VALUES (?, ?, ?)";
            try (PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) {
                insertStmt.setString(1, userName);
                insertStmt.setString(2, email);
                insertStmt.setString(3, password);
                insertStmt.executeUpdate();
                try (ResultSet generatedKeys = insertStmt.getGeneratedKeys()) {
                    int generatedId = generatedKeys.getInt(1);
                    System.out.println("User registered successfully!");

                    return new User(generatedId, userName, email, password);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Logs in a user by checking the provided email and password against the database.
     *
     * @param mail     the email address of the user
     * @param password the password of the user
     * @return a User object if login is successful, null otherwise
     */
    @Override
    public User loginAccount(String mail, String password){
        query = "SELECT userName, userID FROM T_User WHERE email = ? AND userPassword = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, mail);
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    rs.getString("userName");
                    System.out.println("Login successful!" + rs.getInt("userID"));
                    return new User(rs.getInt("userID"), rs.getString("userName"), mail, password);
                } else {
                    alertError.setContentText("Invalid email or password");
                    alertError.show();
                    return null;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Inserts a review into the database for a specific book.
     *
     * @param review the Review object containing the review details
     * @param isbn   the ISBN of the book being reviewed
     */
    @Override
    public void insertReview(Review review, String isbn) {
        try {
            query = "INSERT INTO T_Grade (ISBN, userID, grade, gradeText, gradeDate) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, isbn);
                stmt.setInt(2, review.getUserID());
                stmt.setInt(3, review.getGrade());
                stmt.setString(4, review.getText());
                stmt.setDate(5, new java.sql.Date(review.getGrade_date().getTime()));
                stmt.executeUpdate();
            }
        } catch (SQLIntegrityConstraintViolationException e){
            JOptionPane.showMessageDialog(null, "You have already reviewed this book.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Inserts a new author into the database and links it to the user who added it.
     *
     * @param userID the ID of the user adding the author
     * @param author the Author object containing the author's details
     */
    @Override
    public void insertAuthor(int userID, Author author) {
        Date DOB = null, DOD = null;
        if(author.getDateOfBirth() != null)
            DOB = new java.sql.Date(author.getDateOfBirth().getTime());
        if(author.getDateOfDeath() != null)
            DOD = new java.sql.Date(author.getDateOfDeath().getTime());
        try {
            connection.setAutoCommit(false);
            query = "INSERT INTO T_Author (authorName, DOB, DOD) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, author.getName());
                if(DOB == null)
                    stmt.setNull(2, 4);
                else
                    stmt.setDate(2, DOB);
                if(DOD == null)
                    stmt.setNull(3, 4);
                else
                    stmt.setDate(3, DOD);
                stmt.executeUpdate();
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int authorID = generatedKeys.getInt(1);

                        query = "INSERT INTO T_addedauthor (authorID, userID, addedAuthorDate) VALUES (?, ?, ?)";
                        try (PreparedStatement stmt2 = connection.prepareStatement(query)) {
                            stmt2.setInt(1, authorID);
                            stmt2.setInt(2, userID);
                            stmt2.setDate(3, new java.sql.Date(System.currentTimeMillis()));
                            stmt2.executeUpdate();
                        }
                    } else {
                        System.out.println("Failed to retrieve author ID.");
                    }
                }
            }
            connection.commit();

        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
