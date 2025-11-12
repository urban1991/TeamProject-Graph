import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        String csvFile = "C:\\Users\\jakub\\IdeaProjects\\TeamProject-Graph\\polandcities.csv"; // pełna ścieżka
        List<Vertex> wierzcholki = new ArrayList<>();
        Graph graph = new Graph(wierzcholki);

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile, Charset.forName("Windows-1250")))) {
            System.out.println("Plik otwarty!");

            // pomiń nagłówek
            br.readLine();

            String line;
            while ((line = br.readLine()) != null) {
                System.out.println("Wczytany wiersz: " + line);

                line = line.replaceAll(";+\\s*$", "");

                String[] values = line.split(",");
                if (values.length < 3) continue;

                String nazwa = values[0].trim();
                double lat = Double.parseDouble(values[1].trim());
                double lng = Double.parseDouble(values[2].trim());

                Vertex w = new Vertex(nazwa, (int) (lat * 1000), (int) (lng * 1000));
                wierzcholki.add(w);
            }

        } catch (IOException e) {
            System.err.println("Błąd przy otwieraniu pliku: " + e.getMessage());
            e.printStackTrace();
        }

        graph.wypisz();

        graph.addEdges();

        wierzcholki.get(3676).wypiszKrawedzie();


    }
}






