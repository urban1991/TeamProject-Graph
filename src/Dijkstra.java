import java.util.*;

public class Dijkstra {

    public static List<Vertex> findShortestPath(Vertex start, Vertex target, Graph graph) {

        Map<Vertex, Double> distance = new HashMap<>();
        Map<Vertex, Vertex> predecessor = new HashMap<>();

        for (Vertex v : graph.getVertices()) {
            distance.put(v, Double.POSITIVE_INFINITY);
            predecessor.put(v, null);
        }

        distance.put(start, 0.0);

        PriorityQueue<Vertex> queue = new PriorityQueue<>(Comparator.comparingDouble(distance::get));
        queue.add(start);

        while (!queue.isEmpty()) {
            Vertex v = queue.poll();

            if (v == target) break;

            for (Edge edge : v.getEdges()) {
                Vertex u = edge.getTarget();
                double d = edge.getDistance();

                double newDistance = distance.get(v) + d;

                if (newDistance < distance.get(u)) {
                    distance.put(u, newDistance);
                    predecessor.put(u, v);
                    queue.add(u);
                }
            }
        }

        List<Vertex> path = new ArrayList<>();
        for (Vertex at = target; at != null; at = predecessor.get(at)) {
            path.add(at);
        }
        Collections.reverse(path);

        if (!path.isEmpty() && path.get(0) != start) path.clear();

        return path;
    }

    public static void PrintPath(List<Vertex> path){
        System.out.print("Shortest path from " + path.get(0).getName() + " to " + path.get(path.size() - 1).getName() + ":");
        System.out.println();
        for (Vertex v : path) {
            System.out.print(v.getName() + " -> ");
        }
        System.out.println("END");
    }
}