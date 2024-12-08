package se.kth.jdbclab.labb.view;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import se.kth.jdbclab.labb.model.Author;
import se.kth.jdbclab.labb.model.Book;

public class MainView {
    private BorderPane root;
    private TableView<Book> bookTable;
    private TableView<Author> authorTable;
    private Button addButton;
    private Button deleteButton;

    public MainView(Stage stage) {
        root = new BorderPane();

        // Create the table
        bookTable = new TableView<>();
        TableColumn<Book, String> isbnColumn = new TableColumn<>("ISBN");
        isbnColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getIsbn()));

        TableColumn<Book, String> titleColumn = new TableColumn<>("Title");
        titleColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitle()));

        TableColumn<Book, String> authorColumn = new TableColumn<>("Authors");
        authorColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAuthorNames()));

        bookTable.getColumns().addAll(isbnColumn, titleColumn, authorColumn);


        // Create buttons
        HBox buttons = new HBox(10);
        addButton = new Button("Add Book");
        deleteButton = new Button("Delete Book");
        buttons.getChildren().addAll(addButton, deleteButton);

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

    public TableView<Author> getAuthorTable() {
        return authorTable;
    }

    public Button getAddButton() {
        return addButton;
    }

    public Button getDeleteButton() {
        return deleteButton;
    }
}