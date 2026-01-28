package org.graph.graphproject;

import java.util.*;

public class AStar {

    private static record Node(Vertex vertex, double fScore) {}

    /**
     * Finds the shortest path between two vertices using the A* algorithm.
     * Uses Euclidean distance as a heuristic.
     *
     * @param start The starting vertex.
     * @param target The destination vertex.
     * @param graph The graph containing the vertices.
     * @return A PathfindingResult containing the path, explored nodes, time taken, and total cost.
     */
    public static PathfindingResult findShortestPath(Vertex start, Vertex target, Graph graph) {
        if (start == null || target == null) return new PathfindingResult(new ArrayList<>(), new ArrayList<>(), 0, 0, 0);

        long startTime = System.nanoTime();
        List<Vertex> exploredNodes = new ArrayList<>();

        Map<Vertex, Double> gScore = new HashMap<>();
        Map<Vertex, Double> fScore = new HashMap<>();
        Map<Vertex, Vertex> predecessor = new HashMap<>();

        for (Vertex v : graph.getVertices()) {
            gScore.put(v, Double.POSITIVE_INFINITY);
            fScore.put(v, Double.POSITIVE_INFINITY);
        }

        gScore.put(start, 0.0);
        double startH = heuristic(start, target);
        fScore.put(start, startH);

        PriorityQueue<Node> openSet = new PriorityQueue<>(Comparator.comparingDouble(Node::fScore));
        openSet.add(new Node(start, startH));

        Set<Vertex> closedSet = new HashSet<>();

        while (!openSet.isEmpty()) {
            Node node = openSet.poll();
            Vertex current = node.vertex();

            if (closedSet.contains(current)) continue;
            closedSet.add(current);
            exploredNodes.add(current);

            if (current.equals(target)) {
                List<Vertex> path = reconstructPath(predecessor, current);
                long endTime = System.nanoTime();
                return new PathfindingResult(path, exploredNodes, exploredNodes.size(), (endTime - startTime) / 1_000_000.0, gScore.get(target));
            }

            for (Edge edge : current.getEdges()) {
                Vertex neighbor = edge.getTarget();
                double tentativeGScore = gScore.get(current) + edge.getDistance();

                if (tentativeGScore < gScore.getOrDefault(neighbor, Double.POSITIVE_INFINITY)) {
                    predecessor.put(neighbor, current);
                    gScore.put(neighbor, tentativeGScore);
                    double h = heuristic(neighbor, target);
                    fScore.put(neighbor, tentativeGScore + h);
                    openSet.add(new Node(neighbor, tentativeGScore + h));
                }
            }
        }

        long endTime = System.nanoTime();
        return new PathfindingResult(new ArrayList<>(), exploredNodes, exploredNodes.size(), (endTime - startTime) / 1_000_000.0, 0);
    }

    private static double heuristic(Vertex a, Vertex b) {
        // Euclidean distance with the same multiplier as in Graph.calculateDistance
        double dx = a.getX() - b.getX();
        double dy = a.getY() - b.getY();
        return Math.sqrt(dx * dx + dy * dy) * 0.1073;
    }

    private static List<Vertex> reconstructPath(Map<Vertex, Vertex> predecessor, Vertex current) {
        List<Vertex> path = new ArrayList<>();
        path.add(current);
        while (predecessor.containsKey(current)) {
            current = predecessor.get(current);
            path.add(0, current);
        }
        return path;
    }
}
