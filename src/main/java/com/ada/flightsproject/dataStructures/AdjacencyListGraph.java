package com.ada.flightsproject.dataStructures;

import java.util.*;

/**
 * This class is a weighted graph.
 */
public class AdjacencyListGraph<T> {

    // Internal edge class
    private static class Edge<T> {
        T to;
        int duration;
        int departureTime;
        int arrivalTime;
        int day;

        Edge(T to, int duration, int departureTime, int arrivalTime, int day) {
            this.to = to;
            this.duration = duration;
            this.departureTime = departureTime;
            this.arrivalTime = arrivalTime;
            this.day = day;
        }
    }

    private final Map<T, List<Edge<T>>> adj;

    /**
     * Create a new graph.
     */
    public AdjacencyListGraph() {
        this.adj = new HashMap<>();
    }

    /**
     * Add a node if it doesn't exist yet.
     */
    public void addNode(T node) {
        adj.putIfAbsent(node, new ArrayList<>());
    }

    /**
     * Add an edge u -> v with a given duration.
     * If the nodes don't exist yet, they'll be created.
     * If graph is undirected, also adds v -> u.
     */
    public void addEdge(T from, T to, int duration, int departureTime, int arrivalTime, int day) {
        addNode(from);
        addNode(to);

        adj.get(from).add(new Edge<>(to, duration, departureTime, arrivalTime, day));
    }

    /**
     * Remove an edge u -> v.
     * If undirected, also removes v -> u.
     */
    public void removeEdge(T from, T to) {
        List<Edge<T>> edgesFrom = adj.get(from);
        if (edgesFrom != null) {
            edgesFrom.removeIf(e -> e.to.equals(to));
        }
    }

    /**
     * Remove a node completely:
     * - delete it from the map
     * - delete all edges pointing to it
     */
    public void removeNode(T node) {
        // Remove all outgoing edges from this node
        adj.remove(node);

        // Remove all incoming edges pointing to this node
        for (List<Edge<T>> edges : adj.values()) {
            edges.removeIf(e -> e.to.equals(node));
        }
    }

    /**
     * Get all neighbors of a node. Returns Map<neighbor, weight>.
     * If node doesn't exist, returns empty map.
     */
    public Map<T, Integer> getNeighbors(T node) {
        Map<T, Integer> res = new HashMap<>();
        List<Edge<T>> edges = adj.get(node);
        if (edges != null) {
            for (Edge<T> e : edges) {
                res.put(e.to, e.duration);
            }
        }
        return res;
    }

    /**
     * Get all flights u -> v as (duration, departureTime) pairs.
     * Returns an empty list if no such edge.
     */
    public List<Map.Entry<Integer, Integer>> getDurations(T from, T to) {
        List<Map.Entry<Integer, Integer>> result = new ArrayList<>();
        List<Edge<T>> edges = adj.get(from);
        if (edges == null) return result;

        for (Edge<T> e : edges) {
            if (e.to.equals(to)) {
                // key = duration, value = departureTime
                result.add(new AbstractMap.SimpleEntry<>(e.duration, e.departureTime));
            }
        }
        return result;
    }


    /**
     * Get all nodes currently in the graph.
     */
    public Set<T> getNodes() {
        return adj.keySet();
    }

    /* Dijkstra's */


}
