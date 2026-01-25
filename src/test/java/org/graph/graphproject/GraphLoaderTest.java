package org.graph.graphproject;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class GraphLoaderTest {

    @TempDir
    Path tempDir;

    @Test
    public void testLoadGraphData() throws IOException {
        Path csvPath = tempDir.resolve("test_cities.csv");
        String content = "City,Lat,Lng\n" +
                        "Warszawa,52.229,21.012\n" +
                        "Kraków,50.064,19.945\n" +
                        "Invalid,abc,def\n" +
                        "Wrocław;51.107;17.038;\n"; // Test semicolon and trailing semicolon
        
        Files.writeString(csvPath, content, StandardCharsets.UTF_8);

        Graph graph = GraphLoader.loadGraphData(csvPath.toString(), StandardCharsets.UTF_8, 500.0);
        
        assertNotNull(graph);
        List<Vertex> vertices = graph.getVertices();
        assertEquals(3, vertices.size());
        
        assertEquals("Warszawa", vertices.get(0).getName());
        assertEquals(52229, vertices.get(0).getX());
        
        assertEquals("Kraków", vertices.get(1).getName());
        
        assertEquals("Wrocław", vertices.get(2).getName());
        assertEquals(51107, vertices.get(2).getX());
        
        // Connections should be established because radius is large (500km)
        assertFalse(vertices.get(0).getEdges().isEmpty());
    }

    @Test
    public void testLoadNonExistentFile() {
        Graph graph = GraphLoader.loadGraphData("non_existent.csv", StandardCharsets.UTF_8, 30.0);
        assertNull(graph);
    }
}
