import java.util.ArrayList;
import java.util.List;

public class Vertex {

    private static int countID = 0; // auto ID counter


    private int nr;                    // unique ID
    private String name;
    private int x;
    private int y;
    private List<Edge> edges;

    // --- Konstruktor ---
    public Vertex(String name, int x, int y) {
        this.nr = countID++;         // sets unique ID from counter
        this.name = name;
        this.x = x;
        this.y = y;
        this.edges = new ArrayList<>();
    }

    // --- Gettery i settery ---
    public int getNumer() {
        return nr;
    }

    public void setNumer(int numer) {
        this.nr = numer;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public List<Edge> getEdges() {
        return edges;
    }

    public void setEdges(List<Edge> edges) {
        this.edges = edges;
    }

    // --- Dodaj krawędź ---
    public void addEdge(Vertex cel, double distance) {

        this.edges.add(new Edge(cel, distance));
    }

    public void print(){
        System.out.println("Vertex nr: " + nr + " x: " + x + " y: " + y + "name: " + name);
    }

    public void printEdges(){
        for (Edge edge : edges) {
            System.out.println(name +"->"+ edge.getTarget().getName()+" Distance: "+ edge.getDistance());

        }
    }

}