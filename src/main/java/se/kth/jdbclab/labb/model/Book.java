package se.kth.jdbclab.labb.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Book {
    private String isbn, title;
    private List<Author> authors;

    public Book(String isbn, String title) {
        this.isbn = isbn;
        this.title = title;
        this.authors = new ArrayList<Author>();
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Author> getAuthors() {
        return authors;
    }

    public String getAuthorNames() {
        String authorNames = "";
        for (Author author : authors) {
            authorNames += author.getName() + ", ";
        }
        return authorNames.substring(0, authorNames.length() - 2);
    }

    public void setAuthors(List<Author> authors) {
        this.authors = authors;
    }
}
