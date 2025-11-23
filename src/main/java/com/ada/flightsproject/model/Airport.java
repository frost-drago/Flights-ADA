package com.ada.flightsproject.model;

public class Airport {
    private final String airportCode;

    public Airport(String airportCode) {
        this.airportCode = airportCode;
    }

    public String getAirportCode() {
        return airportCode;
    }

    @Override
    public String toString() {
        return "Airport{" +
                "airportCode='" + airportCode + "'}";
    }
}
