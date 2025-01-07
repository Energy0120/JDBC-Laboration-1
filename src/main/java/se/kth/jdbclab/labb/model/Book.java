package se.kth.jdbclab.labb.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Represents a Book with its details such as ISBN, title, authors, genres, and reviews.
 */
public class Book {
    private String isbn, title;
    private List<Author> authors;
    private ObservableList<Review> reviews;
    private List<Genre> genre;

    /**
     * Constructs a Book with the given ISBN and title.
     * Initializes empty lists for authors, reviews, and genres.
     *
     * @param isbn  The ISBN of the book.
     * @param title The title of the book.
     */
    public Book(String isbn, String title) {
        this.isbn = isbn;
        this.title = title;
        this.authors = new ArrayList<>();
        this.reviews = FXCollections.observableArrayList();
        this.genre = new ArrayList<>();
    }

    /**
     * Constructs a Book with the given ISBN, title, authors, and genres.
     *
     * @param isbn    The ISBN of the book.
     * @param title   The title of the book.
     * @param authors The list of authors of the book.
     * @param genre   The list of genres of the book.
     */
    public Book(String isbn, String title, List<Author> authors, List<Genre> genre) {
        this.isbn = isbn;
        this.title = title;
        this.authors = authors;
        this.genre = genre;
        this.reviews = FXCollections.observableArrayList();
    }

    /**
     * Gets the ISBN of the book.
     *
     * @return The ISBN of the book.
     */
    public String getIsbn() {
        return isbn;
    }

    /**
     * Sets the ISBN of the book.
     *
     * @param isbn The ISBN to set for the book.
     */
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    /**
     * Gets the title of the book.
     *
     * @return The title of the book.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of the book.
     *
     * @param title The title to set for the book.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Gets the list of authors of the book.
     *
     * @return A list of authors.
     */
    public List<Author> getAuthors() {
        return authors;
    }

    /**
     * Gets a comma-separated string of the names of the authors of the book.
     *
     * @return A string containing the names of the authors.
     */
    public String getAuthorNames() {
        String authorNames = "";
        for (Author author : authors) {
            authorNames += author.getName() + ", ";
        }
        return authorNames.substring(0, authorNames.length() - 2);
    }

    /**
     * Sets the list of authors for the book.
     *
     * @param authors The list of authors to set for the book.
     */
    public void setAuthors(List<Author> authors) {
        this.authors = authors;
    }

    /**
     * Sets the list of genres for the book.
     *
     * @param genre The list of genres to set for the book.
     */
    public void setGenre(List<Genre> genre) {
        this.genre = genre;
    }

    /**
     * Gets the list of genres of the book.
     *
     * @return A list of genres.
     */
    public List<Genre> getGenre() {
        return genre;
    }

    /**
     * Gets a comma-separated string of the genres of the book.
     *
     * @return A string containing the genres of the book.
     */
    public String getGenreNames() {
        String genreNames = "";
        for (Genre genre : genre) {
            genreNames += genre.getGenre() + ", ";
        }
        return genreNames.substring(0, genreNames.length() - 2);
    }

    /**
     * Sets the list of reviews for the book.
     *
     * @param reviews The list of reviews to set for the book.
     */
    public void setReviews(ObservableList<Review> reviews) {
        this.reviews = reviews;
    }

    /**
     * Gets the list of reviews of the book.
     *
     * @return An observable list of reviews.
     */
    public ObservableList<Review> getReviews() {
        return reviews;
    }

    /**
     * Gets the average rating of the book based on its reviews.
     * If there are no reviews, returns "No Rating".
     *
     * @return The average rating as a string, or "No Rating" if no reviews exist.
     */
    public String getAverageRating() {
        if(reviews == null || reviews.isEmpty()) {
            return "No Rating";
        }
        float averageRating = 0;
        for (Review review : reviews) {
            averageRating += review.getGrade();
        }
        return String.valueOf(BigDecimal.valueOf(averageRating / reviews.size()).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue());
    }
}