package se.kth.jdbclab.labb.controller;

import com.sun.tools.javac.Main;
import se.kth.jdbclab.labb.model.Book;
import se.kth.jdbclab.labb.view.MainView;

import java.sql.Connection;
import java.util.Objects;

public class BookController {
    private MainView view;
    private Connection connection;
    private Book book;

    public BookController(MainController main, MainView view, Connection connection) {
        this.view = view;/*
        this.connection = connection;
        view.getAddButton().setOnAction(e -> addBook());
        view.getDeleteButton().setOnAction(e -> deleteBook());*/
    }

    public void initializeBook(MainController main, Book book) {
        this.book = book;
        view.getViewButton().setOnAction(e -> main.switchToLibrary());
        if(!Objects.equals(book.getAverageRating(), "No Rating"))
            loadReviews();
    }

    private void loadReviews(){
        view.getBookTable().setItems(book.getReviews());
    }

}
