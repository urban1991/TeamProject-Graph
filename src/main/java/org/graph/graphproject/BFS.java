package org.graph.graphproject;

import java.util.*;

public class BFS {

    /**
     * Finds the shortest path between two vertices using Breadth-First Search (BFS).
     * In unweighted graphs, this finds the path with the minimum number of hops.
     * 
     * @param start The starting vertex.
     * @param target The destination vertex.
     * @param graph The graph containing the vertices.
     * @return A PathfindingResult containing the path, explored nodes, time taken, and total cost (sum of edge weights).
     */
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
/*
Analysis of the BFS algorithm in BFS.java confirmed that it is correctly implemented in accordance with graph theory.
It traverses the graph breadth-first, identifying the path with the minimum number of edges (hops) rather than the minimum sum of weights (distance).
This is the expected behavior for BFS on weighted graphs. Consequently, the resulting path may appear less optimal (arc-shaped) compared to A* or Dijkstra, as BFS ignores edge lengths.
 */