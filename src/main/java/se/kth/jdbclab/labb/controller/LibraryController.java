package se.kth.jdbclab.labb.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import se.kth.jdbclab.labb.model.*;
import se.kth.jdbclab.labb.view.MainView;

import javax.swing.*;
import java.sql.*;
import java.util.List;

public class LibraryController {
    private MainView view;
    private IDatabase database;

    public LibraryController(MainView view, IDatabase database) {
        this.view = view;
        this.database = database;

        // Refresh books from database
        try {
            List<Book> books = database.loadBooks();
            view.refreshLibrary(books);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Button actions
        view.getAddButton().setOnAction(e -> addBook());
        view.getDeleteButton().setOnAction(e -> deleteBook());
    }

    private void addBook() {
        try {
            Book newBook = new Book("1234", "New Book");
            database.insertBook(newBook);
            //view.refreshLibrary(database.loadBooks()); // Example, refresh with new book
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteBook() {
        Book selectedBook = view.getLibraryTable().getSelectionModel().getSelectedItem();
        if (selectedBook != null) {
            try {
                database.deleteBook(selectedBook);
                //view.refreshLibrary(database.loadBooks()); // Refresh book list after deletion
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}


/*
public class LibraryController {
    private final MainView view;
    private final Connection connection;
    private ObservableList<Book> bookList;
    private Book selectedBook;

    public LibraryController(MainController main, MainView view, Connection connection) {
        this.view = view;
        this.connection = connection;
        initializeLibrary(main);
    }

    public void initializeLibrary(MainController main) {
        loadBooks();
        view.getAddButton().setOnAction(e -> addBook());
        view.getDeleteButton().setOnAction(e -> deleteBook());
        view.getViewButton().setOnAction(e -> main.switchToBook());
    }

    private void loadBooks() {
        try {
            String query1 = "SELECT  T_Book.ISBN,  T_Book.Title, T_Author.AuthorName, T_book_genre.genre, T_Grade.gradeID, T_Grade.grade, T_Grade.gradeText, T_Grade.gradeDate, T_User.userName\n" +
                    "FROM T_Book\n" +
                    "JOIN T_Book_Author ON T_Book.ISBN = T_Book_Author.ISBN\n" +
                    "JOIN T_Author ON T_Book_Author.AuthorID = T_Author.AuthorID\n" +
                    "JOIN T_Book_Genre ON T_Book.ISBN = T_Book_Genre.ISBN\n" +
                    "LEFT JOIN T_Grade ON T_Book.ISBN = T_Grade.ISBN\n"+
                    "LEFT JOIN T_User ON T_User.userID = T_Grade.userID;";
            bookList = FXCollections.observableArrayList();
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
            view.getLibraryTable().setItems(bookList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addBook() {
        try {
            String isbn = JOptionPane.showInputDialog("What isbn??"); // Example data
            String title = "New Book";

            String query = "INSERT INTO T_Book (ISBN, Title) VALUES (?, ?)";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, isbn);
                stmt.setString(2, title);
                stmt.executeUpdate();

                bookList.add(new Book(isbn, title));
            }
        } catch (SQLIntegrityConstraintViolationException e){
          JOptionPane.showMessageDialog(null, "This ISBN is already registered in the database.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteBook() {
        Book selectedBook = view.getLibraryTable().getSelectionModel().getSelectedItem();
        if (selectedBook != null) {
            try {
                String query = "DELETE FROM T_Book WHERE ISBN = ?";
                try (PreparedStatement stmt = connection.prepareStatement(query)) {
                    stmt.setString(1, selectedBook.getIsbn());
                    stmt.executeUpdate();

                    bookList.remove(selectedBook);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
*/