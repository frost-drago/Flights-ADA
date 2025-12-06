package com.ada.flightsproject;

public class FlightRow {
    public String from;
    public String to;
    public String depart;
    public String arrive;
    public String duration;

    public FlightRow(String from, String to, String depart, String arrive, String duration) {
        this.from = from;
        this.to = to;
        this.depart = depart;
        this.arrive = arrive;
        this.duration = duration;
    }
}
