module com.ada.flightsproject {
    requires javafx.controls;
    requires javafx.fxml;



    opens com.ada.flightsproject to javafx.fxml;
    exports com.ada.flightsproject;
}