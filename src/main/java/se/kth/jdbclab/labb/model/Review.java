package se.kth.jdbclab.labb.model;

import java.util.Date;

public class Review {
    private int gradeID, grade, userID;
    private String text, userName;
    private Date grade_date;

    public Review(int gradeID, int grade, String text, Date grade_date, String userName) {
        this.gradeID = gradeID;
        this.grade = grade;
        this.text = text;
        this.grade_date = grade_date;
        this.userName = userName;
    }


    public Review(int grade, String text, Date grade_date, int userID) {
        this.grade = grade;
        this.text = text;
        this.grade_date = grade_date;
        this.userID = userID;
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

    public int getUserID() {
        return userID;
    }

    public String getUser() {
        return userName;
    }

    public int getGradeID() {
        return gradeID;
    }

    public void setGrade_date(Date grade_date) {
        this.grade_date = grade_date;
    }
}
