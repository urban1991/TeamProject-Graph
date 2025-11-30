package org.graph.graphproject;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GraphController {

    @FXML
    private TextArea outputArea;

    @FXML
    protected void onGenerateButtonClick() {
        outputArea.setText("Starting graph generation...\n");

        Graph graph = loadGraphData();

        if (graph != null) {
            outputArea.appendText("Graph successfully loaded!\n\n");

            List<Vertex> vertices = graph.getVertices();

            // --- 1. EXISTING VERTEX PRINTING LOGIC ---
            outputArea.appendText("--- VERTEX LIST ---\n");
            for (Vertex v : vertices) {
                String vertexInfo = String.format("Nr: %d | City: %s (X: %d, Y: %d)\n",
                        v.getNumber(), v.getName(), v.getX(), v.getY());
                outputArea.appendText(vertexInfo);
            }

            // --- 2. EXISTING NEIGHBOR CHECK ---
            int firstTestIndex = 10;
            int lastTestIndex = vertices.size() - 1;

            outputArea.appendText("\n--- TEST FOR SELECTED NEIGHBOURS ---\n");

            if (vertices.size() > firstTestIndex) {
                Vertex testV = vertices.get(firstTestIndex);
                outputArea.appendText(String.format("Checking: %s (Index %d)\n",
                        testV.getName(), firstTestIndex));
                testV.printEdgesToGui(outputArea);
            }

            if (vertices.size() > lastTestIndex && lastTestIndex >= 0) {
                Vertex testV = vertices.get(lastTestIndex);
                outputArea.appendText(String.format("\nChecking: %s (Index %d - LAST)\n",
                        testV.getName(), lastTestIndex));
                testV.printEdgesToGui(outputArea);
            }

            // --- 3. YOUR NEW BFS TESTING LOGIC ---
            runBFSTest(graph, vertices);
            List<Vertex> path = Dijkstra.findShortestPath(vertices.get(3676), vertices.get(5), graph);

            Dijkstra.printPath(path);
        }
    }

    private void runBFSTest(Graph graph, List<Vertex> vertices) {
        outputArea.appendText("\n==================================\n");
        outputArea.appendText("       BFS ALGORITHM TEST       \n");
        outputArea.appendText("==================================\n");

        System.out.println("Graf zbudowany. Liczba wierzchołków: " + vertices.size());

        if (vertices.isEmpty()) {
            outputArea.appendText("Brak wierzchołków w grafie.\n");
            return;
        }

        Vertex startNode = vertices.get(0);
        Vertex targetNode = vertices.size() > 1000 ? vertices.get(1000) : vertices.get(vertices.size() - 1);

        outputArea.appendText("--- START: Uruchamianie BFS ---\n");
        outputArea.appendText("Wierzchołek startowy: " + startNode.getName() + "\n");
        outputArea.appendText("Wierzchołek docelowy: " + targetNode.getName() + "\n");

        BFS bfsAlgorithm = new BFS();
        bfsAlgorithm.runBFS(graph, startNode);

        outputArea.appendText("--- KONIEC: BFS zakończony ---\n");
        outputArea.appendText("\n--- Wyniki BFS ---\n");

        // Sprawdzenie odległości
        int distance = targetNode.getDistance();

        if (distance == Integer.MAX_VALUE) {
            outputArea.appendText("Wierzchołek " + targetNode.getName() +
                    " jest nieosiągalny z " + startNode.getName() + ".\n");
        } else {
            outputArea.appendText("Najkrótsza odległość krawędziowa (liczba kroków) z " + startNode.getName() +
                    " do " + targetNode.getName() + " wynosi: " + distance + "\n");

            outputArea.appendText("Ścieżka: ");
            String pathString = getPathString(targetNode);
            outputArea.appendText(pathString + "\n");
        }
    }

    // Helper method to generate the path string (modified from your printPath void method)
    private String getPathString(Vertex target) {
        List<String> path = new ArrayList<>();
        Vertex current = target;

        while (current != null) {
            path.add(current.getName());
            current = current.getParent();
        }

        Collections.reverse(path);

        return String.join(" -> ", path);
    }

    private Graph loadGraphData() {
        /**
         * I commented out polandcities.csv because it was too big to be loaded in memory.
         * for testing purposes I used test_cities.csv instead.
         * If someone wants to use the original file, uncomment the line below, and comment out the one above.
         * */
        //String csvFile = "polandcities.csv";
        String csvFile = "test_cities.csv";
        List<Vertex> vertices = new ArrayList<>();
        Graph graph = new Graph(vertices);

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile, StandardCharsets.UTF_8))) {
            br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                line = line.replaceAll(";+\\s*$", "");
                String[] values = line.split(",");
                if (values.length < 3) continue;

                String name = values[0].trim();
                try {
                    double lat = Double.parseDouble(values[1].trim());
                    double lng = Double.parseDouble(values[2].trim());

                    Vertex w = new Vertex(name, (int) (lat * 1000), (int) (lng * 1000));
                    vertices.add(w);
                } catch (NumberFormatException e) {
                    outputArea.appendText("Data error in line: " + line + "\n");
                }
            }

            graph.addEdges();
            return graph;

        } catch (IOException e) {
            outputArea.appendText("Critical error, no file found" + csvFile + "\n");
            e.printStackTrace();
            return null;
        }
    }
}
