module com.example.schatv2 {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.schatv2 to javafx.fxml;
    exports com.example.schatv2;

}