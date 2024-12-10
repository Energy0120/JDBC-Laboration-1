package se.kth.jdbclab.labb.model;

import java.util.Date;

public class Review {
    private int gradeID, grade;
    private String text;
    private Date grade_date;
    private User user;

    public Review(int gradeID, int grade, String text, Date grade_date, User user) {
        this.gradeID = gradeID;
        this.grade = grade;
        this.text = text;
        this.grade_date = grade_date;
        this.user = user;
    }

    public void setGradeID(int gradeID) {
        this.gradeID = gradeID;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getGrade_date() {
        return grade_date;
    }

    public User getUser() {
        return user;
    }

    public void setGrade_date(Date grade_date) {
        this.grade_date = grade_date;
    }
}
