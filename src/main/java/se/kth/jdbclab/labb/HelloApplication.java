package se.kth.jdbclab.labb;

import javafx.application.Application;
import javafx.stage.Stage;
import se.kth.jdbclab.labb.view.MainView;
import se.kth.jdbclab.labb.controller.BookController;

public class HelloApplication extends Application {

    @Override
    public void start(Stage primaryStage) {
        MainView view = new MainView(primaryStage);
        new BookController(view);
    }

    public static void main(String[] args) {
        launch(args);
    }
}