package org.graph.graphproject;

import java.util.*;

public class AStar {
    private static final double SCALE_FACTOR = 0.1073;

    public enum HeuristicType {
        MANHATTAN,
        EUCLIDEAN,
        NONE // Basically Dijkstra
    }

    /**
     * Finds the shortest path between start and target vertices using A* algorithm.
     *
     * @param start         The starting vertex.
     * @param target        The destination vertex.
     * @param heuristicType The type of heuristic to use (MANHATTAN, EUCLIDEAN).
     * @return A list of vertices representing the path from start to target. Returns empty list if no path found.
     */
    public static List<Vertex> findPath(Vertex start, Vertex target, HeuristicType heuristicType) {
        // Map to store the cost of the cheapest path from start to a node (g score)
        Map<Vertex, Double> gScore = new HashMap<>();

        // Map to store the estimated total cost from start to goal through a node (f score = g + h)
        Map<Vertex, Double> fScore = new HashMap<>();

        // Map to keep track of the path (predecessor of each node)
        Map<Vertex, Vertex> cameFrom = new HashMap<>();

        // Initialize scores with infinity
        // We don't need to initialize all vertices in the graph if we use getOrDefault with infinity
        // But to match the style of Dijkstra.java in this project, we can rely on the maps.

        gScore.put(start, 0.0);
        fScore.put(start, calculateHeuristic(start, target, heuristicType));

        // Priority queue to explore nodes with the lowest fScore first
        PriorityQueue<Vertex> openSet = new PriorityQueue<>(Comparator.comparingDouble(v -> fScore.getOrDefault(v, Double.POSITIVE_INFINITY)));
        openSet.add(start);

        // Set to keep track of visited nodes to avoid processing them multiple times unnecessarily
        // Although A* with consistent heuristic handles re-expansion, keeping track of closed set or checking gScore is good.
        // In this implementation, checking if we found a better gScore is sufficient.

        while (!openSet.isEmpty()) {
            Vertex current = openSet.poll();

            // If we reached the target, reconstruct and return the path
            if (current.equals(target)) {
                return reconstructPath(cameFrom, current);
            }

            for (Edge edge : current.getEdges()) {
                Vertex neighbor = edge.getTarget();
                double tentativeGScore = gScore.getOrDefault(current, Double.POSITIVE_INFINITY) + edge.getDistance();

                if (tentativeGScore < gScore.getOrDefault(neighbor, Double.POSITIVE_INFINITY)) {
                    // This path to neighbor is better than any previous one. Record it!
                    cameFrom.put(neighbor, current);
                    gScore.put(neighbor, tentativeGScore);
                    double h = calculateHeuristic(neighbor, target, heuristicType);
                    fScore.put(neighbor, tentativeGScore + h);

                    // If neighbor is not in openSet, add it.
                    // PriorityQueue doesn't update priority automatically if key changes,
                    // so we usually remove and add, or add duplicates and handle visited.
                    // Simplest for Java PQ is to add (or remove-then-add).
                    openSet.remove(neighbor); // O(n) but safe
                    openSet.add(neighbor);
                }
            }
        }

        // Return empty list if failure
        return new ArrayList<>();
    }

    /**
     * Reconstructs the path from the cameFrom map.
     *
     * @param cameFrom The map of predecessors.
     * @param current  The current node (target).
     * @return The path as a list of vertices.
     */
    private static List<Vertex> reconstructPath(Map<Vertex, Vertex> cameFrom, Vertex current) {
        List<Vertex> totalPath = new ArrayList<>();
        totalPath.add(current);
        while (cameFrom.containsKey(current)) {
            current = cameFrom.get(current);
            totalPath.add(current);
        }
        Collections.reverse(totalPath);
        return totalPath;
    }

    /**
     * Calculates the heuristic value (estimated cost from current to target).
     *
     * @param current       The current vertex.
     * @param target        The target vertex.
     * @param heuristicType The type of heuristic to apply.
     * @return The heuristic value.
     */
    private static double calculateHeuristic(Vertex current, Vertex target, HeuristicType heuristicType) {
        double dx = Math.abs(current.getX() - target.getX());
        double dy = Math.abs(current.getY() - target.getY());

        return switch (heuristicType) {
            case MANHATTAN ->
                // Manhattan distance: |x1 - x2| + |y1 - y2|
                    (dx + dy) * SCALE_FACTOR;
            case EUCLIDEAN ->
                // Euclidean distance: sqrt(dx^2 + dy^2)
                    Math.sqrt(dx * dx + dy * dy) * SCALE_FACTOR;
            default -> 0.0;
        };
    }
}
