package se.kth.jdbclab.labb.view;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import se.kth.jdbclab.labb.model.Author;
import se.kth.jdbclab.labb.model.Book;

public class MainView {
    private final TableView<Book> bookTable;
    private final Button addButton, deleteButton, viewButton;

    public MainView(Stage stage) {
        BorderPane root = new BorderPane();

        // Create the table
        bookTable = new TableView<>();
        TableColumn<Book, String> isbnColumn = new TableColumn<>("ISBN");
        isbnColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getIsbn()));

        TableColumn<Book, String> titleColumn = new TableColumn<>("Title");
        titleColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitle()));

        TableColumn<Book, String> authorColumn = new TableColumn<>("Authors");
        authorColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAuthorNames()));

        TableColumn<Book, String> genreColumn = new TableColumn<>("Genre");
        genreColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getGenreNames()));

        TableColumn<Book, String> gradeColumn = new TableColumn<>("Rating");
        gradeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAverageRating()));

        bookTable.getColumns().addAll(isbnColumn, titleColumn, authorColumn, genreColumn, gradeColumn);


        // Create buttons
        HBox buttons = new HBox(10);
        addButton = new Button("Add Book");
        deleteButton = new Button("Delete Book");
        viewButton = new Button("View Book");
        buttons.getChildren().addAll(addButton, deleteButton, viewButton);

        // Assemble the GUI
        root.setCenter(bookTable);
        root.setBottom(buttons);

        Scene scene = new Scene(root, 800, 600);
        stage.setScene(scene);
        stage.setTitle("Book Database");
        stage.show();
    }

    public TableView<Book> getBookTable() {
        return bookTable;
    }

    public Button getAddButton() {
        return addButton;
    }

    public Button getDeleteButton() {
        return deleteButton;
    }

    public Button getViewButton() {
        return viewButton;
    }
}