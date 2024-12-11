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
import se.kth.jdbclab.labb.model.*;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Represents the main view of the library application, which includes
 * the library table and various dialogs for user interaction.
 */
public class MainView {
    private TableView libraryTable;
    private BorderPane root;
    private Button addButton, deleteButton, viewButton, loginButton, createAccountButton, addAuthor, refreshButton;
    private final Button searchButton;
    private final Scene scene;
    private final MainController controller;
    private String loggedIn;    // null = Not Logged In.
    private Alert alertError;
    private Book currentBook;

    /**
     * Constructs the main view of the application.
     *
     * @param stage      The primary stage of the application.
     * @param controller The controller to handle user actions.
     */
    public MainView(Stage stage, MainController controller) {
        this.controller = controller;
        addButton = new Button();
        deleteButton = new Button();
        viewButton = new Button();
        loginButton = new Button();
        createAccountButton = new Button();
        searchButton = new Button();
        addAuthor = new Button();
        alertError = new Alert(Alert.AlertType.ERROR);
        makeLibraryTable(controller.loadBooks());
        scene = new Scene(root, 800, 600);
        stage.setScene(scene);
        stage.setTitle("Library");
        stage.show();
    }

    /**
     * Refreshes the library table with the given list of items.
     *
     * @param list The list of items to display in the table.
     */
    public <T> void refreshLibrary(List<T> list) {
        libraryTable = new TableView<>();
        libraryTable.setItems(FXCollections.observableArrayList(list));
    }

    /**
     * Creates and displays the library table with a list of books.
     *
     * @param list The list of books to display in the table.
     */
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

