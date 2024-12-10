package se.kth.jdbclab.labb.view;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import se.kth.jdbclab.labb.controller.MainController;
import se.kth.jdbclab.labb.model.Book;
import se.kth.jdbclab.labb.model.ListItem;
import se.kth.jdbclab.labb.model.Review;

import java.util.List;

public class MainView {
    private TableView libraryTable;
    private BorderPane root;
    private Button addButton, deleteButton, viewButton, loginButton, createAccountButton;
    private final Scene scene;
    private final MainController controller;

    public MainView(Stage stage, MainController controller) {
        this.controller = controller;
        addButton = new Button();
        deleteButton = new Button();
        viewButton = new Button();
        loginButton = new Button();
        createAccountButton = new Button();
        makeLibraryTable();
        scene = new Scene(root, 800, 600);
        stage.setScene(scene);
        stage.setTitle("Library");
        stage.show();
    }

    public <T> void refreshLibrary(List<T> list) {
        libraryTable = new TableView<>();
        libraryTable.setItems(FXCollections.observableArrayList(list));
    }

    public void makeLibraryTable() {
        root = new BorderPane();
        refreshLibrary(controller.loadBooks());

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

        libraryTable.getColumns().addAll(isbnColumn, titleColumn, authorColumn, genreColumn, gradeColumn);

        HBox buttons = createButtons("Add", "Remove", "View");
        /*
        if(getLibraryTable().getSelectionModel().getSelectedItem() == null) {
            deleteButton.setDisable(true);
            viewButton.setDisable(true);
        } else {
            deleteButton.setDisable(false);
            viewButton.setDisable(false);
        }
        */
        addButton.setOnAction(e -> controller.addBook(getLibraryTable().getSelectionModel().getSelectedItem()));
        viewButton.setOnAction(e -> makeBookTable());
        root.setCenter(libraryTable);
        root.setBottom(buttons);
        if(scene != null)
            scene.setRoot(root);
    }

    public void makeBookTable() {
        root = new BorderPane();
        refreshLibrary(controller.loadReviews(getLibraryTable().getSelectionModel().getSelectedItem().getIsbn()));

        TableColumn<Review, String> gradeColumn = new TableColumn<>("Grade");
        gradeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getGrade())));

        TableColumn<Review, String> userNameColumn = new TableColumn<>("User Name");
        userNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getUser().getName())));

        TableColumn<Review, String> textColumn = new TableColumn<>("Description");
        textColumn.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getText())));

        TableColumn<Review, String> dateColumn = new TableColumn<>("Date");
        dateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getGrade_date())));

        libraryTable.getColumns().addAll(gradeColumn, textColumn, userNameColumn, dateColumn);

        HBox buttons = createButtons("Add Review", "Delete Review", "Back");
        viewButton.setOnAction(e -> makeLibraryTable());
        root.setCenter(libraryTable);
        root.setBottom(buttons);
        scene.setRoot(root);
    }

    private HBox createButtons(String add, String delete, String view){

        HBox buttons = new HBox(10);
        HBox manageButtons = new HBox(10);
        manageButtons.setAlignment(Pos.CENTER_LEFT);
        HBox spacer = new HBox();
        spacer.setAlignment(Pos.CENTER);
        HBox loginButtons = new HBox(10);
        loginButtons.setAlignment(Pos.CENTER_RIGHT);
        HBox.setHgrow(spacer, Priority.ALWAYS);

        addButton = new Button(add);
        addButton.setText(add);
        deleteButton = new Button(delete);
        deleteButton.setText(delete);
        viewButton = new Button(view);
        viewButton.setText(view);
        loginButton = new Button("Login");
        createAccountButton = new Button("Create Account");

        loginButtons.getChildren().addAll(loginButton, createAccountButton);
        manageButtons.getChildren().addAll(addButton, deleteButton, viewButton);
        buttons.getChildren().addAll(manageButtons, spacer, loginButtons);
        return buttons;
    }

    public TableView<Book> getLibraryTable() {
        return libraryTable;
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

    public Scene getScene() {
        return scene;
    }
}