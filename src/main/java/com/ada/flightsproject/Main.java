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

        AdjacencyListGraph flightPaths = new AdjacencyListGraph(true);

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


    // East Coast
        flightPaths.addEdge(JFK, LAX, 3970);
        flightPaths.addEdge(JFK, SFO, 4150);
        flightPaths.addEdge(JFK, MIA, 1750);
        flightPaths.addEdge(JFK, ATL, 1220);
        flightPaths.addEdge(BOS, JFK, 300);
        flightPaths.addEdge(BOS, PHL, 440);
        flightPaths.addEdge(EWR, JFK, 33);
        flightPaths.addEdge(LGA, JFK, 16);
        flightPaths.addEdge(IAD, JFK, 360);

    // Midwest
        flightPaths.addEdge(ORD, JFK, 1180);
        flightPaths.addEdge(ORD, DEN, 1420);
        flightPaths.addEdge(ORD, DFW, 1300);
        flightPaths.addEdge(DTW, ORD, 370);

    // South
        flightPaths.addEdge(ATL, CLT, 365);
        flightPaths.addEdge(ATL, MIA, 960);
        flightPaths.addEdge(ATL, DFW, 1300);

    // West Coast
        flightPaths.addEdge(SFO, LAX, 540);
        flightPaths.addEdge(LAX, DEN, 1380);
        flightPaths.addEdge(SFO, OAK, 11);
        flightPaths.addEdge(OAK, LAX, 540);

    // Cross-country extras
        flightPaths.addEdge(MIA, LAX, 3750);
        flightPaths.addEdge(PHL, DEN, 2530);
        flightPaths.addEdge(CLT, BOS, 1150);

        List<Airport> path = flightPaths.bellmanFord(ATL, SFO);
        System.out.println("Path ATL -> SFO: " + path);
        System.out.println("Distance: " + flightPaths.bellmanFordDistance(ATL, SFO));
    }
}
