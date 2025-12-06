package com.ada.flightsproject;

import com.ada.flightsproject.utility.Utility;
import com.ada.flightsproject.dataStructures.FlightGraph;
import com.ada.flightsproject.data.FlightGraphLoader;

/*
    For command line stuff and testing
*/
public class Main {
    public static void main(String[] args) {

        int[] t = Utility.computeDepartureArrivalMinutes("Sunday", "23:40", 160);
        // t[0] = departure week minute
        // t[1] = arrival week minute
        System.out.println("Dep: " + t[0]);
        System.out.println("Arr: " + t[1]);

        // r[0] = day
        // r[1] = HH:MM
        String[] r = Utility.minutesToDayAndTime(1570);
        System.out.println(r[0] + " " + r[1]);

        FlightGraph graph = new FlightGraph();

        FlightGraphLoader.loadFlights(
                graph,
                "/com/ada/flightsproject/data/FlightPathData.csv"
        );

        System.out.println("Loaded all flights!");

        // Now you can run searches:
        int startTime = Utility.computeDepartureArrivalMinutes("Monday", "08:00", 0)[0];
        FlightGraph.Result a = graph.earliestArrival("CGK", "SYD", startTime, 60);
        System.out.println("Arrives at: " + a.arrivalTime);

    }
}
