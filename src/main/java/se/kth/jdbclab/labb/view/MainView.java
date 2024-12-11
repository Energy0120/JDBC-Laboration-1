package se.kth.jdbclab.labb.view;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import se.kth.jdbclab.labb.controller.MainController;
import se.kth.jdbclab.labb.model.Book;
import se.kth.jdbclab.labb.model.Review;
import se.kth.jdbclab.labb.model.User;

import java.util.Date;
import java.util.List;
import java.util.Objects;

public class MainView {
    private TableView libraryTable;
    private BorderPane root;
    private Button addButton, deleteButton, viewButton, loginButton, createAccountButton;
    private final Button searchByName, searchByID, searchByAuthor, searchByGenre;
    private final Scene scene;
    private final MainController controller;
    private String loggedIn;    // null = Not Logged In.
    private Alert alertError;

    public MainView(Stage stage, MainController controller) {
        this.controller = controller;
        addButton = new Button();
        deleteButton = new Button();
        viewButton = new Button();
        loginButton = new Button();
        createAccountButton = new Button();
        searchByID = new Button();
        searchByGenre = new Button();
        searchByName = new Button();
        searchByAuthor = new Button();
        alertError = new Alert(Alert.AlertType.ERROR);
        makeLibraryTable(controller.loadBooks());
        scene = new Scene(root, 800, 600);
        stage.setScene(scene);
        stage.setTitle("Library");
        stage.show();
    }

    public <T> void refreshLibrary(List<T> list) {
        libraryTable = new TableView<>();
        libraryTable.setItems(FXCollections.observableArrayList(list));
    }

    public void makeLibraryTable(List<Book> list) {
        root = new BorderPane();
        refreshLibrary(list);

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
        viewButton.setOnAction(e -> {
            if (getLibraryTable().getSelectionModel().getSelectedItem() == null) {
                alertError.setTitle("Error");
                alertError.setContentText("Please select a book.");
                alertError.showAndWait();
            } else makeBookTable();
        });
        root.setCenter(libraryTable);
        root.setTop(buttons);
        if(scene != null)
            scene.setRoot(root);
    }

