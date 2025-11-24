package com.ada.flightsproject;

import com.ada.flightsproject.dataStructures.AdjacencyListGraph;
import com.ada.flightsproject.model.Airport;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/*
    For command line stuff and testing
*/
public class Main {
    public static void main(String[] args) {

        /*
        // Testing making airports
        AdjacencyListGraph flightPaths = new AdjacencyListGraph(true);
        String resourcePath = "/com/ada/flightsproject/data/airportCodes.txt";

        try (InputStream inputStream = Main.class.getResourceAsStream(resourcePath);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    flightPaths.addNode(new Airport(line));
                }
            }
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
            System.err.println("Failed to load airport codes from: " + resourcePath);
        }

        System.out.println(flightPaths.getNodes());
         */

        AdjacencyListGraph flightPaths = new AdjacencyListGraph();

        // TODO: Make connections
        // After loading airport nodes:
        Airport ATL = new Airport("ATL");
        Airport BOS = new Airport("BOS");
        Airport CLT = new Airport("CLT");
        Airport DEN = new Airport("DEN");
        Airport DFW = new Airport("DFW");
        Airport DTW = new Airport("DTW");
        Airport EWR = new Airport("EWR");
        Airport IAD = new Airport("IAD");
        Airport JFK = new Airport("JFK");
        Airport LAX = new Airport("LAX");
        Airport LGA = new Airport("LGA");
        Airport MIA = new Airport("MIA");
        Airport OAK = new Airport("OAK");
        Airport ORD = new Airport("ORD");
        Airport PHL = new Airport("PHL");
        Airport SFO = new Airport("SFO");

    }
}
