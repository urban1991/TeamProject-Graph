package org.graph.graphproject;

import java.util.*;

public class Dijkstra {

    public static double Infinity = Double.POSITIVE_INFINITY;

    private static record Node(Vertex vertex, double distance) {}

    public static PathfindingResult findShortestPath(Vertex start, Vertex target, Graph graph) {
        long startTime = System.nanoTime();
        List<Vertex> exploredNodes = new ArrayList<>();

        Map<Vertex, Double> distance = new HashMap<>();
        Map<Vertex, Vertex> predecessor = new HashMap<>();

        for (Vertex v : graph.getVertices()) {
            distance.put(v, Infinity);
        }

        distance.put(start, 0.0);

        PriorityQueue<Node> queue = new PriorityQueue<>(Comparator.comparingDouble(Node::distance));
        queue.add(new Node(start, 0.0));

        Set<Vertex> settled = new HashSet<>();

        while (!queue.isEmpty()) {
            Node node = queue.poll();
            Vertex v = node.vertex();

            if (settled.contains(v)) continue;
            settled.add(v);
            exploredNodes.add(v);

            if (v == target) break;

            for (Edge edge : v.getEdges()) {
                Vertex u = edge.getTarget();
                double d = edge.getDistance();

                double newDistance = distance.get(v) + d;

                if (newDistance < distance.get(u)) {
                    distance.put(u, newDistance);
                    predecessor.put(u, v);
                    queue.add(new Node(u, newDistance));
                }
            }
        }

        List<Vertex> path = new ArrayList<>();
        for (Vertex at = target; at != null; at = predecessor.get(at)) {
            path.add(at);
        }
        Collections.reverse(path);

        if (!path.isEmpty() && path.get(0) != start) path.clear();

        long endTime = System.nanoTime();
        double durationMs = (endTime - startTime) / 1_000_000.0;
        double totalCost = path.isEmpty() ? 0 : distance.get(target);

        return new PathfindingResult(path, exploredNodes, exploredNodes.size(), durationMs, totalCost);
    }

    public static void printPath(List<Vertex> path) {
        System.out.print("Shortest path from " + path.get(0).getName() + " to " + path.get(path.size() - 1).getName() + ":");
        System.out.println();
        for (Vertex v : path) {
            System.out.print(v.getName() + " -> ");
        }
        System.out.println("END");
    }
}