    public void makeBookTable() {
        root = new BorderPane();
        Book currentBook = getLibraryTable().getSelectionModel().getSelectedItem();
        refreshLibrary(controller.loadReviews(currentBook.getIsbn()));

        TableColumn<Review, String> gradeColumn = new TableColumn<>("Grade");
        gradeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getGrade())));

        TableColumn<Review, String> userNameColumn = new TableColumn<>("User Name");
        userNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getUser())));

        TableColumn<Review, String> textColumn = new TableColumn<>("Description");
        textColumn.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getText())));

        TableColumn<Review, String> dateColumn = new TableColumn<>("Date");
        dateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getGrade_date())));

        libraryTable.getColumns().addAll(gradeColumn, textColumn, userNameColumn, dateColumn);

        HBox buttons = createButtons("Add Review", "Delete Review", "Back");
        addButton.setOnAction(e -> reviewForm(currentBook.getIsbn()));
        viewButton.setOnAction(e -> makeLibraryTable(controller.loadBooks()));
        root.setCenter(libraryTable);
        root.setTop(buttons);
        scene.setRoot(root);
    }

    private void reviewForm(String isbn){
        Dialog dialog = new Dialog();
        VBox container = new VBox();
        container.setSpacing(10);
        Label gradeLabel = new Label("Grade (1-5): ");
        TextField gradeField = new TextField ();
        HBox gradeBox = new HBox();
        gradeBox.getChildren().addAll(gradeLabel, gradeField);
        gradeBox.setAlignment(Pos.CENTER_RIGHT);
        Label descLabel = new Label("Description: ");
        HBox descBox = new HBox();
        TextField descField = new TextField ();
        descBox.getChildren().addAll(descLabel, descField);
        descBox.setAlignment(Pos.CENTER_RIGHT);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.setTitle("Add a Review");
        HBox buttons = new HBox(10);
        container.getChildren().addAll(gradeBox, descBox);
        dialog.getDialogPane().setContent(container);
        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                try {
                    int grade = Integer.parseInt(gradeField.getText());
                    if (grade < 1 || grade > 5) {
                        throw new NumberFormatException("Grade must be between 1 and 5.");
                    }
                    controller.addReview(isbn, grade, descField.getText(), new Date());
                    makeLibraryTable(controller.loadBooks());
                } catch (NumberFormatException ex) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Invalid Input");
                    alert.setHeaderText("Grade Error");
                    alert.setContentText("Please enter a valid grade between 1 and 5.");
                    alert.showAndWait();
                }
            }
            return null;
        });
        dialog.showAndWait();
    }

    private HBox createButtons(String add, String delete, String view){
        HBox buttons = new HBox(10);
        HBox manageButtons = new HBox(10);
        manageButtons.setAlignment(Pos.CENTER_LEFT);
        HBox spacer = new HBox();
        spacer.setAlignment(Pos.CENTER);
        HBox loginState = new HBox(10);
        loginState.setAlignment(Pos.CENTER_RIGHT);
        HBox.setHgrow(spacer, Priority.ALWAYS);

        addButton = new Button(add);
        addButton.setText(add);
        deleteButton = new Button(delete);
        deleteButton.setText(delete);
        viewButton = new Button(view);
        viewButton.setText(view);

        if (loggedIn == null){
            addButton.setDisable(true);
            deleteButton.setDisable(true);
            loginButton = new Button("Login");
            loginButton.setOnAction(e -> accountManager(false));
            createAccountButton = new Button("Create Account");
            createAccountButton.setOnAction(e -> accountManager(true));
            loginState.getChildren().addAll(loginButton, createAccountButton);
        } else {
            addButton.setDisable(false);
            deleteButton.setDisable(false);
            Label loginInfo = new Label("Logged in as " + loggedIn);
            loginState.getChildren().add(loginInfo);
        }

        searchByID.setText("Search ID");
        searchByID.setOnAction(e -> {
            setSearchByCriteria("ID");
        });
        searchByName.setText("Search Name");
        searchByName.setOnAction(e -> {
            setSearchByCriteria("Name");
        });
        searchByAuthor.setText("Search Author");
        searchByAuthor.setOnAction(e -> {
            setSearchByCriteria("Author");
        });
        searchByGenre.setText("Search Genre");
        searchByGenre.setOnAction(e -> {
            setSearchByCriteria("Genre");
        });

        manageButtons.getChildren().addAll(addButton, deleteButton, searchByName, searchByAuthor, searchByGenre, searchByID, viewButton);
        buttons.getChildren().addAll(manageButtons, spacer, loginState);
        buttons.setPadding(new Insets(5, 10, 5, 10));
        return buttons;
    }

    public void setSearchByCriteria(String criteria){
        Dialog dialog = new Dialog();
        HBox container = new HBox(10);
        Label search = new Label(criteria + ": ");
        TextField text = new TextField ();
        container.getChildren().addAll(search, text);
        dialog.getDialogPane().setContent(container);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.setTitle("Search Book");
        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                if (text.getText() == ""){
                    makeLibraryTable(controller.loadBooks());
                    return null;
                }
                makeLibraryTable(controller.loadBooksByCriteria(criteria, text.getText()));
            }
            return null;
        });
        dialog.showAndWait();

    }

    /**
     * Responsible for creating or logging into an account.
     * Value 0 indicates that you are logging in, while value 1
     * indicates that you are creating a new account. Creating
     * an account logs you in automatically to the newly created account.
     *
     * @param  creating Indicates whether you are creating or logging into an account.
     */
    public void accountManager(boolean creating){
        Dialog dialog = new Dialog();
        VBox container = new VBox();
        container.setSpacing(10);
        Label mailLabel = new Label("Email: ");
        HBox mailBox = new HBox();
        TextField mailField = new TextField ();
        mailBox.getChildren().addAll(mailLabel, mailField);
        mailBox.setAlignment(Pos.CENTER_RIGHT);
        Label passLabel = new Label("Password: ");
        HBox passBox = new HBox();
        TextField passField = new TextField ();
        passBox.getChildren().addAll(passLabel, passField);
        passBox.setAlignment(Pos.CENTER_RIGHT);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        if (creating) {
            dialog.setTitle("Create an account");
            Label nameLabel = new Label("Name: ");
            TextField nameField = new TextField ();
            HBox nameBox = new HBox();
            nameBox.getChildren().addAll(nameLabel, nameField);
            nameBox.setAlignment(Pos.CENTER_RIGHT);
            container.getChildren().addAll(nameBox, mailBox, passBox);
            dialog.getDialogPane().setContent(container);
            dialog.setResultConverter(buttonType -> {
                if (buttonType == ButtonType.OK) {
                    loggedIn = controller.createAccount(nameField.getText(), mailField.getText(), passField.getText());
                    makeLibraryTable(controller.loadBooks());
                }
                return null;
            });
            dialog.showAndWait();
        } else {
            dialog.setTitle("Login to your account");
            Label nameLabel = new Label("Name: ");
            TextField nameField = new TextField ();
            HBox nameBox = new HBox();
            nameBox.getChildren().addAll(nameLabel, nameField);
            nameBox.setAlignment(Pos.CENTER_RIGHT);
            HBox buttons = new HBox(10);
            container.getChildren().addAll(mailBox, passBox);
            dialog.getDialogPane().setContent(container);
            dialog.setResultConverter(buttonType -> {
                if (buttonType == ButtonType.OK) {
                    loggedIn = controller.login(mailField.getText(), passField.getText());
                    makeLibraryTable(controller.loadBooks());
                }
                return null;
            });
            dialog.showAndWait();
        }
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