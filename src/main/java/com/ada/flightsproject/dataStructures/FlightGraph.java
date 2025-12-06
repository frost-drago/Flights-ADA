package com.ada.flightsproject.dataStructures;

import com.ada.flightsproject.utility.Utility;
import java.util.*;

public class FlightGraph {

    public static class Flight {
        public String from;
        public String to;
        public int depart; // minutes from start of week
        public int arrive; // minutes from start of week

        Flight(String from, String to, int depart, int arrive) {
            this.from = from;
            this.to = to;
            this.depart = depart;
            this.arrive = arrive;
        }
    }

    private final Map<String, List<Flight>> flightsFrom = new HashMap<>();

    /**
     * Core low-level add. Assumes depart/arrive are already
     * in "minutes from start of week" (0.. +infinity).
     */
    public void addFlight(String from, String to, int depart, int arrive) {
        flightsFrom
                .computeIfAbsent(from, k -> new ArrayList<>())
                .add(new Flight(from, to, depart, arrive));
    }

    /**
     * Convenience method to add a flight using the CSV column values.
     *
     * Columns:
     *   - Starting_Airport
     *   - Destination_Airport
     *   - day_of_flight           (e.g. "Monday")
     *   - departure_time          (e.g. "10:30")
     *   - travel_duration_minutes (e.g. 250)
     *
     * We ignore the CSV's arrival_time string and compute arrival from duration.
     */
    public void addFlightFromCsv(
            String startingAirport,
            String destinationAirport,
            String dayOfFlight,
            String departureTime,
            int travelDurationMinutes
    ) {
        // Uses your Utility helper from earlier
        int[] depArr = Utility.computeDepartureArrivalMinutes(
                dayOfFlight,
                departureTime,
                travelDurationMinutes
        );
        int departWeekMinute = depArr[0];
        int arriveWeekMinute = depArr[1];

        addFlight(startingAirport, destinationAirport, departWeekMinute, arriveWeekMinute);
    }

    /*
     * Just the result table, nothing much.
     */
    public static class Result {
        public final List<String> airports;   // sequence of airports
        public final List<Flight> flights;    // sequence of flights actually used
        public final int arrivalTime;         // final arrival time in minutes

        Result(List<String> airports, List<Flight> flights, int arrivalTime) {
            this.airports = airports;
            this.flights = flights;
            this.arrivalTime = arrivalTime;
        }
    }


    /**
     * Earliest-arrival Dijkstra assuming:
     * - startTime is also in "minutes from start of week"
     * - flights repeat weekly
     * “If I start at source at time startTime (minutes from start of week),
     * and flights repeat every week, and I must wait at least minLayover minutes before boarding each next flight,
     * what is the earliest time I can reach target, within 2 weeks?”
     *
     * @param source starting airport code
     * @param target target airport code
     * @param startTime starting time in weekMinutes
     * @param minLayover minutes of layover (from my arrival to the next departure, how long do I need my buffer time?)
     * @return Result {airports, flights, arrivalTime}
     */
    public Result earliestArrival(String source, String target, int startTime, int minLayover) {
        final int MINUTES_IN_DAY = 24 * 60;
        final int WEEK = 7 * MINUTES_IN_DAY;
        final int MAX_TIME = startTime + 2 * WEEK; // cap search to 2 weekly cycles

        Map<String, Integer> bestTime = new HashMap<>();
        Map<String, String> prevAirport = new HashMap<>();
        Map<String, Flight> prevFlight = new HashMap<>();

        // Initialize known airports with INF
        for (String airport : flightsFrom.keySet()) {
            bestTime.put(airport, Integer.MAX_VALUE);
        }
        bestTime.put(source, startTime);

        class State {
            String airport;
            int time;
            State(String airport, int time) { this.airport = airport; this.time = time; }
        }

        PriorityQueue<State> pq = new PriorityQueue<>(Comparator.comparingInt(s -> s.time));
        pq.add(new State(source, startTime));

        while (!pq.isEmpty()) {
            State cur = pq.poll();
            String u = cur.airport;
            int time = cur.time;

            // If we already found a better time for this airport, skip
            if (time > bestTime.getOrDefault(u, Integer.MAX_VALUE)) continue;
            // If we already exceed our 2-week cap, stop exploring from here
            if (time > MAX_TIME) continue;
            // Early exit: earliest arrival at target found
            if (u.equals(target)) break;

            List<Flight> outgoing = flightsFrom.getOrDefault(u, Collections.emptyList());
            for (Flight f : outgoing) {
                // Base departure within a week (0..WEEK-1)
                int depBase = f.depart % WEEK;
                int flightDuration = f.arrive - f.depart; // should be > 0

                // We need to respect layover from current time
                int earliestAllowed = time + minLayover;
                int allowedMod = earliestAllowed % WEEK;
                int allowedWeekStart = earliestAllowed - allowedMod;

                int candidateDep;
                if (depBase >= allowedMod) {
                    // catch it in this weekly cycle
                    candidateDep = allowedWeekStart + depBase;
                } else {
                    // need to wait until next week's occurrence
                    candidateDep = allowedWeekStart + WEEK + depBase;
                }

                if (candidateDep > MAX_TIME) {
                    // Too far in future
                    continue;
                }

                int candidateArr = candidateDep + flightDuration;
                if (candidateArr > MAX_TIME) {
                    // Arrival past our 2-week window
                    continue;
                }

                if (candidateArr < bestTime.getOrDefault(f.to, Integer.MAX_VALUE)) {
                    bestTime.put(f.to, candidateArr);
                    prevAirport.put(f.to, u);

                    // Store the actual instance of the flight we used, with
                    // the concrete departure/arrival time for this occurrence.
                    prevFlight.put(f.to, new Flight(f.from, f.to, candidateDep, candidateArr));

                    pq.add(new State(f.to, candidateArr));
                }
            }
        }

        int finalTime = bestTime.getOrDefault(target, Integer.MAX_VALUE);
        if (finalTime == Integer.MAX_VALUE) {
            return new Result(Collections.emptyList(), Collections.emptyList(), Integer.MAX_VALUE);
        }

        // reconstruct path: airports + flights
        List<String> airportPath = new ArrayList<>();
        List<Flight> usedFlights = new ArrayList<>();

        String curAirport = target;
        while (curAirport != null) {
            airportPath.add(curAirport);
            Flight used = prevFlight.get(curAirport);
            if (used != null) {
                usedFlights.add(used);
            }
            curAirport = prevAirport.get(curAirport);
        }

        Collections.reverse(airportPath);
        Collections.reverse(usedFlights);

        return new Result(airportPath, usedFlights, finalTime);
    }

}
