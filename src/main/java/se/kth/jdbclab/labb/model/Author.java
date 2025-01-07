package se.kth.jdbclab.labb.model;

import java.util.Date;

/**
 * Represents an Author with their details such as ID, name, date of birth, and date of death.
 */
public class Author {
    private int AuthorID;
    private String Name;
    private Date DateOfBirth;
    private Date DateOfDeath;

    /**
     * Constructs an Author with all details.
     *
     * @param Name        The name of the author.
     * @param DateOfBirth The birthdate of the author.
     * @param DateOfDeath The death date of the author.
     */
    public Author(String Name, Date DateOfBirth, Date DateOfDeath) {
        this.Name = Name;
        this.DateOfBirth = DateOfBirth;
        this.DateOfDeath = DateOfDeath;
    }

    /**
     * Constructs an Author with only ID and name.
     *
     * @param authorID The ID of the author.
     * @param Name     The name of the author.
     */
    public Author(int authorID, String Name) {
        this.AuthorID = authorID;
        this.Name = Name;
    }

    public Author(String Name) {
        this.Name = Name;
    }

    /**
     * Gets the Author's ID.
     *
     * @return The author's ID.
     */
    public int getAuthorID() {
        return AuthorID;
    }

    /**
     * Sets the Author's ID.
     *
     * @param authorID The author's ID.
     */
    public void setAuthorID(int authorID) {
        AuthorID = authorID;
    }

    /**
     * Gets the Name of the author.
     *
     * @return The name of the author.
     */
    public String getName() {
        return Name;
    }

    /**
     * Sets the Name of the author.
     *
     * @param name The name of the author.
     */
    public void setName(String name) {
        Name = name;
    }

    /**
     * Gets the Date of Birth of the author.
     *
     * @return The author's date of birth.
     */
    public Date getDateOfBirth() {
        return DateOfBirth;
    }

    /**
     * Sets the Date of Birth of the author.
     *
     * @param dateOfBirth The author's date of birth.
     */
    public void setDateOfBirth(Date dateOfBirth) {
        DateOfBirth = dateOfBirth;
    }

    /**
     * Gets the Date of Death of the author.
     *
     * @return The author's date of death, or null if still alive.
     */
    public Date getDateOfDeath() {
        return DateOfDeath;
    }

    /**
     * Sets the Date of Death of the author.
     *
     * @param dateOfDeath The author's date of death.
     */
    public void setDateOfDeath(Date dateOfDeath) {
        DateOfDeath = dateOfDeath;
    }

    /**
     * Returns a string representation of the author, which is the author's name.
     *
     * @return The name of the author.
     */
    @Override
    public String toString() {
        return Name;
    }
}
