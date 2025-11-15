import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        // Allow passing CSV path as the first CLI argument; default to repo root CSV
        String csvFile = (args != null && args.length > 0) ? args[0] : "polandcities.csv";

        List<Vertex> vertices = new ArrayList<>();
        Graph graph = new Graph(vertices);

        // Resolve and validate the CSV path before attempting to read
        Path csvPath = Paths.get(csvFile);
        if (!Files.exists(csvPath)) {
            System.err.println("CSV file not found: " + csvPath.toAbsolutePath());
            System.err.println("Hint: run as 'java -cp out Main polandcities.csv' or provide an absolute path.");
            return; // Do not continue without data
        }

        try (BufferedReader br = new BufferedReader(new FileReader(csvPath.toFile(), Charset.forName("Windows-1250")))) {
            System.out.println("File opened: " + csvPath.toAbsolutePath());

            // Skip header
            br.readLine();

            String line;
            while ((line = br.readLine()) != null) {
                // Debug log can be noisy; keep minimal
                // System.out.println("Read line: " + line);

                // Trim trailing semicolons
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
            System.err.println("Failed to open/read file: " + csvPath.toAbsolutePath());
            e.printStackTrace();
            return; // Stop on I/O errors
        }

        if (vertices.isEmpty()) {
            System.err.println("No vertices loaded from CSV. Exiting.");
            return;
        }

        graph.addEdges(); // connects nearest neighbour (up to 10km)

        // Test/diagnostics output (safe access)
        graph.print(); // prints vertices
        if (vertices.size() > 3676) {
            vertices.get(3676).printEdges(); // original specific vertex
        } else {
            vertices.get(vertices.size() - 1).printEdges(); // fallback to last available vertex
        }
    }
}






