package com.ada.flightsproject.dataStructures;

import java.util.*;

/**
 * This class is a weighted graph.
 */
public class AdjacencyListGraph<T> {

    // Internal edge class
    private static class Edge<T> {
        T to;
        double weight;

        Edge(T to, double weight) {
            this.to = to;
            this.weight = weight;
        }
    }

    private final Map<T, List<Edge<T>>> adj;
    private final boolean directed;

    /**
     * Create a new graph.
     * @param directed true for directed graph, false for undirected
     */
    public AdjacencyListGraph(boolean directed) {
        this.directed = directed;
        this.adj = new HashMap<>();
    }

    /**
     * Add a node if it doesn't exist yet.
     */
    public void addNode(T node) {
        adj.putIfAbsent(node, new ArrayList<>());
    }

    /**
     * Add an edge u -> v with a given weight.
     * If the nodes don't exist yet, they'll be created.
     * If graph is undirected, also adds v -> u.
     */
    public void addEdge(T from, T to, double weight) {
        addNode(from);
        addNode(to);

        adj.get(from).add(new Edge<>(to, weight));

        if (!directed) {
            adj.get(to).add(new Edge<>(from, weight));
        }
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

        if (!directed) {
            List<Edge<T>> edgesTo = adj.get(to);
            if (edgesTo != null) {
                edgesTo.removeIf(e -> e.to.equals(from));
            }
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
    public Map<T, Double> getNeighbors(T node) {
        Map<T, Double> res = new HashMap<>();
        List<Edge<T>> edges = adj.get(node);
        if (edges != null) {
            for (Edge<T> e : edges) {
                res.put(e.to, e.weight);
            }
        }
        return res;
    }

    /**
     * Check if an edge u -> v exists, and return its weight if yes.
     * Returns null if no such edge.
     */
    public Double getWeight(T from, T to) {
        List<Edge<T>> edges = adj.get(from);
        if (edges == null) return null;
        for (Edge<T> e : edges) {
            if (e.to.equals(to)) {
                return e.weight;
            }
        }
        return null;
    }

    /**
     * Get all nodes currently in the graph.
     */
    public Set<T> getNodes() {
        return adj.keySet();
    }

    /* Djikstra's, Bellman-Ford, DAG Shortest Path, A* algorithm */

    /**
     * Bellman-Ford shortest path from source to destination.
     *
     * @param source      starting node
     * @param destination target node
     * @return list of nodes representing the shortest path from source to destination
     *         (including both ends), or null if destination is unreachable.
     * @throws IllegalArgumentException if source or destination is not in the graph
     * @throws IllegalStateException    if a negative-weight cycle is reachable from source
     */
    public List<T> bellmanFord(T source, T destination) {
        if (!adj.containsKey(source) || !adj.containsKey(destination)) {
            throw new IllegalArgumentException("Source or destination not in graph");
        }

        // Distance and predecessor maps
        Map<T, Double> dist = new HashMap<>();
        Map<T, T> prev = new HashMap<>();

        // 1. Initialize distances
        for (T node : adj.keySet()) {
            dist.put(node, Double.POSITIVE_INFINITY);
        }
        dist.put(source, 0.0);

        int V = adj.size();

        // 2. Relax all edges |V| - 1 times
        for (int i = 0; i < V - 1; i++) {
            boolean updated = false;

            for (Map.Entry<T, List<Edge<T>>> entry : adj.entrySet()) {
                T u = entry.getKey();
                double du = dist.get(u);
                if (du == Double.POSITIVE_INFINITY) continue; // unreachable so far

                for (Edge<T> e : entry.getValue()) {
                    T v = e.to;
                    double nd = du + e.weight;

                    double current = dist.getOrDefault(v, Double.POSITIVE_INFINITY);
                    if (nd < current) {
                        dist.put(v, nd);
                        prev.put(v, u);
                        updated = true;
                    }
                }
            }

            // Early stop if no update in this pass
            if (!updated) break;
        }

        // 3. Check for negative-weight cycles reachable from source
        for (Map.Entry<T, List<Edge<T>>> entry : adj.entrySet()) {
            T u = entry.getKey();
            double du = dist.get(u);
            if (du == Double.POSITIVE_INFINITY) continue;

            for (Edge<T> e : entry.getValue()) {
                T v = e.to;
                double nd = du + e.weight;
                if (nd < dist.getOrDefault(v, Double.POSITIVE_INFINITY)) {
                    throw new IllegalStateException(
                            "Graph contains a negative-weight cycle reachable from the source");
                }
            }
        }

        // 4. Reconstruct path from source to destination
        if (Double.isInfinite(dist.get(destination))) {
            // unreachable
            return null;
        }

        List<T> path = new ArrayList<>();
        T cur = destination;
        while (cur != null) {
            path.add(0, cur); // prepend
            cur = prev.get(cur);
        }

        return path;
    }

    public Double bellmanFordDistance(T source, T destination) {
        List<T> path = bellmanFord(source, destination);
        if (path == null) return Double.POSITIVE_INFINITY;

        double total = 0.0;
        for (int i = 0; i < path.size() - 1; i++) {
            Double w = getWeight(path.get(i), path.get(i + 1));
            if (w == null) return Double.POSITIVE_INFINITY; // should not happen
            total += w;
        }
        return total;
    }


}
