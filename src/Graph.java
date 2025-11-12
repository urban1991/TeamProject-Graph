import java.util.ArrayList;
import java.util.List;

public class Graph {

        private List<Vertex> wierzcholki = new ArrayList<>();

    public Graph(List<Vertex> wierzcholki) {
        this.wierzcholki = wierzcholki;
    }

    public void wypisz(){
        System.out.println("\nLista wczytanych miast:");
        for (Vertex w : this.wierzcholki) {
            System.out.println(w.getNumer()+". "+w.getName()+" "+w.getX()+" "+w.getY());
        }

    }

    public double odl (Vertex w1, Vertex w2){

        double dx = w2.getX() - w1.getX();
        double dy = w2.getY() - w1.getY();
        return Math.sqrt(dx * dx + dy * dy)*0.1073;

    }

    public void addEdges() {
        int n = wierzcholki.size();
        for (int i = 0; i < n; i++) {
            Vertex w1 = wierzcholki.get(i);
            for (int j = i + 1; j < n; j++) {
                Vertex w2 = wierzcholki.get(j);
                double d = odl(w1, w2);
                if (d <= 10) {
                    w1.dodajKrawedz(w2, d);
                    w2.dodajKrawedz(w1, d);
                }
            }
        }
    }
}



