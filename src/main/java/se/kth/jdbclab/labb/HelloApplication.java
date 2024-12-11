package se.kth.jdbclab.labb;

import javafx.application.Application;
import javafx.stage.Stage;
import se.kth.jdbclab.labb.controller.MainController;
import se.kth.jdbclab.labb.model.Database;
import se.kth.jdbclab.labb.view.MainView;

/**
 * The entry point of the JavaFX application. This class initializes and starts the application.
 */
public class HelloApplication extends Application {

    /**
     * Starts the JavaFX application by initializing the model, controller, and view.
     *
     * @param primaryStage The primary stage for this application.
     */
    @Override
    public void start(Stage primaryStage) {
        Database db = new Database();
        MainController controller = new MainController(db);
        MainView view = new MainView(primaryStage, controller);
    }

    /**
     * The main method that launches the application.
     *
     * @param args The command-line arguments passed to the application.
     */
    public static void main(String[] args) {
        launch(args);
    }
}