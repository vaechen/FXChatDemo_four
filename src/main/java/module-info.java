module org.example.fxchatdemo {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires java.sql;
    requires mysql.connector.java;

    opens org.example.fxchatdemo to javafx.fxml;
    exports org.example.fxchatdemo;

    opens org.example.fxchatdemo.controllers to javafx.fxml;
    exports org.example.fxchatdemo.controllers;
}