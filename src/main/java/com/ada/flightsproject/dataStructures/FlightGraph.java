package com.ada.flightsproject.dataStructures;

import com.ada.flightsproject.utility.Utility;
import java.util.*;

public class FlightGraph {

    static class Flight {
        String from;
        String to;
        int depart; // minutes from start of week
        int arrive; // minutes from start of week

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

    public static class Result {
        public final List<String> path;
        public final int arrivalTime;

        Result(List<String> path, int arrivalTime) {
            this.path = path;
            this.arrivalTime = arrivalTime;
        }
    }

    /**
     * Simple earliest-arrival Dijkstra assuming:
     * - startTime is also in "minutes from start of week"
     * - flights do NOT repeat weekly (that’s the next upgrade)
     */
    public Result earliestArrival(String source, String target, int startTime, int minLayover) {
        Map<String, Integer> bestTime = new HashMap<>();
        Map<String, String> prev = new HashMap<>();

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

            if (time > bestTime.getOrDefault(u, Integer.MAX_VALUE)) continue;
            if (u.equals(target)) break;

            List<Flight> outgoing = flightsFrom.getOrDefault(u, Collections.emptyList());
            for (Flight f : outgoing) {
                // Can only catch flights departing after we arrive + layover
                if (f.depart >= time + minLayover) {
                    int newTime = f.arrive;
                    if (newTime < bestTime.getOrDefault(f.to, Integer.MAX_VALUE)) {
                        bestTime.put(f.to, newTime);
                        prev.put(f.to, u);
                        pq.add(new State(f.to, newTime));
                    }
                }
            }
        }

        int finalTime = bestTime.getOrDefault(target, Integer.MAX_VALUE);
        if (finalTime == Integer.MAX_VALUE) {
            return new Result(Collections.emptyList(), Integer.MAX_VALUE);
        }

        // reconstruct path of airports
        List<String> path = new ArrayList<>();
        String cur = target;
        while (cur != null) {
            path.add(cur);
            cur = prev.get(cur);
        }
        Collections.reverse(path);

        return new Result(path, finalTime);
    }

    // Might still be useful elsewhere.
    int toMinutes(String hhmm) {
        String[] parts = hhmm.split(":");
        return Integer.parseInt(parts[0]) * 60 + Integer.parseInt(parts[1]);
    }

}
