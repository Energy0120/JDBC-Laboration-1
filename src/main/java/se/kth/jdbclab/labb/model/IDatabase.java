package se.kth.jdbclab.labb.model;

public interface IDatabase {
    public void insertBook();
    public void deleteBook();
    public void loadBooks();
    public void loadReviews(Book book);
}
