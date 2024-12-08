module se.kth.jdbclab.labb {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;
    requires mysql.connector.j;


    opens se.kth.jdbclab.labb to javafx.fxml;
    exports se.kth.jdbclab.labb;
}