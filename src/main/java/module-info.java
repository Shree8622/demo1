module com.example.demo1 {
    requires javafx.controls;
    requires javafx.fxml;
    requires opencv;
    requires java.sql;
    requires sqlite.jdbc;
    requires java.desktop;
    requires javafx.swing;


    opens com.example.demo1 to javafx.fxml;
    exports com.example.demo1;
}