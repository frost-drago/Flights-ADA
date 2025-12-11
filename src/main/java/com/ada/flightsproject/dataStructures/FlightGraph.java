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

    /* Flights from
     * "CGK" -> [Flight(CGK->SIN), Flight(CGK->KUL), Flight(CGK->DPS)]
     * "SIN" -> [Flight(SIN->SYD), Flight(SIN->NRT)]
     * "DPS" -> [Flight(DPS->PER)]
     */
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

    public Collection<Flight> getAllFlights() {
        List<Flight> all = new ArrayList<>();
        for (List<Flight> list : flightsFrom.values()) {
            all.addAll(list);
        }
        return all;
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

        // Initialize known airports with INFINITY
        for (String airport : flightsFrom.keySet()) {
            bestTime.put(airport, Integer.MAX_VALUE);
        }
        bestTime.put(source, startTime);

        // We are currently at airport at time, and we want to explore where we can go from here.
        class State {
            String airport;
            int time;
            State(String airport, int time) { this.airport = airport; this.time = time; }
        }
        PriorityQueue<State> pq = new PriorityQueue<>(Comparator.comparingInt(s -> s.time)); // Take a State object called s, and return s.time.
        pq.add(new State(source, startTime));

        // The main Dijkstra loop
        while (!pq.isEmpty()) {
            State cur = pq.poll(); //retrieves and removes the element at the head of the queue
            String u = cur.airport;
            int time = cur.time;

            // If the state we popped has a time worse than what we already recorded for that airport → discard.
            if (time > bestTime.getOrDefault(u, Integer.MAX_VALUE)) continue;
            // If we already exceed our 2-week cap, stop exploring from here
            if (time > MAX_TIME) continue;
            // Early exit: earliest arrival at target found
            if (u.equals(target)) break; // If u == target → we popped the earliest possible arrival at the target → done.

            // Give me the list of flights that depart from airport u.
            // If u has no outgoing flights, just return an empty list instead of null.
            List<Flight> outgoing = flightsFrom.getOrDefault(u, Collections.emptyList());
            // We iterate over all outgoing flights from u
            for (Flight f : outgoing) {
                // Base departure within a week (0..WEEK-1)
                int depBase = f.depart % WEEK;
                int flightDuration = f.arrive - f.depart; // should be > 0

                // When are we allowed to depart next?
                // We need to respect layover from current time
                int earliestAllowed = time + minLayover;
                // EXAMPLE. If earliestAllowed is Monday 10:30 in week 3:
                // - allowedWeekStart = Monday 00:00 of week 3
                // - allowedMod = 10:30 in minutes
                int allowedMod = earliestAllowed % WEEK;
                int allowedWeekStart = earliestAllowed - allowedMod;

                // Decide whether we catch it this week or next week
                int candidateDep;
                if (depBase >= allowedMod) {
                    // catch it in this weekly cycle
                    // EXAMPLE.
                    // Flight: Wednesday 14:00
                    // depBase = Wed 14:00
                    // earliestAllowed = Wed 13:00 → allowedMod = Wed 13:00
                    // depBase >= allowedMod → we can catch this same week → candidateDep is weekStart + Wed 14:00.
                    candidateDep = allowedWeekStart + depBase;
                } else {
                    // need to wait until next week's occurrence
                    candidateDep = allowedWeekStart + WEEK + depBase;
                }
                // If the earliest flight we can catch is already beyond our allowed window, we discard this edge.
                if (candidateDep > MAX_TIME) {
                    // Too far in future
                    continue;
                }

                // Compute arrival time for this instance of the flight
                int candidateArr = candidateDep + flightDuration;
                if (candidateArr > MAX_TIME) {
                    // Arrival past our 2-week window
                    continue;
                }

                // If going from u to f.to via this flight results in an arrival time better than the best we have for f.to,
                // ... update and push to PQ.
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

        // When we’re done: check if we reached target
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
