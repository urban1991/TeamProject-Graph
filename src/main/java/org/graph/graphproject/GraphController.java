package org.graph.graphproject;

import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.web.WebView;
import javafx.scene.web.WebEngine;
import org.controlsfx.control.SearchableComboBox;
import org.controlsfx.control.Notifications;
import javafx.util.Duration;
import javafx.geometry.Pos;
import javafx.application.Platform;

import java.io.File;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javafx.stage.FileChooser;

public class GraphController {

    @FXML private Button loadBtn;
    @FXML private TextField neighborRadiusField;
    @FXML private Button rebuildBtn;
    @FXML private Button clearBtn;
    @FXML private SearchableComboBox<String> startCityCombo;
    @FXML private SearchableComboBox<String> targetCityCombo;
    @FXML private RadioButton bfsRadio;
    @FXML private RadioButton dijkstraRadio;
    @FXML private RadioButton astarRadio;
    @FXML private Button findPathBtn;
    @FXML private Label distanceLabel;
    @FXML private Label timeLabel;
    @FXML private Label nodesLabel;
    @FXML private WebView webView;
    @FXML private TextArea outputArea;
    @FXML private ToggleGroup algoGroup;

    private Graph graph;
    private WebEngine webEngine;
    private boolean isMapLoaded = false;
    private JavaConnector javaConnector;

    private List<PathfindingResult> currentResults = new ArrayList<>();
    private String lastSearchKey = "";

