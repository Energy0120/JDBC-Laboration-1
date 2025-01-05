module se.kth.jdbclab.labb {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;
    requires mysql.connector.j;
    requires jdk.compiler;
    requires org.mongodb.driver.core;
    requires org.mongodb.bson;
    requires org.mongodb.driver.sync.client;


    opens se.kth.jdbclab.labb to javafx.fxml;
    exports se.kth.jdbclab.labb;
}