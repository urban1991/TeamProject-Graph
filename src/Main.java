import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        String csvFile = "C:\\Users\\jakub\\IdeaProjects\\TeamProject-Graph\\polandcities.csv"; // full path
        List<Vertex> vertices = new ArrayList<>();
        Graph graph = new Graph(vertices);

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile, Charset.forName("Windows-1250")))) {
            System.out.println("File opened!");


            br.readLine();

            String line;
            while ((line = br.readLine()) != null) {
                System.out.println("Read line: " + line);

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
        }

        graph.addEdges();//connects nearest neighbour (up to 10km)


        //Test
        graph.print();//prints vertices
        vertices.get(3676).printEdges(); //prints edges from last vertex (Biała róża)

        List<Vertex> path = Dijkstra.findShortestPath(vertices.get(3676), vertices.get(5), graph);

        Dijkstra.PrintPath(path);


    }
}






