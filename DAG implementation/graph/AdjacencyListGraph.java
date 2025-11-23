package graph;

import model.Airport;
import model.FlightEdge;

import java.util.*;

/**
 * Directed graph implemented using adjacency lists.
 * Each Airport maps to a list of its outgoing FlightEdge objects.
 * adjacency lists chosen because flight networks are sparse
 * most airports don't have direct flights to most other airports.
 * This saves memory especially when dealing with the full dataset.
 */
public class AdjacencyListGraph {

    // airport → outgoing edges
    private final Map<Airport, List<FlightEdge>> adjacency;

    public AdjacencyListGraph() {
        this.adjacency = new HashMap<>();
    }

    /**
     * Ensures the airport exists in the adjacency map.
     * Using putIfAbsent to avoid overwriting existing edge lists
     */
    public void addNode(Airport airport) {
        adjacency.putIfAbsent(airport, new ArrayList<>());
    }

    /**
     * Adds a directed edge (origin → destination) with the given weight.
     * Note: We need to add both nodes first to handle isolated airports.
     * isolated airports = airports that might only have incoming or only outgoing flights
     */
    public void addEdge(Airport origin, Airport destination, double weight) {
        addNode(origin);
        addNode(destination);
        adjacency.get(origin).add(new FlightEdge(origin, destination, weight));
    }

    /**
     * Returns all outgoing edges from a given airport.
     * Returns empty list if airport has no outgoing flights
     */
    public List<FlightEdge> getNeighbors(Airport airport) {
        return adjacency.getOrDefault(airport, new ArrayList<>());
    }

    // Returns all airports (nodes) in the graph.

    public Set<Airport> getAllNodes() {
        return adjacency.keySet();
    }

    // Helper method for debugging - prints the graph structure.

    public void printGraph() {
        for (Airport a : adjacency.keySet()) {
            System.out.println(a.getId() + " -> " + adjacency.get(a));
        }
    }
}
