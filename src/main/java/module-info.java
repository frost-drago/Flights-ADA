module com.ada.flightsproject {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;

    opens com.ada.flightsproject to javafx.fxml;
    exports com.ada.flightsproject;
}