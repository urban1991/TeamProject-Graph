package org.graph.graphproject;

import java.util.List;

public class PathfindingResult {
    private final List<Vertex> path;
    private final List<Vertex> exploredNodes;
    private final int nodesVisited;
    private final double timeMs;
    private final double totalCost;
    private String algorithmName;

    public PathfindingResult(List<Vertex> path, List<Vertex> exploredNodes, int nodesVisited, double timeMs, double totalCost) {
        this.path = path;
        this.exploredNodes = exploredNodes;
        this.nodesVisited = nodesVisited;
        this.timeMs = timeMs;
        this.totalCost = totalCost;
    }

    public PathfindingResult(List<Vertex> path, List<Vertex> exploredNodes, int nodesVisited, double timeMs, double totalCost, String algorithmName) {
        this.path = path;
        this.exploredNodes = exploredNodes;
        this.nodesVisited = nodesVisited;
        this.timeMs = timeMs;
        this.totalCost = totalCost;
        this.algorithmName = algorithmName;
    }

    public List<Vertex> getPath() {
        return path;
    }

    public List<Vertex> getExploredNodes() {
        return exploredNodes;
    }

    public int getNodesVisited() {
        return nodesVisited;
    }

    public double getTimeMs() {
        return timeMs;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public String getAlgorithmName() {
        return algorithmName;
    }

    public void setAlgorithmName(String algorithmName) {
        this.algorithmName = algorithmName;
    }
}
