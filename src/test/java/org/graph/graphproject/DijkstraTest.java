package org.graph.graphproject;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class DijkstraTest {
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
     * Tests if Dijkstra's algorithm correctly finds the path with the minimum total edge weight.
     */
    @Test
    public void testDijkstraShortestWeight() {
        // A -> B -> C (2 hops, weight 200)
        vA.addEdge(vB, 100);
        vB.addEdge(vC, 100);

        // A -> D -> E -> C (3 hops, weight 30)
        vA.addEdge(vD, 10);
        vD.addEdge(vE, 10);
        vE.addEdge(vC, 10);

        PathfindingResult result = Dijkstra.findShortestPath(vA, vC, graph);
        List<Vertex> path = result.getPath();

        // Dijkstra should prefer the path with lower weight
        assertEquals(4, path.size()); // A, D, E, C
        assertEquals(vA, path.get(0));
        assertEquals(vD, path.get(1));
        assertEquals(vE, path.get(2));
        assertEquals(vC, path.get(3));
        assertEquals(30.0, result.getTotalCost(), 0.001);
    }

    /**
     * Tests the algorithm's behavior after a manual update of edge weights.
     */
    @Test
    public void testCustomWeightUpdate() {
        // Initial: A -> B (100)
        vA.addEdge(vB, 100);

        PathfindingResult res1 = Dijkstra.findShortestPath(vA, vB, graph);
        assertEquals(100.0, res1.getTotalCost(), 0.001);

        // Update weight
        vA.getEdges().get(0).setDistance(5.5);

        PathfindingResult res2 = Dijkstra.findShortestPath(vA, vB, graph);
        assertEquals(5.5, res2.getTotalCost(), 0.001);
    }

    /**
     * Tests the behavior of Dijkstra's algorithm when no path to the target exists.
     */
    @Test
    public void testNoPath() {
        // A -> B, but C is isolated
        vA.addEdge(vB, 10);
        
        PathfindingResult result = Dijkstra.findShortestPath(vA, vC, graph);
        
        assertTrue(result.getPath().isEmpty(), "Path should be empty when no path exists");
        assertEquals(0.0, result.getTotalCost(), 0.001);
    }

    /**
     * Tests the case where the starting vertex is the same as the target vertex.
     */
    @Test
    public void testStartIsTarget() {
        PathfindingResult result = Dijkstra.findShortestPath(vA, vA, graph);
        
        assertEquals(1, result.getPath().size());
        assertEquals(vA, result.getPath().get(0));
        assertEquals(0.0, result.getTotalCost(), 0.001);
    }

    /**
     * Tests the algorithm's behavior in a disconnected graph.
     */
    @Test
    public void testDisconnectedGraph() {
        // A -> B
        vA.addEdge(vB, 10);
        // D -> E
        vD.addEdge(vE, 5);
        
        // Path from A to E should not exist
        PathfindingResult result = Dijkstra.findShortestPath(vA, vE, graph);
        
        assertTrue(result.getPath().isEmpty(), "Path should be empty in disconnected graph");
        assertEquals(0.0, result.getTotalCost(), 0.001);
    }

    /**
     * Tests the algorithm's performance in a graph containing cycles.
     */
    @Test
    public void testPathWithCycles() {
        // A -> B (10) -> A (10) - cycle
        vA.addEdge(vB, 10);
        vB.addEdge(vA, 10);
        
        // A -> C (50)
        vA.addEdge(vC, 50);
        
        // B -> C (5)
        vB.addEdge(vC, 5);
        
        // Shortest path A -> B -> C should be 15
        PathfindingResult result = Dijkstra.findShortestPath(vA, vC, graph);
        
        assertEquals(3, result.getPath().size());
        assertEquals(vA, result.getPath().get(0));
        assertEquals(vB, result.getPath().get(1));
        assertEquals(vC, result.getPath().get(2));
        assertEquals(15.0, result.getTotalCost(), 0.001);
    }

    /**
     * Tests the algorithm's behavior in the presence of negative edge weights.
     * Note: Classic Dijkstra does not guarantee correctness for negative weights.
     */
    @Test
    public void testNegativeWeights() {
        // Dijkstra doesn't officially support negative weights, 
        // but we test how it behaves with the current implementation.
        // A -> B (5)
        // A -> C (10)
        // B -> C (-2)
        vA.addEdge(vB, 5);
        vA.addEdge(vC, 10);
        vB.addEdge(vC, -2);
        
        PathfindingResult result = Dijkstra.findShortestPath(vA, vC, graph);
        
        // In this case, it should find A -> B -> C (3.0)
        assertEquals(3, result.getPath().size());
        assertEquals(vA, result.getPath().get(0));
        assertEquals(vB, result.getPath().get(1));
        assertEquals(vC, result.getPath().get(2));
        assertEquals(3.0, result.getTotalCost(), 0.001);
    }
}

