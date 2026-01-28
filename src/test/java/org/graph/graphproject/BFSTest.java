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

    /**
     * Tests if BFS finds the path with the minimum number of edges (hops),
     * ignoring the weights of those edges.
     */
    @Test
    public void testBFSShortestHops() {
        // A -> B -> C (2 hops, high weight: 100+100=200)
        vA.addEdge(vB, 100);
        vB.addEdge(vC, 100);

        // A -> D -> E -> C (3 hops, low weight: 1+1+1=3)
        vA.addEdge(vD, 1);
        vD.addEdge(vE, 1);
        vE.addEdge(vC, 1);

        PathfindingResult result = BFS.findShortestPath(vA, vC, graph);
        List<Vertex> path = result.getPath();

        // BFS should prefer the path with fewer edges (2 hops), regardless of weight
        assertEquals(3, path.size()); // A, B, C
        assertEquals(vA, path.get(0));
        assertEquals(vB, path.get(1));
        assertEquals(vC, path.get(2));
        
        // Cost should be calculated correctly even if not optimized
        assertEquals(200.0, result.getTotalCost(), 0.001);
    }

    /**
     * Tests BFS performance on a simple linear path.
     */
    @Test
    public void testBFSLinearPath() {
        // A -> B -> E
        vA.addEdge(vB, 10);
        vB.addEdge(vE, 10);

        PathfindingResult result = BFS.findShortestPath(vA, vE, graph);
        List<Vertex> path = result.getPath();

        assertEquals(3, path.size());
        assertEquals(vA, path.get(0));
        assertEquals(vB, path.get(1));
        assertEquals(vE, path.get(2));
        assertEquals(20.0, result.getTotalCost(), 0.001);
    }

    /**
     * Tests BFS behavior in a graph with cycles and multiple paths with the same number of hops.
     */
    @Test
    public void testBFSCycle() {
        // A -> B -> E
        // A -> D -> E
        // B -> D
        vA.addEdge(vB, 10);
        vB.addEdge(vE, 10);
        vA.addEdge(vD, 5);
        vD.addEdge(vE, 5);
        vB.addEdge(vD, 1);

        PathfindingResult result = BFS.findShortestPath(vA, vE, graph);
        List<Vertex> path = result.getPath();

        // Both A->B->E and A->D->E have 2 hops. 
        // BFS order depends on edge addition order in this implementation.
        assertEquals(3, path.size());
        assertEquals(vA, path.get(0));
        assertTrue(path.get(1) == vB || path.get(1) == vD);
        assertEquals(vE, path.get(2));
    }

    /**
     * Tests BFS behavior when there is no connection between vertices.
     */
    @Test
    public void testBFSDisconnected() {
        // A -> B
        // C -> D
        vA.addEdge(vB, 10);
        vC.addEdge(vD, 10);

        PathfindingResult result = BFS.findShortestPath(vA, vC, graph);
        assertTrue(result.getPath().isEmpty());
        assertEquals(0, result.getTotalCost());
    }

    /**
     * Tests the case where the starting vertex is the same as the target vertex.
     */
    @Test
    public void testBFSSameNode() {
        PathfindingResult result = BFS.findShortestPath(vA, vA, graph);
        assertEquals(1, result.getPath().size());
        assertEquals(vA, result.getPath().get(0));
        assertEquals(0, result.getTotalCost());
    }
}

