import java.util.ArrayList;
import java.util.List;

public class Vertex {

    private static int licznikID = 0; // automatyczny licznik ID

    private int nr;                    // numer (unikalny ID)
    private String name;
    private int x;
    private int y;
    private List<Edge> krawedzie;

    // --- Konstruktor ---
    public Vertex(String nazwa, int x, int y) {
        this.nr = licznikID++;         // nadaj unikalny numer
        this.name = nazwa;
        this.x = x;
        this.y = y;
        this.krawedzie = new ArrayList<>();
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

    public List<Edge> getKrawedzie() {
        return krawedzie;
    }

    public void setKrawedzie(List<Edge> krawedzie) {
        this.krawedzie = krawedzie;
    }

    // --- Dodaj krawędź ---
    public void dodajKrawedz(Vertex cel, double waga) {

        this.krawedzie.add(new Edge(cel, waga));
    }

    public void wypisz(){
        System.out.println("Wierzcholek nr: " + nr + " x: " + x + " y: " + y + "nazwa: " + name);
    }

    public void wypiszKrawedzie(){
        for (Edge edge : krawedzie) {
            System.out.println(name +"->"+ edge.getCel().getName()+" Odległosc: "+ edge.getWaga());

        }
    }

}