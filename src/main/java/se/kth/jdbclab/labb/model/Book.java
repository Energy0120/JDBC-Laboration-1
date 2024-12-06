package se.kth.jdbclab.labb.model;

import java.util.Date;

public class Book {
    private String isbn;
    private String title;
    private Date releaseDate;
    //private List<Author> authors;

    public Book(String isbn, String title, Date releaseDate) {
        this.isbn = isbn;
        this.title = title;
        this.releaseDate = releaseDate;
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

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    /*public List<Author> getAuthors() {
        return authors;
    }*/

    /*public void setAuthors(List<Author> authors) {
        this.authors = authors;
    }*/
}
