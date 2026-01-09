package org.graph.graphproject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class GraphLoader {

    public static Graph loadGraphData(String fileName, Charset charset, double radius) {
        File file = new File(fileName);
        if (!file.exists()) return null;

        List<Vertex> vertices = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file, charset))) {
            String header = br.readLine(); // skip header
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                // Handle different delimiters and trailing semicolons
                line = line.replaceAll(";+\\s*$", "");
                String[] values = line.split("[,;]");

                if (values.length < 3) continue;

                String name = values[0].trim();
                try {
                    double lat = Double.parseDouble(values[1].trim());
                    double lng = Double.parseDouble(values[2].trim());

                    Vertex w = new Vertex(name, (int) (lat * 1000), (int) (lng * 1000));
                    vertices.add(w);
                } catch (NumberFormatException e) {
                    // skip invalid lines
                }
            }

            Graph g = new Graph(vertices);
            g.addEdges(radius);
            return g;

        } catch (IOException e) {
            return null;
        }
    }
}
