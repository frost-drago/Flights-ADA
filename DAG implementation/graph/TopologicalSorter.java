package graph;

import model.Airport;
import model.FlightEdge;

import java.util.*;

/**
 * TopologicalSorter provides methods to compute a topological ordering of the nodes in DAG
 * Implementation uses Kahn's algorithm (BFS-style).
 * If the graph contains a cycle, the returned list will have size less than the number of nodes;
 * as a result -> implementation throws an IllegalStateException.
 */
public class TopologicalSorter {

    /**
     * This produces a topological ordering of the graph's nodes.
     * the adjacency-list graph returns list of airports in topological order (size == number of nodes)
     * IllegalStateException if the graph contains a cycle
     */
    public List<Airport> topologicalSort(AdjacencyListGraph graph) {
        // Compute indegree for every node
        Map<Airport, Integer> indegree = new HashMap<>();
        for (Airport node : graph.getAllNodes()) {
            indegree.put(node, 0);
        }

        for (Airport u : graph.getAllNodes()) {
            for (FlightEdge edge : graph.getNeighbors(u)) {
                Airport v = edge.getDestination();
                indegree.put(v, indegree.getOrDefault(v, 0) + 1);
            }
        }

        // Collect nodes with indegree == 0
        Deque<Airport> queue = new ArrayDeque<>();
        for (Map.Entry<Airport, Integer> e : indegree.entrySet()) {
            if (e.getValue() == 0) queue.add(e.getKey());
        }

        List<Airport> order = new ArrayList<>(indegree.size());

        while (!queue.isEmpty()) {
            Airport u = queue.removeFirst();
            order.add(u);

            for (FlightEdge edge : graph.getNeighbors(u)) {
                Airport v = edge.getDestination();
                int d = indegree.get(v) - 1;
                indegree.put(v, d);
                if (d == 0) queue.addLast(v);
            }
        }

        // not all nodes processed, there's a cycle
        if (order.size() != indegree.size()) {
            throw new IllegalStateException("Graph contains a cycle — topological ordering not possible.");
        }

        return order;
    }

    public boolean isDAG(AdjacencyListGraph graph) {
        try {
            topologicalSort(graph);
            return true;
        } catch (IllegalStateException ex) {
            return false;
        }
    }
}

