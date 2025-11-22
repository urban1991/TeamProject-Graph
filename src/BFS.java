import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
// Jeśli są w innym pakiecie, importy dla Graph i Vertex

public class BFS {

    public void runBFS(Graph graph, Vertex startVertex) {

        // Inicjalizacja: Upewnij się, że stan wszystkich wierzchołków jest czysty
        graph.resetAllVertices(); // Używamy nowej metody z klasy Graph

        // Ustawienia wierzchołka startowego
        startVertex.setDistance(0);
        startVertex.setVisited(true);

        // Kolejka jest sercem BFS
        Queue<Vertex> queue = new LinkedList<>();
        queue.offer(startVertex);

        while (!queue.isEmpty()) {
            Vertex u = queue.poll();

            // Pobieramy sąsiadów za pomocą nowej metody z klasy Graph
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