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
}
