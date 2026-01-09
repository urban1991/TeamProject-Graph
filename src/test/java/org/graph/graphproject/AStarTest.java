package org.graph.graphproject;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class AStarTest {
    private Graph graph;
    private Vertex vStart, vTarget, vDistraction;

    @BeforeEach
    public void setup() {
        // vStart(0,0), vTarget(100,0)
        vStart = new Vertex("Start", 0, 0);
        vTarget = new Vertex("Target", 100000, 0); // 100km away
        
        // vDistraction is far away from target but close to start
        vDistraction = new Vertex("Distraction", 0, 50000); // 50km north
        
        List<Vertex> vertices = List.of(vStart, vTarget, vDistraction);
        graph = new Graph(vertices);
    }

    @Test
    public void testAStarPath() {
        vStart.addEdge(vTarget, 100);
        vStart.addEdge(vDistraction, 10);
        
        PathfindingResult result = AStar.findShortestPath(vStart, vTarget, graph);
        assertFalse(result.getPath().isEmpty());
        assertEquals(vStart, result.getPath().get(0));
        assertEquals(vTarget, result.getPath().get(1));
        assertEquals(100.0, result.getTotalCost(), 0.001);
    }

    @Test
    public void testAStarEfficiency() {
        // Create a grid-like graph
        // Start at (0,0), Goal at (10,10)
        // Dijkstra will explore in all directions.
        // A* should prioritize (right, up) directions.
        
        List<Vertex> grid = new java.util.ArrayList<>();
        Vertex start = null;
        Vertex goal = null;
        int size = 15;
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                Vertex v = new Vertex("V_"+x+"_"+y, x*1000, y*1000);
                grid.add(v);
                if (x == 0 && y == 0) start = v;
                if (x == size-1 && y == size-1) goal = v;
            }
        }
        
        // Connect neighbors
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                Vertex current = grid.get(x * size + y);
                if (x + 1 < size) current.addEdge(grid.get((x + 1) * size + y), 1.0);
                if (y + 1 < size) current.addEdge(grid.get(x * size + (y + 1)), 1.0);
                if (x - 1 >= 0) current.addEdge(grid.get((x - 1) * size + y), 1.0);
                if (y - 1 >= 0) current.addEdge(grid.get(x * size + (y - 1)), 1.0);
            }
        }
        
        Graph gridGraph = new Graph(grid);
        PathfindingResult dijkstraRes = Dijkstra.findShortestPath(start, goal, gridGraph);
        PathfindingResult astarRes = AStar.findShortestPath(start, goal, gridGraph);
        
        assertEquals(dijkstraRes.getTotalCost(), astarRes.getTotalCost(), 0.001);
        // A* should visit fewer nodes than Dijkstra on a grid
        assertTrue(astarRes.getNodesVisited() <= dijkstraRes.getNodesVisited(), 
            "A* ("+astarRes.getNodesVisited()+") should be more efficient than Dijkstra ("+dijkstraRes.getNodesVisited()+")");
    }
}
