package se.kth.jdbclab.labb.model;

import java.util.Date;

public class Review {
    private int ISBN;
    private int grade;
    private String text;
    private Date grade_date;

    public Review(int ISBN, int grade, String text, Date grade_date) {
        this.ISBN = ISBN;
        this.grade = grade;
        this.text = text;
        this.grade_date = grade_date;
    }

    public int getISBN() {
        return ISBN;
    }

    public void setISBN(int ISBN) {
        this.ISBN = ISBN;
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

    public void setGrade_date(Date grade_date) {
        this.grade_date = grade_date;
    }
}
