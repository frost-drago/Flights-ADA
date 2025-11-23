package graph;

import model.Airport;
import model.FlightEdge;

import java.util.*;

/**
 * Implements the DAG Shortest Path algorithm using topological ordering.
 * In a DAG with topological ordering, we only need to relax each edge once because we process nodes in dependency order.
 */
public class DAGShortestPath {

    /**
     * Computes shortest path distances from a source airport to all reachable airports.
     *
     * Algorithm steps:
     * Step 1: Get topological ordering of airports
     * Step 2: Initialize all distances to infinity (except source = 0)
     * Step 3: Process airports in topological order, relaxing outgoing edges
     * Step 4: Return distance and predecessor maps
     * result -> contains distance and predecessor maps
     */
    public Result shortestPath(AdjacencyListGraph graph, Airport source) {

        // Step 1: get topological ordering
        // output = the order to process airports
        TopologicalSorter sorter = new TopologicalSorter();
        List<Airport> topoOrder = sorter.topologicalSort(graph);

        // Step 2: initialize distances and predecessors
        Map<Airport, Double> dist = new HashMap<>();
        Map<Airport, Airport> prev = new HashMap<>();

        for (Airport a : graph.getAllNodes()) {
            dist.put(a, Double.POSITIVE_INFINITY); // start with "unreachable"
            prev.put(a, null);
        }
        dist.put(source, 0.0); // distance to source is 0

        // Step 3: process airports in topological order
        for (Airport u : topoOrder) {
            double d_u = dist.get(u);

            // Skip unreachable airports
            // bc there is no path from source to u
            if (d_u == Double.POSITIVE_INFINITY) continue;

            // Relaxing all outgoing edges from u
            for (FlightEdge edge : graph.getNeighbors(u)) {
                Airport v = edge.getDestination();
                double weight = edge.getWeight();

                // Checking if going through u gives a better path to v
                double newDist = d_u + weight;

                if (newDist < dist.get(v)) {
                    // Updating distance and predecessor if there is a better path
                    dist.put(v, newDist);
                    prev.put(v, u);
                }
            }
        }

        return new Result(dist, prev);
    }

    /**
     * Reconstructing the actual path from source to destination by following predecessors.
     * this works backwards from destination to source, then reverses the list.
     * If there is no path, prev.get(destination) will be null ->
     * one node in the path.
     */
    public List<Airport> getPath(Airport destination, Map<Airport, Airport> prev) {
        List<Airport> path = new ArrayList<>();

        // Follow the chain of predecessors backwards
        for (Airport at = destination; at != null; at = prev.get(at)) {
            path.add(at);
        }

        Collections.reverse(path); // flip it to get source -> destination order
        return path;
    }

    /**
     * container to return both distance and predecessor maps together.
     */
    public static class Result {
        public final Map<Airport, Double> distance;
        public final Map<Airport, Airport> predecessor;

        public Result(Map<Airport, Double> distance, Map<Airport, Airport> predecessor) {
            this.distance = distance;
            this.predecessor = predecessor;
        }
    }
}

