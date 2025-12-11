package com.ada.flightsproject;

import com.ada.flightsproject.dataStructures.FlightGraph;
import com.ada.flightsproject.dataStructures.FlightGraph.Flight;
import com.ada.flightsproject.utility.Utility;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.Set;

public class MainController {

    @FXML private ComboBox<String> sourceCombo;
    @FXML private ComboBox<String> destCombo;
    @FXML private ComboBox<String> dayCombo;
    @FXML private Spinner<Integer> layoverSpinner;
    @FXML private Button searchButton;
    @FXML private Button resetButton;
    @FXML private TextField departTimeField;
    @FXML private Button seeAllFlightsButton;

    @FXML private TableView<FlightRow> resultsTable;
    @FXML private TableColumn<FlightRow, String> colFrom;
    @FXML private TableColumn<FlightRow, String> colTo;
    @FXML private TableColumn<FlightRow, String> colDepart;
    @FXML private TableColumn<FlightRow, String> colArrive;
    @FXML private TableColumn<FlightRow, String> colDuration;

    @FXML private Label summaryLabel;
    @FXML private ProgressIndicator progressIndicator; // optional in FXML

    private FlightGraph graph;

    @FXML
    public void initialize() {
        // table column bindings
        colFrom.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().from));
        colTo.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().to));
        colDepart.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().depart));
        colArrive.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().arrive));
        colDuration.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().duration));

        // spinner defaults: 0..1440, default 60 minutes
        layoverSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 1440, 60, 15));

        // Fill days (so UI always has them)
        dayCombo.getItems().addAll(
                "Monday","Tuesday","Wednesday","Thursday","Friday","Saturday","Sunday"
        );

        resultsTable.setPlaceholder(new Label("No results"));
        summaryLabel.setText("Ready");
        if (progressIndicator != null) progressIndicator.setVisible(false);
    }

    public void setGraph(FlightGraph graph) {
        this.graph = graph;
        // populate airport combos from CSV (safe; no backend change)
        Set<String> airports = loadAirportsFromCsv("/com/ada/flightsproject/data/FlightPathData.csv");
        javafx.collections.ObservableList<String> list = javafx.collections.FXCollections.observableArrayList(airports);
        javafx.collections.FXCollections.sort(list);
        sourceCombo.setItems(list);
        destCombo.setItems(list);
    }

    private Set<String> loadAirportsFromCsv(String resourcePath) {
        java.util.Set<String> set = new java.util.HashSet<>();
        try (java.io.InputStream is = getClass().getResourceAsStream(resourcePath)) {
            if (is == null) return set;
            java.io.BufferedReader br = new java.io.BufferedReader(new java.io.InputStreamReader(is));
            String header = br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                String[] cols = line.split(",");
                if (cols.length >= 2) {
                    set.add(cols[0].trim());
                    set.add(cols[1].trim());
                }
            }
        } catch (Exception e) {
            Platform.runLater(() -> summaryLabel.setText("Could not load airports list."));
            e.printStackTrace();
        }
        return set;
    }

    @FXML
    private void onSearchClicked() {
        String src = sourceCombo.getValue();
        String dst = destCombo.getValue();
        String day = dayCombo.getValue();
        

        if (src == null || dst == null || day == null) {
            summaryLabel.setText("Please fill all inputs (From, To, Day).");
            return;
        }
        if (src.equals(dst)) {
            summaryLabel.setText("Source and destination cannot be the same.");
            return;
        }

        // Start at midnight of selected day (user does not type time)
        int startTime;
        try {
            startTime = Utility.computeDepartureArrivalMinutes(day, "00:00", 0)[0];
        } catch (Exception ex) {
            summaryLabel.setText("Internal time convert error.");
            return;
        }

        int layover = layoverSpinner.getValue();

        // disable UI while searching
        searchButton.setDisable(true);
        resetButton.setDisable(true);
        summaryLabel.setText("Searching...");
        if (progressIndicator != null) progressIndicator.setVisible(true);

        Task<FlightGraph.Result> task = new Task<>() {
            @Override
            protected FlightGraph.Result call() {
                return graph.earliestArrival(src, dst, startTime, layover);
            }
        };

        task.setOnSucceeded(evt -> {
            FlightGraph.Result res = task.getValue();
            if (progressIndicator != null) progressIndicator.setVisible(false);
            searchButton.setDisable(false);
            resetButton.setDisable(false);

            if (res == null || res.arrivalTime == Integer.MAX_VALUE) {
                summaryLabel.setText("No route found.");
                resultsTable.getItems().clear();
                return;
            }

            displayResult(res);
        });

        task.setOnFailed(evt -> {
            if (progressIndicator != null) progressIndicator.setVisible(false);
            searchButton.setDisable(false);
            resetButton.setDisable(false);
            summaryLabel.setText("Search failed: " + (task.getException() != null ? task.getException().getMessage() : "unknown"));
            if (task.getException() != null) task.getException().printStackTrace();
        });

        Thread t = new Thread(task, "dijkstra-search");
        t.setDaemon(true);
        t.start();
    }

    @FXML
    private void onSeeAllFlightsClicked(ActionEvent event) {
        if (graph == null) {
            // Safety: graph not loaded
            summaryLabel.setText("Graph not loaded.");
            return;
        }

        // Collect all flights from the graph
        ObservableList<FlightRow> rows = FXCollections.observableArrayList();

        for (Flight f : graph.getAllFlights()) {
            // Depart / arrive in week-minutes -> (Day, HH:MM)
            String[] dep = Utility.computeMinutesToDayAndTime(f.depart);
            String[] arr = Utility.computeMinutesToDayAndTime(f.arrive);

            String departStr = dep[0] + " " + dep[1];  // e.g. "Monday 09:30"
            String arriveStr = arr[0] + " " + arr[1];  // e.g. "Monday 13:45"

            int durationMinutes = f.arrive - f.depart;
            String durationStr = durationMinutes + " min";

            rows.add(new FlightRow(
                    f.from,
                    f.to,
                    departStr,
                    arriveStr,
                    durationStr
            ));
        }

        // Optionally sort by departure time
        rows.sort((a, b) -> a.depart.compareTo(b.depart));

        resultsTable.getItems().setAll(rows);
        summaryLabel.setText("Showing all " + rows.size() + " flights");
    }

    private void displayResult(FlightGraph.Result res) {
        resultsTable.getItems().clear();

        String[] arrival = Utility.computeMinutesToDayAndTime(res.arrivalTime);
        summaryLabel.setText("Arrival: " + arrival[0] + " " + arrival[1] + "  (Hops: " + Math.max(0, res.airports.size()-1) + ")");

        for (Flight f : res.flights) {
            String[] dep = Utility.computeMinutesToDayAndTime(f.depart);
            String[] ari = Utility.computeMinutesToDayAndTime(f.arrive);
            int durMin = f.arrive - f.depart;
            String dur = String.format("%dh %02dm", durMin / 60, durMin % 60);

            resultsTable.getItems().add(new FlightRow(
                    f.from,
                    f.to,
                    dep[0] + " " + dep[1],
                    ari[0] + " " + ari[1],
                    dur
            ));
        }
    }

    @FXML
    private void onResetClicked() {
        sourceCombo.getSelectionModel().clearSelection();
        destCombo.getSelectionModel().clearSelection();
        dayCombo.getSelectionModel().clearSelection();
        layoverSpinner.getValueFactory().setValue(60);
        resultsTable.getItems().clear();
        summaryLabel.setText("Ready");
    }
}
