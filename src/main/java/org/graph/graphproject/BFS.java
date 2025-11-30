package org.graph.graphproject;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class BFS {

    public void runBFS(Graph graph, Vertex startVertex) {
        graph.resetAllVertices();

        startVertex.setDistance(0);
        startVertex.setVisited(true);

        Queue<Vertex> queue = new LinkedList<>();
        queue.offer(startVertex);

        while (!queue.isEmpty()) {
            Vertex u = queue.poll();

            List<Vertex> neighbors = graph.getNeighbors(u);

            for (Vertex v : neighbors) {
                if (!v.isVisited()) {
                    v.setVisited(true);
                    v.setDistance(u.getDistance() + 1);
                    v.setParent(u);
                    queue.offer(v);
                }
            }
        }
    }
}