    /**
     * Escapes arbitrary text so it can be safely embedded into a single-quoted JavaScript string literal.
     */
    private static String escapeForJsSingleQuotedString(String s) {
        if (s == null) return "";
        StringBuilder out = new StringBuilder(s.length() + 16);
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '\\' -> out.append("\\\\");
                case '\'' -> out.append("\\'");
                case '\n' -> out.append("\\n");
                case '\r' -> out.append("\\r");
                case '\t' -> out.append("\\t");
                case '\u2028' -> out.append("\\u2028");
                case '\u2029' -> out.append("\\u2029");
                default -> out.append(c);
            }
        }
        return out.toString();
    }

    @FXML
    public void initialize() {
        webEngine = webView.getEngine();
        webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                isMapLoaded = true;
                outputArea.appendText("Map visualization loaded.\n");

                // Set up Java-to-JavaScript connector
                javaConnector = new JavaConnector();
                netscape.javascript.JSObject window = (netscape.javascript.JSObject) webEngine.executeScript("window");
                window.setMember("javaConnector", javaConnector);
            } else if (newState == Worker.State.FAILED) {
                outputArea.appendText("Error: Failed to load map visualization.\n");
            }
        });

        URL url = getClass().getResource("graph-view.html");
        if (url != null) {
            webEngine.load(url.toExternalForm());
        } else {
            outputArea.appendText("Error: graph-view.html not found\n");
        }

        startCityCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            listNeighbors(newVal);
        });

        // Auto-load polandcities.csv if it exists
        File defaultFile = new File("polandcities.csv");
        if (defaultFile.exists()) {
            loadDataFromFile(defaultFile);
        }
    }

    private void listNeighbors(String cityDisplayName) {
        if (cityDisplayName == null || graph == null) return;
        Vertex v = findVertexByDisplayName(cityDisplayName);
        if (v == null) return;

        outputArea.appendText("\nNeighbors of " + v.getName() + ":\n");
        if (v.getEdges().isEmpty()) {
            outputArea.appendText("  (No direct neighbors found)\n");
        } else {
            for (Edge e : v.getEdges()) {
                outputArea.appendText(String.format(java.util.Locale.US, "  -> %s (#%d): %.2f km\n",
                        e.getTarget().getName(), e.getTarget().getNumber(), e.getDistance()));
            }
        }
        outputArea.appendText("\n");
    }

    /**
     * Helper class for receiving events from JavaScript (Map interactions)
     */
    public class JavaConnector {
        public void setAsStart(int id, String name) {
            Platform.runLater(() -> {
                String displayName = name + " (#" + id + ")";
                startCityCombo.getSelectionModel().select(displayName);
                startCityCombo.setValue(displayName);
                outputArea.appendText("Start city selected via map: " + displayName + "\n");
                showNotification("Start City Set", "Start city set to: " + name);
            });
        }

        public void setAsTarget(int id, String name) {
            Platform.runLater(() -> {
                String displayName = name + " (#" + id + ")";
                targetCityCombo.getSelectionModel().select(displayName);
                targetCityCombo.setValue(displayName);
                outputArea.appendText("Target city selected via map: " + displayName + "\n");
                showNotification("Target City Set", "Target city set to: " + name);
            });
        }

        public void onCityClick(int id, String name) {
            Platform.runLater(() -> {
                String displayName = name + " (#" + id + ")";
                if (startCityCombo.getValue() == null) {
                    setAsStart(id, name);
                } else if (targetCityCombo.getValue() == null || !targetCityCombo.getValue().equals(displayName)) {
                    setAsTarget(id, name);
                } else {
                    startCityCombo.getSelectionModel().select(displayName);
                    startCityCombo.setValue(displayName);
                    targetCityCombo.getSelectionModel().clearSelection();
                    outputArea.appendText("Selection reset. New start: " + displayName + "\n");
                }
            });
        }
    }

    private double getNeighborRadius() {
        try {
            double radius = Double.parseDouble(neighborRadiusField.getText());
            return radius > 0 ? radius : 30.0;
        } catch (Exception e) {
            return 30.0;
        }
    }

    @FXML
    protected void onRebuildGraphClick() {
        if (graph == null) {
            outputArea.appendText("Error: Please load graph data first.\n");
            return;
        }

        double radius = getNeighborRadius();
        outputArea.appendText("Attempting to rebuild graph (Radius: " + radius + " km)...\n");

        currentResults.clear();
        lastSearchKey = "";
        if (isMapLoaded) {
            webEngine.executeScript("window.clearPath();");
        }

        javafx.concurrent.Task<List<Vertex>> rebuildTask = new javafx.concurrent.Task<>() {
            @Override
            protected List<Vertex> call() {
                // 1. Attempt building edges with user-defined radius
                graph.addEdges(radius);

                // 2. Fetch list of isolated (lonely) vertices
                return graph.getLonelyVertices();
            }
        };

        rebuildTask.setOnSucceeded(e -> {
            List<Vertex> lonelyOnes = rebuildTask.getValue();

            if (!lonelyOnes.isEmpty()) {
                outputArea.appendText("❌ Disconnected Graph! Found " + lonelyOnes.size() + " isolated cities.\n");
                lonelyOnes.stream().limit(20).forEach(v ->
                        outputArea.appendText("   - " + v.getName() + " (#" + v.getNumber() + ")\n")
                );

                if(lonelyOnes.size() > 20) {
                    outputArea.appendText("   - ... and " + (lonelyOnes.size() - 20) + " other cities\n");
                }

                showNotification(
                        "Graph Build Error",
                        "Found " + lonelyOnes.size() + " isolated vertices. Applying rescue radius of 100km.",
                        true
                );

                // Revert to rescue radius of 100km
                neighborRadiusField.setText("100.0");
                graph.addEdges(100.0);
                outputArea.appendText("Applied rescue radius: 100.0 km.\n");

            } else {
                outputArea.appendText("✅ Graph built successfully. All cities are connected.\n");

                showNotification(
                        "Success",
                        "Graph built correctly for radius " + radius + " km."
                );
            }

            if (isMapLoaded) updateMapWithCities(graph.getVertices());
        });

        new Thread(rebuildTask).start();
    }

    @FXML
    protected void onClearDataClick() {
        startCityCombo.setValue(null);
        targetCityCombo.setValue(null);
        neighborRadiusField.setText("30.0");
        outputArea.clear();
        distanceLabel.setText("Distance: -");
        timeLabel.setText("Time: -");
        nodesLabel.setText("Nodes Visited: -");
        currentResults.clear();
        lastSearchKey = "";
        if (isMapLoaded) {
            webEngine.executeScript("window.clearPath();");
        }

        if (graph != null) {
            javafx.concurrent.Task<Void> clearTask = new javafx.concurrent.Task<>() {
                @Override
                protected Void call() {
                    graph.addEdges(30.0);
                    return null;
                }
            };
            clearTask.setOnSucceeded(e -> {
                outputArea.appendText("Results and selections cleared. Neighbor Radius reset.\n");
                showNotification("Data Cleared", "All selections and graph results have been reset.");
            });
            new Thread(clearTask).start();
        } else {
            outputArea.appendText("Selections cleared.\n");
            showNotification("Data Cleared", "All selections have been cleared.");
        }
    }

    @FXML
    protected void onLoadDataClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Graph Data File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );
        File selectedFile = fileChooser.showOpenDialog(loadBtn.getScene().getWindow());

        if (selectedFile == null) {
            outputArea.appendText("Data loading cancelled.\n");
            return;
        }

        loadDataFromFile(selectedFile);
    }

    private void loadDataFromFile(File selectedFile) {
        if (selectedFile == null || !selectedFile.exists()) {
            return;
        }

        Vertex.resetCounter();
        outputArea.appendText("Loading graph data from: " + selectedFile.getName() + "...\n");
        double radius = getNeighborRadius();

        javafx.concurrent.Task<Graph> loadTask = new javafx.concurrent.Task<>() {
            @Override
            protected Graph call() throws Exception {
                // Try with Windows-1250 then UTF-8
                Graph g = GraphLoader.loadGraphData(selectedFile.getAbsolutePath(), Charset.forName("Windows-1250"), radius);
                if (g == null || g.getVertices().isEmpty()) {
                    g = GraphLoader.loadGraphData(selectedFile.getAbsolutePath(), StandardCharsets.UTF_8, radius);
                }
                return g;
            }
        };

        loadTask.setOnSucceeded(e -> {
            graph = loadTask.getValue();
            if (graph != null && !graph.getVertices().isEmpty()) {
                List<Vertex> vertices = graph.getVertices();
                outputArea.appendText("Graph successfully loaded with " + vertices.size() + " vertices.\n");

                List<String> cityDisplayNames = vertices.stream()
                        .map(v -> v.getName() + " (#" + v.getNumber() + ")")
                        .sorted()
                        .collect(Collectors.toList());

                startCityCombo.getItems().setAll(cityDisplayNames);
                targetCityCombo.getItems().setAll(cityDisplayNames);

                if (isMapLoaded) {
                    updateMapWithCities(vertices);
                }
                showNotification("Data Loaded", "Graph data loaded successfully.");
            } else {
                outputArea.appendText("Error: Could not load any city data.\n");
                showNotification("Loading Error", "No valid city data found in the file.", true);
            }
        });

        loadTask.setOnFailed(e -> {
            outputArea.appendText("Critical error during data loading.\n");
            showNotification("Critical Error", "An error occurred while processing the file.", true);
        });

        new Thread(loadTask).start();
    }

    private void updateMapWithCities(List<Vertex> vertices) {
        webEngine.executeScript("window.clearAll();");

        int chunkSize = 2000;
        for (int i = 0; i < vertices.size(); i += chunkSize) {
            int end = Math.min(i + chunkSize, vertices.size());
            List<Vertex> chunk = vertices.subList(i, end);
            StringBuilder sb = new StringBuilder("[");
            for (int j = 0; j < chunk.size(); j++) {
                Vertex v = chunk.get(j);
                String escapedName = v.getName()
                        .replace("\\", "\\\\")
                        .replace("\"", "\\\"")
                        .replace("\n", "\\n");

                sb.append("{\"lat\": ").append(String.format(java.util.Locale.US, "%.6f", v.getX() / 1000.0))
                        .append(", \"lng\": ").append(String.format(java.util.Locale.US, "%.6f", v.getY() / 1000.0))
                        .append(", \"name\": \"").append(escapedName)
                        .append("\", \"id\": ").append(v.getNumber()).append("}");
                if (j < chunk.size() - 1) sb.append(",");
            }
            sb.append("]");

            String script = "window.addCities(JSON.parse('" + escapeForJsSingleQuotedString(sb.toString()) + "'));";
            webEngine.executeScript(script);
        }
        webEngine.executeScript("map.invalidateSize();");
        outputArea.appendText("Map updated with city markers.\n");
    }

    @FXML
    protected void onFindPathClick() {
        if (graph == null) {
            outputArea.appendText("Error: Load graph data first.\n");
            showNotification("Missing Data", "Please load graph data first.", true);
            return;
        }

        String startDisplayName = startCityCombo.getValue();
        String targetDisplayName = targetCityCombo.getValue();

        if (startDisplayName == null || targetDisplayName == null) {
            outputArea.appendText("Error: Please select both start and target cities.\n");
            showNotification("Selection Missing", "Both start and target cities must be selected.", true);
            return;
        }

        Vertex start = findVertexByDisplayName(startDisplayName);
        Vertex target = findVertexByDisplayName(targetDisplayName);

        if (start == null || target == null) return;

        String searchKey = start.getNumber() + " -> " + target.getNumber();
        if (!searchKey.equals(lastSearchKey)) {
            currentResults.clear();
            lastSearchKey = searchKey;
        }

        PathfindingResult result = null;
        if (bfsRadio.isSelected()) {
            result = BFS.findShortestPath(start, target, graph);
            result.setAlgorithmName("BFS");
        } else if (dijkstraRadio.isSelected()) {
            result = Dijkstra.findShortestPath(start, target, graph);
            result.setAlgorithmName("Dijkstra");
        } else if (astarRadio.isSelected()) {
            result = AStar.findShortestPath(start, target, graph);
            result.setAlgorithmName("A*");
        }

        if (result != null) {
            currentResults.add(result);
            displayResult(result);
            if (currentResults.size() > 1) {
                checkAndHighlightBest();
            }
        }
    }

    private void checkAndHighlightBest() {
        PathfindingResult fastest = Collections.min(currentResults, java.util.Comparator.comparingDouble(PathfindingResult::getTimeMs));
        PathfindingResult leastNodes = Collections.min(currentResults, java.util.Comparator.comparingInt(PathfindingResult::getNodesVisited));

        outputArea.appendText("⭐ BEST PERFORMANCE SO FAR ⭐\n");
        outputArea.appendText("Fastest: " + fastest.getAlgorithmName() + " (" + String.format("%.2f", fastest.getTimeMs()) + " ms)\n");
        outputArea.appendText("Most Efficient: " + leastNodes.getAlgorithmName() + " (" + leastNodes.getNodesVisited() + " nodes)\n");
        outputArea.appendText("--------------------------------\n\n");
    }

    private Vertex findVertexByDisplayName(String displayName) {
        if (displayName == null || !displayName.contains(" (#")) return null;
        try {
            int startIdx = displayName.lastIndexOf(" (#") + 3;
            int endIdx = displayName.lastIndexOf(")");
            int id = Integer.parseInt(displayName.substring(startIdx, endIdx));
            return graph.getVertices().get(id);
        } catch (Exception e) {
            return null;
        }
    }

    private void displayResult(PathfindingResult result) {
        List<Vertex> path = result.getPath();
        List<Vertex> explored = result.getExploredNodes();

        if (path.isEmpty()) {
            outputArea.appendText("Result: No path found using " + result.getAlgorithmName() + ".\n");
            showNotification("No Path Found", "No connection exists between selected cities.", true);
            distanceLabel.setText("Distance: N/A");
            if (!explored.isEmpty()) sendExploredToMap(explored);
            return;
        }

        StringBuilder jsonBuilder = new StringBuilder("[");
        for (int i = 0; i < path.size(); i++) {
            Vertex v = path.get(i);
            jsonBuilder.append(String.format(java.util.Locale.US, "{\"lat\": %.6f, \"lng\": %.6f}", v.getX() / 1000.0, v.getY() / 1000.0));
            if (i < path.size() - 1) jsonBuilder.append(",");
        }
        jsonBuilder.append("]");

        outputArea.appendText("Algorithm: " + result.getAlgorithmName() + "\n");
        outputArea.appendText("Time: " + String.format("%.2f", result.getTimeMs()) + " ms | Nodes visited: " + result.getNodesVisited() + "\n");
        outputArea.appendText("Total Distance: " + String.format("%.2f km", result.getTotalCost()) + "\n\n");

        distanceLabel.setText(String.format("Distance: %.2f km", result.getTotalCost()));
        timeLabel.setText(String.format("Time: %.2f ms", result.getTimeMs()));
        nodesLabel.setText("Nodes Visited: " + result.getNodesVisited());

        sendExploredToMap(explored);
        webEngine.executeScript("window.drawPath('" + escapeForJsSingleQuotedString(jsonBuilder.toString()) + "');");
    }

    private void sendExploredToMap(List<Vertex> explored) {
        webEngine.executeScript("window.clearExplored();");
        if (explored == null || explored.isEmpty()) return;

        int chunkSize = 2000;
        for (int i = 0; i < explored.size(); i += chunkSize) {
            int end = Math.min(i + chunkSize, explored.size());
            List<Vertex> chunk = explored.subList(i, end);
            StringBuilder sb = new StringBuilder("[");
            for (int j = 0; j < chunk.size(); j++) {
                Vertex v = chunk.get(j);
                sb.append(String.format(java.util.Locale.US, "{\"lat\": %.6f, \"lng\": %.6f}", v.getX() / 1000.0, v.getY() / 1000.0));
                if (j < chunk.size() - 1) sb.append(",");
            }
            sb.append("]");
            String script = "window.addExplored(JSON.parse('" + escapeForJsSingleQuotedString(sb.toString()) + "'));";
            webEngine.executeScript(script);
        }
    }

    private void showNotification(String title, String content) {
        showNotification(title, content, false);
    }

    private void showNotification(String title, String content, boolean isError) {
        Platform.runLater(() -> {
            Notifications notification = Notifications.create()
                    .title(title)
                    .text(content)
                    .hideAfter(Duration.seconds(3))
                    .position(Pos.BOTTOM_RIGHT)
                    .darkStyle();

            if (loadBtn != null && loadBtn.getScene() != null && loadBtn.getScene().getWindow() != null) {
                notification.owner(loadBtn.getScene().getWindow());
            }

            if (isError) notification.showError();
            else notification.showInformation();
        });
    }
}