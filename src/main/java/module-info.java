module se.kth.jdbclab.labb {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens se.kth.jdbclab.labb to javafx.fxml;
    exports se.kth.jdbclab.labb;
}