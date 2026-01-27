package org.graph.graphproject;

import java.util.ArrayList;
import java.util.List;

public class Graph {
    private List<Vertex> vertices = new ArrayList<>();
    public Graph(List<Vertex> vertices) {
        this.vertices = vertices;
    }

    public List<Vertex> getVertices() {
        return vertices;
    }

    public void setVertices(List<Vertex> vertices) {
        this.vertices = vertices;
    }

    public List<Vertex> getAllVertices() {
        return this.vertices;
    }


    public void addVertex(Vertex vertex) {
        this.vertices.add(vertex);
    }

    public void print(){
        System.out.println("\nList of cities from csv(vertices:");
        for (Vertex w : this.vertices) {
            System.out.println(w.getNumber()+". "+w.getName()+" "+w.getX()+" "+w.getY());
        }
    }

    public double calculateDistance(Vertex w1, Vertex w2){
        double dx = w2.getX() - w1.getX();
        double dy = w2.getY() - w1.getY();
        return Math.sqrt(dx * dx + dy * dy)*0.1073;
    }

    /**
     * Builds the graph edges by connecting vertices within a specified distance (threshold).
     * Uses a grid-based spatial index to efficiently find nearby vertices.
     * 
     * @param threshold The maximum distance (radius) to connect two vertices.
     */
    public void addEdges(double threshold) {
        if (vertices.isEmpty()) return;
        
        // Clear existing edges if any
        for (Vertex v : vertices) {
            v.getEdges().clear();
        }

        // 1 degree lat is ~111km. Our units are 1000 * degrees.
        // So threshold km is roughly threshold/0.111 = threshold * 9.0 units.
        // We adjust cellSize to be proportional to threshold to keep grid efficient
        int cellSize = (int)(threshold * 10); 
        if (cellSize < 10) cellSize = 10;
        
        java.util.Map<String, List<Vertex>> grid = new java.util.HashMap<>();
        for (Vertex v : vertices) {
            int gx = v.getX() / cellSize;
            int gy = v.getY() / cellSize;
            String key = gx + ":" + gy;
            grid.computeIfAbsent(key, k -> new ArrayList<>()).add(v);
        }
        
        for (Vertex v1 : vertices) {
            int gx = v1.getX() / cellSize;
            int gy = v1.getY() / cellSize;
            
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    String key = (gx + i) + ":" + (gy + j);
                    List<Vertex> cellVertices = grid.get(key);
                    if (cellVertices == null) continue;
                    
                    for (Vertex v2 : cellVertices) {
                        if (v1.getNumber() >= v2.getNumber()) continue; 
                        double d = calculateDistance(v1, v2);
                        if (d <= threshold) {
                            v1.addEdge(v2, d);
                            v2.addEdge(v1, d);
                        }
                    }
                }
            }
        }
    }

    public List<Vertex> getNeighbors(Vertex vertex) {
        List<Vertex> neighbors = new ArrayList<>();

        // Go through all edges starting from vertex
        for (Edge edge : vertex.getEdges()) {
            neighbors.add(edge.getTarget());
        }
        return neighbors;
    }
}



