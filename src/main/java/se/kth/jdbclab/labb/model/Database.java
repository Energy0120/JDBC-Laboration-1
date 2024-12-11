package se.kth.jdbclab.labb.model;

import javafx.collections.FXCollections;
import javafx.scene.control.Alert;
import javafx.scene.control.Dialog;

import javax.swing.*;
import java.sql.*;
import java.util.List;
import java.util.Objects;

public class Database implements IDatabase {
    private Connection connection;
    private Alert alertError;

    public Database() {
        alertError = new Alert(Alert.AlertType.ERROR);
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/bookvault", "root", "password");
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }

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
                    String genre = rs.getString("T_Book_Genre.Genre");
                    Review grade = new Review(rs.getInt("T_Grade.gradeID"), rs.getInt("T_Grade.grade"), rs.getString("T_Grade.GradeText"), rs.getDate("T_Grade.GradeDate"), rs.getString("T_User.userName"));
                    for (Book book : bookList) {
                        if (book.getIsbn().equals(isbn)) {
                            if(!book.getGenreNames().contains(genre))
                                book.getGenre().add(new Genre(genre));
                            else if (!book.getAuthorNames().contains(authorName))
                                book.getAuthors().add(new Author(authorName));
                            else{
                                book.getReviews().add(grade);
                            }
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        newBook = new Book(isbn, title);
                        newBook.getAuthors().add(new Author(authorName));
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

    @Override
    public List<Book> loadBooks() {
        return loader("SELECT  T_Book.ISBN,  T_Book.Title, T_Author.AuthorName, T_book_genre.genre, T_Grade.gradeID, T_Grade.grade, T_Grade.gradeText, T_Grade.gradeDate, T_User.userName\n" +
                "FROM T_Book\n" +
                "JOIN T_Book_Author ON T_Book.ISBN = T_Book_Author.ISBN\n" +
                "JOIN T_Author ON T_Book_Author.AuthorID = T_Author.AuthorID\n" +
                "JOIN T_Book_Genre ON T_Book.ISBN = T_Book_Genre.ISBN\n" +
                "LEFT JOIN T_Grade ON T_Book.ISBN = T_Grade.ISBN\n" +
                "LEFT JOIN T_User ON T_User.userID = T_Grade.userID;");
    }

    @Override
    public List<Book> loadBooks(String criteria, String value){
        String subQuery = switch (criteria) {
            case "ID" -> "T_Book.ISBN";
            case "Name" -> "T_Book.Title";
            case "Author" -> "T_Author.AuthorName";
            case null, default -> "T_Book_Genre.Genre";
        };
        return loader("SELECT  T_Book.ISBN,  T_Book.Title, T_Author.AuthorName, T_book_genre.genre, T_Grade.gradeID, T_Grade.grade, T_Grade.gradeText, T_Grade.gradeDate, T_User.userName\n" +
                "FROM T_Book\n" +
                "JOIN T_Book_Author ON T_Book.ISBN = T_Book_Author.ISBN\n" +
                "JOIN T_Author ON T_Book_Author.AuthorID = T_Author.AuthorID\n" +
                "JOIN T_Book_Genre ON T_Book.ISBN = T_Book_Genre.ISBN\n" +
                "LEFT JOIN T_Grade ON T_Book.ISBN = T_Grade.ISBN\n" +
                "LEFT JOIN T_User ON T_User.userID = T_Grade.userID\n " +
                "WHERE " + subQuery + " = '" + value + "';");
    }


    @Override
    public void deleteBook(Book selectedBook) {
        if (selectedBook != null) {
            try {
                String query = "DELETE FROM T_Book WHERE ISBN = ?";
                try (PreparedStatement stmt = connection.prepareStatement(query)) {
                    stmt.setString(1, selectedBook.getIsbn());
                    stmt.executeUpdate();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void insertBook(int userID, Book book) {
            try {
                String query = "INSERT INTO T_Book (ISBN, Title) VALUES (?, ?)";
                try (PreparedStatement stmt = connection.prepareStatement(query)) {
                    stmt.setString(1, book.getIsbn());
                    stmt.setString(2, book.getTitle());
                    stmt.executeUpdate();
                }

                query = "INSERT INTO T_Book_Author (ISBN, AuthorID) VALUES (?, ?)";
                try (PreparedStatement stmt = connection.prepareStatement(query)) {
                    for(Author author : book.getAuthors()){
                        stmt.setString(1, book.getIsbn());
                        stmt.setInt(2, author.getAuthorID());
                    }
                    stmt.executeUpdate();
                }

                query = "INSERT INTO T_Book_Genre (ISBN, Genre) VALUES (?, ?)";
                try (PreparedStatement stmt = connection.prepareStatement(query)) {
                    for(Genre genre : book.getGenre()){
                        stmt.setString(1, book.getIsbn());
                        stmt.setString(2, genre.getGenre());
                    }
                    stmt.executeUpdate();
                }

                query = "INSERT INTO T_BookLog (ISBN, userID) VALUES (?, ?, 'add')";
                try (PreparedStatement stmt = connection.prepareStatement(query)) {
                    stmt.setString(1, book.getIsbn());
                    stmt.setInt(2, userID);
                    stmt.executeUpdate();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

    }

    @Override
    public List<Review> loadReviews(String isbn) {
        List<Review> reviewList = FXCollections.observableArrayList();
        String query = "SELECT  T_Grade.gradeID,  T_Grade.grade, T_Grade.gradeText, T_Grade.gradeDate, T_User.userName\n" +
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

    @Override
    public User loginAccount(String mail, String password){
        String query = "SELECT userName, userID FROM T_User WHERE email = ? AND userPassword = ?";
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


    @Override
    public void insertReview(Review review, String isbn) {
        try {
            String query = "INSERT INTO T_Grade (ISBN, userID, grade, gradeText, gradeDate) VALUES (?, ?, ?, ?, ?)";
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
}
