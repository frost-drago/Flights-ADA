package com.ada.flightsproject;

import com.ada.flightsproject.dataStructures.FlightGraph;
import com.ada.flightsproject.utility.Utility;

public class TestFlightGraph {
    public static void main(String[] args) {
        FlightGraph g = new FlightGraph();

        // Helper lambda for HH:MM → minutes
        java.util.function.Function<String, Integer> toMin = s -> {
            String[] p = s.split(":");
            return Integer.parseInt(p[0]) * 60 + Integer.parseInt(p[1]);
        };

        // Add flights
        g.addFlight("SUB", "CGK", toMin.apply("08:00"), toMin.apply("09:30"));
        g.addFlight("SUB", "CGK", toMin.apply("09:00"), toMin.apply("10:30"));

        g.addFlight("CGK", "SIN", toMin.apply("08:15"), toMin.apply("10:15"));
        g.addFlight("CGK", "SIN", toMin.apply("10:00"), toMin.apply("12:30"));

        g.addFlight("CGK", "KUL", toMin.apply("09:45"), toMin.apply("11:15"));
        g.addFlight("KUL", "SIN", toMin.apply("11:30"), toMin.apply("13:00"));

        // Run earliest arrival, min layover = 0 for simplicity
        FlightGraph.Result result =
                g.earliestArrival("SUB", "SIN", toMin.apply("08:00"), 0);

        // Pretty print results
        System.out.println("Best route: " + result.path);
        System.out.println("Arrival time: " + toHHMM(result.arrivalTime));


    }

    // Time formatting helper
    static String toHHMM(int minutes) {
        int h = minutes / 60;
        int m = minutes % 60;
        return String.format("%02d:%02d", h, m);
    }
}
