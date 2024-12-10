package se.kth.jdbclab.labb.controller;

import se.kth.jdbclab.labb.model.Book;
import se.kth.jdbclab.labb.model.Database;
import se.kth.jdbclab.labb.model.Review;
import se.kth.jdbclab.labb.model.User;

import javax.swing.*;
import java.util.List;

public class MainController {
    private User currentUser;
    private int loggedIn;    // 0 = Not Logged In, 1 = User, 2 = Manager.
    Database database;

    public MainController(Database database) {
        loggedIn = 0;
        this.database = database;
    }

    public List<Book> loadBooks(){
        return database.loadBooks();
    }

    public int getLoggedIn() {
        return loggedIn;
    }

    private void authenticate() {
        String userName = JOptionPane.showInputDialog("Please Type your username:");
        String password = JOptionPane.showInputDialog("Please Type your password:");

    }
    /*
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
    */

    public void addBook(Book book) {
        database.insertBook(book);
    }


    public List<Review> loadReviews(String isbn) {
        return database.loadReviews(isbn);
    }
}
