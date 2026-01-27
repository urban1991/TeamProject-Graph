package org.graph.graphproject;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a vertex (node) in the graph, corresponding to a city.
 * Contains geographic coordinates and a list of adjacent edges.
 */
public class Vertex {
    private static int countID = 0; // auto ID counter

    public static void resetCounter() {
        countID = 0;
    }

    private int nr;                    // unique ID
    private String name;
    private int x;
    private int y;
    private List<Edge> edges;

    // --- Constructor ---
    public Vertex(String name, int x, int y) {
        this.nr = countID++;         // sets unique ID from counter
        this.name = name;
        this.x = x;
        this.y = y;
        this.edges = new ArrayList<>();
    }

    // --- Getters & Setters ---
    public int getNumber() {
        return nr;
    }

    public void setNumber(int number) {
        this.nr = number;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public List<Edge> getEdges() {
        return edges;
    }

    public void setEdges(List<Edge> edges) {
        this.edges = edges;
    }

    // --- Add edge ---
    public void addEdge(Vertex target, double distance) {

        this.edges.add(new Edge(target, distance));
    }

    public void print(){
        System.out.println("Vertex nr: " + nr + " x: " + x + " y: " + y + "name: " + name);
    }

    public void printEdges(){
        for (Edge edge : edges) {
            System.out.println(name +"->"+ edge.getTarget().getName()+" Distance: "+ edge.getDistance());
        }
    }

    public void printEdgesToGui(javafx.scene.control.TextArea outputArea){
        outputArea.appendText("Edges from " + name + ":\n");
        if (edges.isEmpty()) {
            outputArea.appendText("  (No edge lower than 10 km)\n");
            return;
        }
        for (Edge edge : edges) {
            outputArea.appendText(String.format("  -> %s | Distance: %.2f km\n",
                    edge.getTarget().getName(),
                    edge.getDistance()));
        }
    }

}
