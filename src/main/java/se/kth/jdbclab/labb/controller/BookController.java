package se.kth.jdbclab.labb.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import se.kth.jdbclab.labb.model.Author;
import se.kth.jdbclab.labb.model.Book;
import se.kth.jdbclab.labb.view.MainView;

import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookController {
    private MainView view;
    private Connection connection;
    private ObservableList<Book> bookList;

    public BookController(MainView view) {
        this.view = view;
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/bookvault", "root", "password");
            loadBooks();

            view.getAddButton().setOnAction(e -> addBook());
            view.getDeleteButton().setOnAction(e -> deleteBook());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadBooks() {
        try {
            String query1 = "SELECT  T_Book.ISBN,  T_Book.Title,  T_Author.AuthorName\n" +
                    "FROM  T_Book\n" +
                    "JOIN  T_Book_Author ON T_Book.ISBN = T_Book_Author.ISBN\n" +
                    "JOIN  T_Author ON T_Book_Author.AuthorID = T_Author.AuthorID;";
            bookList = FXCollections.observableArrayList();
            try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(query1)) {
                while (rs.next()) {
                    Book newBook;
                    boolean found = false;
                    String isbn = rs.getString("T_Book.ISBN");
                    String title = rs.getString("T_Book.Title");
                    String authorName = rs.getString("T_Author.AuthorName");
                    for (Book book : bookList) {
                        if (book.getIsbn().equals(isbn)) {
                            newBook = book;
                            newBook.getAuthors().add(new Author(authorName));
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        newBook = new Book(isbn, title);
                        newBook.getAuthors().add(new Author(authorName));
                        bookList.add(newBook);
                    }
                }
            }
            view.getBookTable().setItems(bookList);
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

                // Update the table
                bookList.add(new Book(isbn, title));
            }
        } catch (SQLIntegrityConstraintViolationException e){
          JOptionPane.showMessageDialog(null, "This ISBN is already registered in the database.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteBook() {
        Book selectedBook = view.getBookTable().getSelectionModel().getSelectedItem();
        if (selectedBook != null) {
            try {
                String query = "DELETE FROM T_Book WHERE ISBN = ?";
                try (PreparedStatement stmt = connection.prepareStatement(query)) {
                    stmt.setString(1, selectedBook.getIsbn());
                    stmt.executeUpdate();

                    // Update the table
                    bookList.remove(selectedBook);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
