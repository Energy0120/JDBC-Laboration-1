package se.kth.jdbclab.labb.model;

import com.mysql.cj.MysqlType;

import java.util.Date;

public class Author {
    private int AuthorID;
    private String Name;
    private Date DateOfBirth;
    private Date DateOfDeath;

    public Author(int AuthorID, String Name, Date DateOfBirth, Date DateOfDeath) {
        this.AuthorID = AuthorID;
        this.Name = Name;
        this.DateOfBirth = DateOfBirth;
        this.DateOfDeath = DateOfDeath;
    }

    public Author(String Name) {
        this.Name = Name;
    }

    public int getAuthorID() {
        return AuthorID;
    }

    public void setAuthorID(int authorID) {
        AuthorID = authorID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public Date getDateOfBirth() {
        return DateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        DateOfBirth = dateOfBirth;
    }

    public Date getDateOfDeath() {
        return DateOfDeath;
    }

    public void setDateOfDeath(Date dateOfDeath) {
        DateOfDeath = dateOfDeath;
    }
}
