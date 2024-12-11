package se.kth.jdbclab.labb.model;

/**
 * Represents a user in the system with basic user information such as
 * ID, name, email, and password.
 */
public class User {
    private int userID;
    private String name, email, password;

    /**
     * Creates a User with only a name.
     *
     * @param name The name of the user.
     */
    public User(String name) {
        this.name = name;
    }

    /**
     * Creates a User with all user details.
     *
     * @param userID   The unique ID of the user.
     * @param name     The name of the user.
     * @param email    The email of the user.
     * @param password The password of the user.
     */
    public User(int userID, String name, String email, String password) {
        this.userID = userID;
        this.name = name;
        this.email = email;
        this.password = password;
    }

    /**
     * Gets the user's unique ID.
     *
     * @return The user's ID.
     */
    public int getUserID() {
        return userID;
    }

    /**
     * Sets the user's unique ID.
     *
     * @param userID The user's ID to set.
     */
    public void setUserID(int userID) {
        this.userID = userID;
    }

    /**
     * Gets the user's name.
     *
     * @return The user's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the user's name.
     *
     * @param name The name to set for the user.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the user's email address.
     *
     * @return The user's email.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the user's email address.
     *
     * @param email The email to set for the user.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the user's password.
     *
     * @return The user's password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the user's password.
     *
     * @param password The password to set for the user.
     */
    public void setPassword(String password) {
        this.password = password;
    }
}
