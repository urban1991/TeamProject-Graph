package org.graph.graphproject;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
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
            outputArea.appendText("--- VERTEX LIST ---\n");

            List<Vertex> vertices = graph.getVertices();

            for (Vertex v : vertices) {
                String vertexInfo = String.format("Nr: %d | City: %s (X: %d, Y: %d)\n",
                        v.getNumber(), v.getName(), v.getX(), v.getY());
                outputArea.appendText(vertexInfo);
            }


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
        }
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
