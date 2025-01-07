package se.kth.jdbclab.labb.model;

import java.util.Date;

/**
 * Represents a review for a book, including grade, text, user details, and the date of the review.
 */
public class Review {
    private int gradeID, grade, userID;
    private String text, userName;
    private Date grade_date;

    /**
     * Constructs a Review with all details including the username.
     *
     * @param gradeID    The unique ID of the review.
     * @param grade      The grade given in the review (e.g., 1-5).
     * @param text       The text content of the review.
     * @param grade_date The date the review was submitted.
     * @param userName   The name of the user who submitted the review.
     */
    public Review(int gradeID, int grade, String text, Date grade_date, String userName) {
        this.gradeID = gradeID;
        this.grade = grade;
        this.text = text;
        this.grade_date = grade_date;
        this.userName = userName;
    }

    /**
     * Constructs a Review without specifying the username but with user ID.
     *
     * @param grade      The grade given in the review (e.g., 1-5).
     * @param text       The text content of the review.
     * @param grade_date The date the review was submitted.
     * @param userID     The ID of the user who submitted the review.
     */
    public Review(int grade, String text, Date grade_date, int userID) {
        this.grade = grade;
        this.text = text;
        this.grade_date = grade_date;
        this.userID = userID;
    }

    public Review(int grade, String text, Date grade_date, String userName) {
        this.grade = grade;
        this.text = text;
        this.grade_date = grade_date;
        this.userName = userName;
    }

    public Review(int grade) {
        this.grade = grade;
    }

    /**
     * Sets the unique ID of the review.
     *
     * @param gradeID The grade ID to set.
     */
    public void setGradeID(int gradeID) {
        this.gradeID = gradeID;
    }

    /**
     * Gets the grade of the review.
     *
     * @return The grade (e.g., 1-5).
     */
    public int getGrade() {
        return grade;
    }

    /**
     * Sets the grade of the review.
     *
     * @param grade The grade to set (e.g., 1-5).
     */
    public void setGrade(int grade) {
        this.grade = grade;
    }

    /**
     * Gets the text content of the review.
     *
     * @return The review text.
     */
    public String getText() {
        return text;
    }

    /**
     * Sets the text content of the review.
     *
     * @param text The text content to set.
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * Gets the date the review was submitted.
     *
     * @return The date of the review.
     */
    public Date getGrade_date() {
        return grade_date;
    }

    /**
     * Gets the user ID associated with the review.
     *
     * @return The user ID of the reviewer.
     */
    public int getUserID() {
        return userID;
    }

    /**
     * Gets the username associated with the review.
     *
     * @return The username of the reviewer.
     */
    public String getUser() {
        return userName;
    }

    /**
     * Gets the unique ID of the review.
     *
     * @return The grade ID of the review.
     */
    public int getGradeID() {
        return gradeID;
    }

    /**
     * Sets the date the review was submitted.
     *
     * @param grade_date The date to set for the review.
     */
    public void setGrade_date(Date grade_date) {
        this.grade_date = grade_date;
    }
}
