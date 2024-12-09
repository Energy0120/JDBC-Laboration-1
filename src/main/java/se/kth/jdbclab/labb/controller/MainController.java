package se.kth.jdbclab.labb.controller;

import javafx.stage.Stage;
import se.kth.jdbclab.labb.model.Book;
import se.kth.jdbclab.labb.model.Database;
import se.kth.jdbclab.labb.model.User;
import se.kth.jdbclab.labb.view.MainView;

import javax.swing.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MainController {
    private final MainView view;
    private Connection connection;
    private LibraryController libraryController;
    private BookController bookController;
    private User currentUser;
    private int loggedIn;    // 0 = Not Logged In, 1 = User, 2 = Manager.
    Database database;

    public MainController(Database db) {
        loggedIn = 0;
        database = db;
        this.view = view;
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/bookvault", "root", "password");
            switchToLibrary();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getLoggedIn() {
        return loggedIn;
    }

    public void switchToBook() {
        Book selectedBook = view.getLibraryTable().getSelectionModel().getSelectedItem();
        if(selectedBook != null) {
            view.makeBookTable();
            if(bookController == null)
                bookController = new BookController(this, view, connection);
            bookController.initializeBook(this, selectedBook);
        }
    }

    public void switchToLibrary(){
        view.makeLibraryTable();
        if (libraryController == null)
            libraryController = new LibraryController(this, view, connection);
        libraryController.initializeLibrary(this);
    }

    private void authenticate() {
        String userName = JOptionPane.showInputDialog("Please Type your username:");
        String password = JOptionPane.showInputDialog("Please Type your password:");

    }

    @FunctionalInterface
    private interface ThrowingConsumer<T> {
        void accept(T t) throws DatabaseException;
    }

    private <T> void threadedOperation(ThrowingConsumer<T> f, T x) throws DatabaseException
    {
        AtomicReference<DatabaseException> dbe = new AtomicReference<>();
        Thread t = new Thread(() -> {
            try { f.accept(x); }
            catch (DatabaseException e) { dbe.set(e); }
        });
        t.start();

        try { t.join(); }
        catch (Exception e) { throw new DatabaseException("Threading error", e); }

        if (dbe.get() != null)
            throw dbe.get();
    }

    private void addBook() {threadedOperation(database.insertBook());}



}
