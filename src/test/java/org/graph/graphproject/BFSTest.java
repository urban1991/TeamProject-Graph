package org.graph.graphproject;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class BFSTest {
    private Graph graph;
    private Vertex vA, vB, vC, vD, vE;

    @BeforeEach
    public void setup() {
        vA = new Vertex("A", 0, 0);
        vB = new Vertex("B", 10, 0);
        vC = new Vertex("C", 20, 0);
        vD = new Vertex("D", 0, 10);
        vE = new Vertex("E", 10, 10);

        List<Vertex> vertices = List.of(vA, vB, vC, vD, vE);
        graph = new Graph(vertices);
    }

    @Test
    public void testBFSShortestHops() {
        // A -> B -> C (2 hops, high weight)
        vA.addEdge(vB, 100);
        vB.addEdge(vC, 100);

        // A -> D -> E -> C (3 hops, low weight)
        vA.addEdge(vD, 1);
        vD.addEdge(vE, 1);
        vE.addEdge(vC, 1);

        PathfindingResult result = BFS.findShortestPath(vA, vC, graph);
        List<Vertex> path = result.getPath();

        // BFS should prefer the path with fewer edges, regardless of weight
        assertEquals(3, path.size()); // A, B, C
        assertEquals(vA, path.get(0));
        assertEquals(vB, path.get(1));
        assertEquals(vC, path.get(2));
        
        // Cost should be calculated correctly even if not optimized
        assertEquals(200.0, result.getTotalCost(), 0.001);
    }

    @Test
    public void testBFSNoPath() {
        // vA connected to vB, but vC isolated
        vA.addEdge(vB, 10);
        
        PathfindingResult result = BFS.findShortestPath(vA, vC, graph);
        assertTrue(result.getPath().isEmpty());
    }
}
