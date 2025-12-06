package com.ada.flightsproject;

import com.ada.flightsproject.dataStructures.FlightGraph;
import com.ada.flightsproject.model.Airport;
import com.ada.flightsproject.utility.Utility;


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



    }
}
