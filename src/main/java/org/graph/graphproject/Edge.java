package org.graph.graphproject;

/**
 * Represents a directed edge between two vertices in the graph.
 * Stores the target vertex and the distance (weight) of the edge.
 */
public class Edge {
    private Vertex target;
    private double distance;

    public Edge(Vertex target, double distance) {
        this.target = target;
        this.distance = distance;
    }

    public Vertex getTarget() {
        return target;
    }

    public void setTarget(Vertex target) {
        this.target = target;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public void print() {
        System.out.println("Target: " + target.getName() + " distance: " + distance);
    }
}
