package com.ada.flightsproject;

import com.ada.flightsproject.dataStructures.FlightGraph;
import com.ada.flightsproject.data.FlightGraphLoader;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        // Load backend graph
        FlightGraph graph = new FlightGraph();
        FlightGraphLoader.loadFlights(graph, "/com/ada/flightsproject/data/FlightPathData.csv");

        FXMLLoader loader = new FXMLLoader(getClass().getResource("MainView.fxml"));
        Parent root = loader.load();

        // Provide the graph to controller
        MainController controller = loader.getController();
        controller.setGraph(graph);

        stage.setScene(new Scene(root, 1000, 600));
        stage.setTitle("Flight Route Finder");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}

