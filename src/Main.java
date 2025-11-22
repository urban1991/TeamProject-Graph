import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        String csvFile = "polandcities.csv"; // full path
        List<Vertex> vertices = new ArrayList<>();
        Graph graph = new Graph(vertices);

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile, Charset.forName("Windows-1250")))) {
            System.out.println("File opened!");


            br.readLine();

            String line;
            while ((line = br.readLine()) != null) {
                //System.out.println("Read line: " + line);

                line = line.replaceAll(";+\\s*$", "");

                String[] values = line.split(",");
                if (values.length < 3) continue;

                String name = values[0].trim();
                double lat = Double.parseDouble(values[1].trim());
                double lng = Double.parseDouble(values[2].trim());

                Vertex w = new Vertex(name, (int) (lat * 1000), (int) (lng * 1000));
                vertices.add(w);
            }

        } catch (IOException e) {
            System.err.println("Failed to open file: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        graph.addEdges();//connects nearest neighbour (up to 10km)

        /*
        Test spr ilość sąsiadów
        Vertex testStart = vertices.get(0);
        List<Vertex> neighbors = graph.getNeighbors(testStart);

        System.out.println("TEST: Wierzchołek startowy: " + testStart.getName());
        System.out.println("TEST: Liczba sąsiadów: " + neighbors.size());
        */



        //Test
        //graph.print();//prints vertices
        //vertices.get(3676).printEdges(); //prints edges from last vertex (Biała róża)
        System.out.println("Graf zbudowany. Liczba wierzchołków: " + vertices.size());


        // --- 2. Uruchomienie Algorytmu BFS ---

        if (vertices.isEmpty()) {
            System.out.println("Brak wierzchołków w grafie.");
            return;
        }

        // Wybieramy wierzchołek startowy (np. pierwszy załadowany)
        Vertex startNode = vertices.get(0);
        // Wybieramy cel do sprawdzenia (np. 1000 wierzchołek)
        Vertex targetNode = vertices.size() > 1000 ? vertices.get(1000) : vertices.get(vertices.size() - 1);

        System.out.println("\n--- START: Uruchamianie BFS ---");
        System.out.println("Wierzchołek startowy: " + startNode.getName());
        System.out.println("Wierzchołek docelowy: " + targetNode.getName());

        // Utworzenie i uruchomienie algorytmu BFS
        BFS bfsAlgorithm = new BFS();
        bfsAlgorithm.runBFS(graph, startNode);

        System.out.println("--- KONIEC: BFS zakończony ---");


        // --- 3. Wyświetlenie Wyników ---

        System.out.println("\n--- Wyniki BFS ---");

        // Sprawdzenie odległości
        int distance = targetNode.getDistance();

        if (distance == Integer.MAX_VALUE) {
            System.out.println("Wierzchołek " + targetNode.getName() +
                    " jest nieosiągalny z " + startNode.getName() + ".");
        } else {
            System.out.println("Najkrótsza odległość krawędziowa (liczba kroków) z " + startNode.getName() +
                    " do " + targetNode.getName() + " wynosi: " + distance);

            // Rekonstrukcja i wyświetlenie ścieżki
            System.out.print("Ścieżka: ");
            printPath(targetNode);
        }

         //Test
        //graph.print(); // Opcjonalnie: prints vertices
         //vertices.get(3676).printEdges(); // Opcjonalnie: prints edges from last vertex
    } // Koniec metody main

    // Metoda pomocnicza do rekonstrukcji i wyświetlania ścieżki

    public static void printPath(Vertex target) {
        List<String> path = new ArrayList<>();
        Vertex current = target;

        // Idziemy od celu do wierzchołka startowego, śledząc pole 'parent'
        while (current != null) {
            path.add(current.getName());
            current = current.getParent();
        }

        // Ścieżka jest odwrócona, więc odwracamy listę
        Collections.reverse(path);

        // Wypisujemy ścieżkę
        System.out.println(String.join(" -> ", path));
    }






}






