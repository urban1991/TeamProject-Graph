package org.graph.graphproject;

import java.util.*;

public class BFS {

    public static PathfindingResult findShortestPath(Vertex start, Vertex target, Graph graph) {
        long startTime = System.nanoTime();
        List<Vertex> exploredNodes = new ArrayList<>();
        Map<Vertex, Vertex> predecessor = new HashMap<>();
        Map<Vertex, Integer> distance = new HashMap<>();
        Set<Vertex> visited = new HashSet<>();

        Queue<Vertex> queue = new LinkedList<>();

        queue.add(start);
        visited.add(start);
        distance.put(start, 0);

        while (!queue.isEmpty()) {
            Vertex u = queue.poll();
            exploredNodes.add(u);

            if (u.equals(target)) break;

            for (Edge edge : u.getEdges()) {
                Vertex v = edge.getTarget();
                if (!visited.contains(v)) {
                    visited.add(v);
                    predecessor.put(v, u);
                    distance.put(v, distance.get(u) + 1);
                    queue.add(v);
                }
            }
        }

        List<Vertex> path = new ArrayList<>();
        Vertex at = target;
        while (at != null) {
            path.add(at);
            at = predecessor.get(at);
        }
        Collections.reverse(path);

        if (!path.isEmpty() && path.get(0) != start) path.clear();

        long endTime = System.nanoTime();
        double durationMs = (endTime - startTime) / 1_000_000.0;
        
        // Cost calculation for BFS (sum of edge weights)
        double totalCost = 0;
        for (int i = 0; i < path.size() - 1; i++) {
            Vertex v1 = path.get(i);
            Vertex v2 = path.get(i + 1);
            for (Edge e : v1.getEdges()) {
                if (e.getTarget().equals(v2)) {
                    totalCost += e.getDistance();
                    break;
                }
            }
        }

        return new PathfindingResult(path, exploredNodes, exploredNodes.size(), durationMs, totalCost);
    }
}
