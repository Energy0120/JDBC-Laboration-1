package se.kth.jdbclab.labb.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Book extends ListItem {
    private String isbn, title;
    private List<Author> authors;
    private ObservableList<Review> reviews;
    private List<Genre> genre;

    public Book(String isbn, String title) {
        this.isbn = isbn;
        this.title = title;
        this.authors = new ArrayList<>();
        this.reviews = FXCollections.observableArrayList();
        this.genre = new ArrayList<>();
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

    public void setGenre(List<Genre> genre) {
        this.genre = genre;
    }

    public List<Genre> getGenre() {
        return genre;
    }

    public String getGenreNames() {
        String genreNames = "";
        for (Genre genre : genre) {
            genreNames += genre.getGenre() + ", ";
        }
        return genreNames.substring(0, genreNames.length() - 2);
    }

    public void setReviews(ObservableList<Review> reviews) {
        this.reviews = reviews;
    }

    public ObservableList<Review> getReviews() {
        return reviews;
    }

    public String getAverageRating() {
        float averageRating = 0;
        for (Review review : reviews) {
            averageRating+=review.getGrade();
        }
        if (averageRating==0) {
            return "No Rating";
        }
        return String.valueOf(averageRating/reviews.size());
    }
}
