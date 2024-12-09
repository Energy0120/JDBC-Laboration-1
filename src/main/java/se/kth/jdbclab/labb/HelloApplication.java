package se.kth.jdbclab.labb;

import javafx.application.Application;
import javafx.stage.Stage;
import se.kth.jdbclab.labb.controller.MainController;
import se.kth.jdbclab.labb.model.Database;
import se.kth.jdbclab.labb.view.MainView;
import se.kth.jdbclab.labb.controller.LibraryController;

public class HelloApplication extends Application {

    @Override
    public void start(Stage primaryStage) {
        Database db = new Database();
        MainController controller = new MainController(db);
        MainView view = new MainView(primaryStage, controller);
    }

    public static void main(String[] args) {
        launch(args);
    }
}


/*
public void start(Stage primaryStage) {
    Database db = new Database;
    MainController controller = new MainController(db);
    MainView view = new MainView(primaryStage, controller);
}*/