        HBox buttons = createButtons("Library");
        root.setCenter(libraryTable);
        root.setTop(buttons);
        if(scene != null)
            scene.setRoot(root);
    }

    /**
     * Creates and displays a table of reviews for the selected book.
     *
     * @param book The book whose reviews are to be displayed.
     */
    public void makeBookTable(Book book) {
        root = new BorderPane();
        if (book == null) {
            alertError.setTitle("Error");
            alertError.setContentText("Please select a book.");
            alertError.showAndWait();
            return;
        }
        refreshLibrary(controller.loadReviews(book.getIsbn()));

        TableColumn<Review, String> gradeColumn = new TableColumn<>("Grade");
        gradeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getGrade())));

        TableColumn<Review, String> userNameColumn = new TableColumn<>("User Name");
        userNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getUser())));

        TableColumn<Review, String> textColumn = new TableColumn<>("Description");
        textColumn.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getText())));

        TableColumn<Review, String> dateColumn = new TableColumn<>("Date");
        dateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getGrade_date())));

        libraryTable.getColumns().addAll(gradeColumn, textColumn, userNameColumn, dateColumn);

        HBox buttons = createButtons("Review");
        addButton.setOnAction(e -> reviewForm(book.getIsbn()));
        viewButton.setOnAction(e -> makeLibraryTable(controller.loadBooks()));
        root.setCenter(libraryTable);
        root.setTop(buttons);
        scene.setRoot(root);
    }

    /**
     * Displays a dialog for adding a review for a specific book.
     *
     * @param isbn The ISBN of the book being reviewed.
     */
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
                    makeBookTable(currentBook);
                } catch (NumberFormatException ex) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Grade Error");
                    alert.setHeaderText("Invalid Input");
                    alert.setContentText("Please enter a valid grade between 1 and 5.");
                    alert.showAndWait();
                }
            }
            return null;
        });
        dialog.showAndWait();
    }

    /**
     * Creates and returns an HBox containing buttons for managing the view.
     *
     * @param mode Specifies the mode of the buttons ("Library" or "Review").
     * @return An HBox containing the buttons.
     */
    private HBox createButtons(String mode){
        HBox buttons = new HBox(10);
        HBox manageButtons = new HBox(10);
        manageButtons.setAlignment(Pos.CENTER_LEFT);
        HBox spacer = new HBox();
        spacer.setAlignment(Pos.CENTER);
        HBox loginState = new HBox(10);
        loginState.setAlignment(Pos.CENTER_RIGHT);
        HBox.setHgrow(spacer, Priority.ALWAYS);
        addButton = new Button();
        refreshButton = new Button("Refresh");
        if(Objects.equals(mode, "Library")){
            addButton.setText("Add Book");
            deleteButton = new Button("Remove Book");
            viewButton = new Button("Review Book");
            addAuthor = new Button("Add Author");
            searchButton.setText("Search Book");
            searchButton.setOnAction(e -> setSearch());
            addButton.setOnAction(e -> addBookForm());
            addAuthor.setOnAction(e -> addAuthorForm());
            deleteButton.setOnAction(e -> {
                controller.removeBook(getLibraryTable().getSelectionModel().getSelectedItem());
                makeLibraryTable(controller.loadBooks());
            });
            viewButton.setOnAction(e -> {
                currentBook = getLibraryTable().getSelectionModel().getSelectedItem();
                makeBookTable(currentBook);
            });
            refreshButton.setOnAction(e -> makeLibraryTable(controller.loadBooks()));
            manageButtons.getChildren().addAll(addButton, deleteButton, addAuthor, searchButton, viewButton, refreshButton);
        } else {
            addButton.setText("Add Review");
            viewButton.setText("Back");
            refreshButton.setOnAction(e -> makeBookTable(currentBook));
            manageButtons.getChildren().addAll(addButton, refreshButton, viewButton);
        }

        if (loggedIn == null){
            addButton.setDisable(true);
            deleteButton.setDisable(true);
            addAuthor.setDisable(true);
            loginButton = new Button("Login");
            loginButton.setOnAction(e -> accountManager(false));
            createAccountButton = new Button("Create Account");
            createAccountButton.setOnAction(e -> accountManager(true));
            loginState.getChildren().addAll(loginButton, createAccountButton);
        } else {
            addButton.setDisable(false);
            deleteButton.setDisable(false);
            addAuthor.setDisable(false);
            Label loginInfo = new Label("Logged in as " + loggedIn);
            loginState.getChildren().add(loginInfo);
        }

        buttons.getChildren().addAll(manageButtons, spacer, loginState);
        buttons.setPadding(new Insets(5, 10, 5, 10));
        return buttons;
    }

    /**
     * Displays a dialog that allows the user to search for books based on different criteria such as title, ID, author, or genre.
     * The user can enter a search value, and the search results will be displayed accordingly.
     */
    public void setSearch(){
        Dialog dialog = new Dialog();
        VBox container = new VBox(10);
        HBox criteriaBox = new HBox(10);
        HBox valueBox = new HBox(10);
        Label criteriaLabel = new Label("Search Criteria: ");
        ComboBox<String> searchBox = new ComboBox<>();
        searchBox.getItems().addAll(
                "Title",
                "ID",
                "Author",
                "Genre"
        );
        criteriaBox.getChildren().addAll(criteriaLabel, searchBox);
        criteriaBox.setAlignment(Pos.CENTER_RIGHT);
        Label search = new Label("Value : ");
        TextField text = new TextField ();
        valueBox.getChildren().addAll(search, text);
        valueBox.setAlignment(Pos.CENTER_RIGHT);

        container.getChildren().addAll(criteriaBox, valueBox);
        dialog.getDialogPane().setContent(container);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.setTitle("Search");
        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                if (text.getText() == ""){
                    makeLibraryTable(controller.loadBooks());
                    return null;
                }
                makeLibraryTable(controller.loadBooksByCriteria(searchBox.getSelectionModel().getSelectedItem(), text.getText()));
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

    /**
     * Displays a dialog for adding a new book.
     * Prompts the user for ISBN, title, authors, and genres.
     * If the user provides valid input, the new book is added to the library.
     */
    private void addBookForm(){
        Dialog dialog = new Dialog();
        VBox container = new VBox(10);
        HBox ISBNBox = new HBox(10);
        Label ISBNLabel = new Label("ISBN: ");
        TextField ISBNField = new TextField ();
        ISBNBox.getChildren().addAll(ISBNLabel, ISBNField);
        ISBNBox.setAlignment(Pos.CENTER_RIGHT);
        HBox TitleBox = new HBox(10);
        Label TitleLabel = new Label("Title: ");
        TextField TitleField = new TextField ();
        TitleBox.getChildren().addAll(TitleLabel, TitleField);
        TitleBox.setAlignment(Pos.CENTER_RIGHT);
        Label AuthorLabel = new Label("Authors: ");
        HBox AuthorBox = new HBox(10);
        ComboBox<Author> AuthorCombo = new ComboBox<>();
        AuthorCombo.getItems().addAll(controller.loadAuthors());
        AuthorBox.getChildren().addAll(AuthorLabel, AuthorCombo);
        AuthorBox.setAlignment(Pos.CENTER_RIGHT);
        HBox GenreBox = new HBox(10);
        Label GenreLabel = new Label("Genre: ");
        TextField GenreField = new TextField ();
        GenreBox.getChildren().addAll(GenreLabel, GenreField);
        GenreBox.setAlignment(Pos.CENTER_RIGHT);
        container.getChildren().addAll(ISBNBox, TitleBox, AuthorBox, GenreBox);
        dialog.getDialogPane().setContent(container);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.setTitle("Add Book");
        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                if (Objects.equals(ISBNField.getText(), "") || Objects.equals(TitleField.getText(), "") || Objects.equals(GenreField.getText(), "") || AuthorCombo.getSelectionModel().getSelectedItem() == null) {
                    alertError = new Alert(Alert.AlertType.ERROR);
                    alertError.setTitle("Book Error");
                    alertError.setHeaderText("Invalid Input");
                    alertError.setContentText("Please fill all required fields.");
                    alertError.showAndWait();
                    return null;
                }
                List<Genre> genreList = new ArrayList<>();
                List<Author> authorList = new ArrayList<>();
                authorList.add(AuthorCombo.getSelectionModel().getSelectedItem());
                String[] genres = GenreField.getText().split(", ");
                for (String genre : genres) {
                    genreList.add(new Genre(genre.trim()));
                    System.out.println(genre.trim());
                }
                controller.addBook(new Book(ISBNField.getText(), TitleField.getText(), authorList, genreList));
                makeLibraryTable(controller.loadBooks());
            }
            return null;
        });
        dialog.showAndWait();
    }

    private void addAuthorForm(){
        Dialog dialog = new Dialog();
        VBox container = new VBox(10);
        HBox NameBox = new HBox(10);
        Label NameLabel = new Label("Name: ");
        TextField NameField = new TextField ();
        NameBox.getChildren().addAll(NameLabel, NameField);
        NameBox.setAlignment(Pos.CENTER_RIGHT);
        HBox DOBBox = new HBox();
        Label DOBLabel = new Label("Date of Birth: ");
        DatePicker DOBField = new DatePicker();
        DOBBox.getChildren().addAll(DOBLabel, DOBField);
        DOBBox.setAlignment(Pos.CENTER_RIGHT);
        HBox DODBox = new HBox();
        Label DODLabel = new Label("Date of Death: ");
        DatePicker DODField = new DatePicker();
        DODBox.getChildren().addAll(DODLabel, DODField);
        DODBox.setAlignment(Pos.CENTER_RIGHT);
        container.getChildren().addAll(NameBox, DOBBox, DODBox);
        dialog.getDialogPane().setContent(container);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.setTitle("Add Author");
        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                if (Objects.equals(NameField.getText(), "")) {
                    alertError = new Alert(Alert.AlertType.ERROR);
                    alertError.setTitle("Author Error");
                    alertError.setHeaderText("Invalid Input");
                    alertError.setContentText("Please fill all required fields.");
                    alertError.showAndWait();
                    return null;
                }
                Date DOD = null, DOB = null;
                if(DODField.getValue() != null)
                    DOD = Date.from(DODField.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
                if(DOBField.getValue() != null)
                    DOB = Date.from(DOBField.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
                controller.addAuthor(new Author(NameField.getText(), DOB, DOD));
                makeLibraryTable(controller.loadBooks());
            }
            return null;
        });
        dialog.showAndWait();
    }

    /**
     * Returns the TableView that displays the library's books.
     *
     * @return The TableView displaying the library's books.
     */
    public TableView<Book> getLibraryTable() {
        return libraryTable;
    }

    /**
     * Returns the button used to add a new book to the library.
     *
     * @return The "Add" button.
     */
    public Button getAddButton() {
        return addButton;
    }

    /**
     * Returns the button used to delete a selected book from the library.
     *
     * @return The "Delete" button.
     */
    public Button getDeleteButton() {
        return deleteButton;
    }

    /**
     * Returns the button used to view details of a selected book.
     *
     * @return The "View" button.
     */
    public Button getViewButton() {
        return viewButton;
    }

    /**
     * Returns the Scene that contains the library's UI components.
     *
     * @return The scene containing the library view.
     */
    public Scene getScene() {
        return scene;
    }
}