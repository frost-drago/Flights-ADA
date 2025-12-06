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

        // t2[0] = day
        // t2[1] = HH:MM
        String[] t2 = Utility.minutesToDayAndTime(1570);
        System.out.println(t2[0] + " " + t2[1]);

        FlightGraph graph = new FlightGraph();

        FlightGraphLoader.loadFlights(graph, "/com/ada/flightsproject/data/FlightPathData.csv");

        System.out.println("Loaded all flights!");

        // Now you can run searches:
        int start = Utility.computeDepartureArrivalMinutes("Monday", "08:00", 0)[0];
        FlightGraph.Result r = graph.earliestArrival("KUL", "KIX", start, 60);

        if (r.arrivalTime == Integer.MAX_VALUE) {
            System.out.println("No route found.");
        } else {
            System.out.println("Airports: " + r.airports);
            for (FlightGraph.Flight f : r.flights) {
                System.out.println(f.from + " -> " + f.to
                        + " depart= " + Utility.minutesToDayAndTime(f.depart)[0] + " " + Utility.minutesToDayAndTime(f.depart)[1]
                        + " | arrive= " + Utility.minutesToDayAndTime(f.arrive)[0] + " " + Utility.minutesToDayAndTime(f.arrive)[1]);
            }
        }


    }
}
