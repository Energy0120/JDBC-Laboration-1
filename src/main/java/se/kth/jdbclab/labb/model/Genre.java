package se.kth.jdbclab.labb.model;

/**
 * Represents a genre in the system.
 */
public class Genre {
    /**
     * The name of the genre.
     */
    private String genre;

    /**
     * Creates a new Genre with the specified name.
     *
     * @param genre the name of the genre
     */
    public Genre(String genre) {
        this.genre = genre;
    }

    /**
     * Retrieves the name of the genre.
     *
     * @return the name of the genre
     */
    public String getGenre() {
        return genre;
    }

    /**
     * Updates the name of the genre.
     *
     * @param genre the new name of the genre
     */
    public void setGenre(String genre) {
        this.genre = genre;
    }
}
