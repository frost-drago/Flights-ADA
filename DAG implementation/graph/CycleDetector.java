package graph;

import model.Airport;
import model.FlightEdge;

import java.util.*;

// This detects cycles in a directed graph using DFS.

public class CycleDetector {

    /**
     * Returns true if the graph contains a cycle.
     */
    public boolean hasCycle(AdjacencyListGraph graph) {
        Set<Airport> visited = new HashSet<>();
        Set<Airport> recStack = new HashSet<>();

        for (Airport airport : graph.getAllNodes()) {
            if (!visited.contains(airport)) {
                if (dfs(airport, graph, visited, recStack)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * DFS helper implemented to detect a back-edge + a cycle
     * A back-edge is an edge that points from a node back to one of its ancestors
     *  which creates a cycle in the graph
     */
    private boolean dfs(Airport node,
                        AdjacencyListGraph graph,
                        Set<Airport> visited,
                        Set<Airport> recStack) {

        visited.add(node);
        recStack.add(node);

        for (FlightEdge edge : graph.getNeighbors(node)) {
            Airport neighbor = edge.getDestination();

            if (!visited.contains(neighbor)) {
                if (dfs(neighbor, graph, visited, recStack)) {
                    return true; // cycle found
                }
            } else if (recStack.contains(neighbor)) {
                return true; // back-edge detected
            }
        }

        recStack.remove(node);
        return false;
    }
}

