package se.kth.jdbclab.labb.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import se.kth.jdbclab.labb.view.MainView;

import javax.swing.*;
import java.sql.*;
import java.util.List;

public class Database implements IDatabase {
    private Connection connection;

    public Database() {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/bookvault", "root", "password");
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Book> loadBooks() {
        List<Book> bookList = FXCollections.observableArrayList();
        try {
            String query1 = "SELECT  T_Book.ISBN,  T_Book.Title, T_Author.AuthorName, T_book_genre.genre, T_Grade.gradeID, T_Grade.grade, T_Grade.gradeText, T_Grade.gradeDate, T_User.userName\n" +
                    "FROM T_Book\n" +
                    "JOIN T_Book_Author ON T_Book.ISBN = T_Book_Author.ISBN\n" +
                    "JOIN T_Author ON T_Book_Author.AuthorID = T_Author.AuthorID\n" +
                    "JOIN T_Book_Genre ON T_Book.ISBN = T_Book_Genre.ISBN\n" +
                    "LEFT JOIN T_Grade ON T_Book.ISBN = T_Grade.ISBN\n"+
                    "LEFT JOIN T_User ON T_User.userID = T_Grade.userID;";
            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(query1)) {
                while (rs.next()) {
                    Book newBook;
                    boolean found = false;
                    String isbn = rs.getString("T_Book.ISBN");
                    String title = rs.getString("T_Book.Title");
                    String authorName = rs.getString("T_Author.AuthorName");
                    String genre = rs.getString("T_Book_Genre.Genre");
                    Review grade = new Review(rs.getInt("T_Grade.gradeID"), rs.getInt("T_Grade.grade"), rs.getString("T_Grade.GradeText"), rs.getDate("T_Grade.GradeDate"), new User(rs.getString("T_User.userName")));
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
    public void insertBook(Book book) {
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

                reviewList.add(new Review(gradeID, grade, text, date, new User(username)));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return reviewList;
    }

    @Override
    public void insertReview(Review review) throws SQLException {
        try {
            String isbn = JOptionPane.showInputDialog("What isbn??"); // Example data
            String title = "New Book";

            String query = "INSERT INTO T_Review (ISBN, Title) VALUES (?, ?)";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, isbn);
                stmt.setString(2, title);
                stmt.executeUpdate();
            }
        } catch (SQLIntegrityConstraintViolationException e){
            JOptionPane.showMessageDialog(null, "This ISBN is already registered in the database.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
