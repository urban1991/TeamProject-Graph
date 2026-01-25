package org.graph.graphproject;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class GraphTest {

    @Test
    public void testGraphBuilding() {
        List<Vertex> vertices = new ArrayList<>();
        // 100 units * 0.1073 approx 10.73 km
        vertices.add(new Vertex("A", 0, 0));
        vertices.add(new Vertex("B", 100, 0)); // ~10.73 km away
        vertices.add(new Vertex("C", 400, 0)); // ~42.92 km away from A

        Graph graph = new Graph(vertices);
        
        // Test with 30km threshold
        graph.addEdges(30.0);
        
        // A should be connected to B (10.73 < 30), but not to C (42.92 > 30)
        Vertex vA = vertices.get(0);
        assertEquals(1, vA.getEdges().size());
        assertEquals("B", vA.getEdges().get(0).getTarget().getName());

        // B should be connected to A
        // C is ~32.19 km from B (300 units), so B should NOT be connected to C with 30km threshold
        Vertex vB = vertices.get(1);
        assertEquals(1, vB.getEdges().size());

        // C should have no edges
        Vertex vC = vertices.get(2);
        assertTrue(vC.getEdges().isEmpty());
    }

    @Test
    public void testThresholdChange() {
        List<Vertex> vertices = new ArrayList<>();
        vertices.add(new Vertex("A", 0, 0));
        vertices.add(new Vertex("B", 500, 0)); // ~53.65 km away

        Graph graph = new Graph(vertices);
        
        // 30km threshold -> no connection
        graph.addEdges(30.0);
        assertTrue(vertices.get(0).getEdges().isEmpty());

        // 60km threshold -> connection
        graph.addEdges(60.0);
        assertFalse(vertices.get(0).getEdges().isEmpty());
        assertEquals(1, vertices.get(0).getEdges().size());
    }

    @Test
    public void testVertexNumbering() {
        Vertex.resetCounter();
        Vertex v1 = new Vertex("City", 0, 0);
        Vertex v2 = new Vertex("City", 1, 1);
        
        assertEquals(0, v1.getNumber());
        assertEquals(1, v2.getNumber());
        
        Vertex.resetCounter();
        Vertex v3 = new Vertex("City", 2, 2);
        assertEquals(0, v3.getNumber());
    }
}
