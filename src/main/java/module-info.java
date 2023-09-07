module com.example.little_pub {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.little_pub to javafx.fxml;
    exports com.example.little_pub;
}