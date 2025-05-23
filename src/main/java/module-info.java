module com.gdse.chatApplication {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.sql;

    opens com.service.project.controller to javafx.fxml;
    opens com.service.project to javafx.fxml;

    exports com.service.project;
    exports com.service.project.controller;
}