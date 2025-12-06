package com.ada.flightsproject.data;

import com.ada.flightsproject.dataStructures.FlightGraph;
import com.ada.flightsproject.utility.Utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FlightGraphLoader {

    /**
     * Loads the CSV file from resources and populates the FlightGraph.
     *
     * @param graph The graph to populate
     * @param resourcePath e.g. "/com/ada/flightsproject/data/FlightPathData.csv"
     */
    public static void loadFlights(FlightGraph graph, String resourcePath) {
        try {
            InputStream is = FlightGraphLoader.class.getResourceAsStream(resourcePath);
            if (is == null) {
                throw new RuntimeException("Could not find resource: " + resourcePath);
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line = br.readLine(); // skip header

            while ((line = br.readLine()) != null) {
                String[] cols = line.split(",");

                String start       = cols[0].trim(); // Starting_Airport
                String dest        = cols[1].trim(); // Destination_Airport
                int duration       = Integer.parseInt(cols[2].trim()); // travel_duration_(minutes)
                String depTime     = cols[3].trim(); // departure_time
                String dayOfFlight = cols[5].trim(); // day_of_flight

                graph.addFlightFromCsv(start, dest, dayOfFlight, depTime, duration);
            }

            br.close();
        } catch (IOException e) {
            throw new RuntimeException("Error reading CSV file: " + e.getMessage(), e);
        }
    }
}
