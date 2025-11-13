import java.util.ArrayList;
import java.util.List;

public class Graph {

        private List<Vertex> vertices = new ArrayList<>();

    public Graph(List<Vertex> vertices) {
        this.vertices = vertices;
    }

    public void print(){
        System.out.println("\nList of cities from csv(vertices:");
        for (Vertex w : this.vertices) {
            System.out.println(w.getNumer()+". "+w.getName()+" "+w.getX()+" "+w.getY());
        }

    }

    public double calculateDistance(Vertex w1, Vertex w2){

        double dx = w2.getX() - w1.getX();
        double dy = w2.getY() - w1.getY();
        return Math.sqrt(dx * dx + dy * dy)*0.1073;

    }

    public void addEdges() {
        int n = vertices.size();
        for (int i = 0; i < n; i++) {
            Vertex w1 = vertices.get(i);
            for (int j = i + 1; j < n; j++) {
                Vertex w2 = vertices.get(j);
                double d = calculateDistance(w1, w2);
                if (d <= 10) {
                    w1.addEdge(w2, d);
                    w2.addEdge(w1, d);
                }
            }
        }
    }
}



