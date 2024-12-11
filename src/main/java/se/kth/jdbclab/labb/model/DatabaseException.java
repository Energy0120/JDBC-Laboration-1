package se.kth.jdbclab.labb.model;

/**
 * Represents an exception that occurs during database operations.
 * This exception includes information about the SQL query that caused the error.
 */
public class DatabaseException extends Exception {

    /**
     * The SQL query associated with this exception.
     */
    private final String sqlQuery;

    /**
     * Constructs a new DatabaseException with the specified detail message and SQL query.
     *
     * @param message  the detail message explaining the exception.
     * @param sqlQuery the SQL query that caused the exception.
     */
    public DatabaseException(String message, String sqlQuery) {
        super(message);
        this.sqlQuery = sqlQuery;
    }

    /**
     * Retrieves the SQL query that caused this exception.
     *
     * @return the SQL query as a {@code String}.
     */
    public String getSqlQuery() {
        return sqlQuery;
    }
}
