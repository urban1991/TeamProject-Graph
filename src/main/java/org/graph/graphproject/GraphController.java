package org.graph.graphproject;

import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.web.WebView;
import javafx.scene.web.WebEngine;
import org.controlsfx.control.SearchableComboBox;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

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
    @FXML private TextField weightField;
    @FXML private Button setWeightBtn;
    @FXML private Button findPathBtn;
    @FXML private Label distanceLabel;
    @FXML private Label currentWeightLabel;
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

        // Handle weight field enabling
        weightField.setText("10.0");
        weightField.setDisable(true);
        setWeightBtn.setDisable(true);
        algoGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            boolean isWeighted = dijkstraRadio.isSelected() || astarRadio.isSelected();
            weightField.setDisable(!isWeighted);
            setWeightBtn.setDisable(!isWeighted);
        });

        startCityCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            updateCurrentWeightDisplay();
            listNeighbors(newVal);
        });
        targetCityCombo.valueProperty().addListener((obs, oldVal, newVal) -> updateCurrentWeightDisplay());

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
            outputArea.appendText("  (No direct neighbors)\n");
        } else {
            for (Edge e : v.getEdges()) {
                outputArea.appendText(String.format(java.util.Locale.US, "  -> %s (#%d): %.2f km\n",
                    e.getTarget().getName(), e.getTarget().getNumber(), e.getDistance()));
            }
        }
        outputArea.appendText("\n");
    }

    /**
     * Helper class for receiving events from JavaScript
     */
    public class JavaConnector {
        public void setAsStart(int id, String name) {
            javafx.application.Platform.runLater(() -> {
                String displayName = name + " (#" + id + ")";
                startCityCombo.getSelectionModel().select(displayName);
                startCityCombo.setValue(displayName);
                outputArea.appendText("Selected Start via Map: " + displayName + "\n");
                updateCurrentWeightDisplay();
            });
        }

        public void setAsTarget(int id, String name) {
            javafx.application.Platform.runLater(() -> {
                String displayName = name + " (#" + id + ")";
                targetCityCombo.getSelectionModel().select(displayName);
                targetCityCombo.setValue(displayName);
                outputArea.appendText("Selected Target via Map: " + displayName + "\n");
                updateCurrentWeightDisplay();
            });
        }

        public void onCityClick(int id, String name) {
            javafx.application.Platform.runLater(() -> {
                String displayName = name + " (#" + id + ")";
                if (startCityCombo.getValue() == null) {
                    startCityCombo.getSelectionModel().select(displayName);
                    startCityCombo.setValue(displayName);
                    outputArea.appendText("Selected Start via Click: " + displayName + "\n");
                } else if (targetCityCombo.getValue() == null || !targetCityCombo.getValue().equals(displayName)) {
                    targetCityCombo.getSelectionModel().select(displayName);
                    targetCityCombo.setValue(displayName);
                    outputArea.appendText("Selected Target via Click: " + displayName + "\n");
                } else {
                    startCityCombo.getSelectionModel().select(displayName);
                    startCityCombo.setValue(displayName);
                    targetCityCombo.getSelectionModel().clearSelection();
                    outputArea.appendText("Reset via Click. Selected Start: " + displayName + "\n");
                }
                updateCurrentWeightDisplay();
            });
        }
    }

    private void updateCurrentWeightDisplay() {
        if (graph == null) {
            currentWeightLabel.setText("Edge Weight: -");
            return;
        }
        String startName = startCityCombo.getValue();
        String targetName = targetCityCombo.getValue();
        if (startName == null || targetName == null) {
            currentWeightLabel.setText("Edge Weight: -");
            return;
        }

        Vertex v1 = findVertexByDisplayName(startName);
        Vertex v2 = findVertexByDisplayName(targetName);

        if (v1 != null && v2 != null) {
            double weight = -1;
            for (Edge e : v1.getEdges()) {
                if (e.getTarget().equals(v2)) {
                    weight = e.getDistance();
                    break;
                }
            }
            if (weight >= 0) {
                currentWeightLabel.setText(String.format(java.util.Locale.US, "Edge Weight: %.2f km", weight));
            } else {
                currentWeightLabel.setText("Edge Weight: No direct edge");
            }
        } else {
            currentWeightLabel.setText("Edge Weight: -");
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
            outputArea.appendText("Error: Load graph data first.\n");
            return;
        }
        double radius = getNeighborRadius();
        outputArea.appendText("Rebuilding graph edges with Neighbor Radius: " + radius + " km...\n");

        // Clear old results as they are no longer valid for the new graph structure
        currentResults.clear();
        lastSearchKey = "";
        if (isMapLoaded) {
            webEngine.executeScript("window.clearPath();");
        }

        javafx.concurrent.Task<Void> rebuildTask = new javafx.concurrent.Task<>() {
            @Override
            protected Void call() {
                graph.addEdges(radius);
                return null;
            }
        };

        rebuildTask.setOnSucceeded(e -> {
            outputArea.appendText("Graph edges successfully rebuilt.\n");
            updateCurrentWeightDisplay();
            // Refresh map markers and state
            if (isMapLoaded && graph != null) {
                updateMapWithCities(graph.getVertices());
            }
        });

        new Thread(rebuildTask).start();
    }

    @FXML
    protected void onClearDataClick() {
        startCityCombo.setValue(null);
        targetCityCombo.setValue(null);
        weightField.setText("10.0");
        neighborRadiusField.setText("30.0");
        outputArea.clear();
        distanceLabel.setText("Distance: -");
        currentWeightLabel.setText("Edge Weight: -");
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
                updateCurrentWeightDisplay();
                outputArea.appendText("Results and selections cleared. Custom weights and Neighbor Radius reset.\n");
            });
            new Thread(clearTask).start();
        } else {
            outputArea.appendText("Results and selections cleared. Custom weights reset.\n");
        }
    }

    @FXML
    protected void onSetWeightClick() {
        if (graph == null) {
            outputArea.appendText("Error: Load graph data first.\n");
            return;
        }
        String startName = startCityCombo.getValue();
        String targetName = targetCityCombo.getValue();
        if (startName == null || targetName == null) {
            outputArea.appendText("Error: Select start and target cities to set weight between them.\n");
            return;
        }
        String weightText = weightField.getText();
        try {
            double weight = Double.parseDouble(weightText);
            if (weight < 0) throw new NumberFormatException();

            Vertex v1 = findVertexByDisplayName(startName);
            Vertex v2 = findVertexByDisplayName(targetName);

            if (v1 != null && v2 != null) {
                updateOrAddEdge(v1, v2, weight);
                updateOrAddEdge(v2, v1, weight);
                outputArea.appendText("Weight between " + v1.getName() + " and " + v2.getName() + " set to " + weight + " km\n");
                updateCurrentWeightDisplay();
            }
        } catch (NumberFormatException e) {
            outputArea.appendText("Error: Invalid weight. Please enter a positive number.\n");
        }
    }

    private void updateOrAddEdge(Vertex from, Vertex to, double weight) {
        boolean found = false;
        for (Edge e : from.getEdges()) {
            if (e.getTarget().equals(to)) {
                e.setDistance(weight);
                found = true;
                break;
            }
        }
        if (!found) {
            from.addEdge(to, weight);
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

        // Use a background task to load data to keep UI responsive
        javafx.concurrent.Task<Graph> loadTask = new javafx.concurrent.Task<>() {
            @Override
            protected Graph call() throws Exception {
                // Try with Windows-1250 (common for Polish CSVs)
                Graph g = GraphLoader.loadGraphData(selectedFile.getAbsolutePath(), Charset.forName("Windows-1250"), radius);
                // If it looks like it failed or didn't load anything, try UTF-8
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
                } else {
                    outputArea.appendText("Waiting for map to load...\n");
                    webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
                        if (newState == Worker.State.SUCCEEDED) {
                            if (graph != null) {
                                updateMapWithCities(graph.getVertices());
                            }
                        }
                    });
                }
            } else {
                outputArea.appendText("Error: Could not load any city data.\n");
            }
        });

        loadTask.setOnFailed(e -> {
            outputArea.appendText("Critical error during data loading.\n");
            loadTask.getException().printStackTrace();
        });

        new Thread(loadTask).start();
    }

    private void updateMapWithCities(List<Vertex> vertices) {
        webEngine.executeScript("window.clearAll();");

        // Group cities into chunks to avoid long script strings
        int chunkSize = 2000;
        for (int i = 0; i < vertices.size(); i += chunkSize) {
            int end = Math.min(i + chunkSize, vertices.size());
            List<Vertex> chunk = vertices.subList(i, end);

            StringBuilder sb = new StringBuilder("[");
            for (int j = 0; j < chunk.size(); j++) {
                Vertex v = chunk.get(j);
                String escapedName = v.getName().replace("\\", "\\\\").replace("\"", "\\\"").replace("'", "\\'");
                sb.append("{\"lat\": ").append(String.format(java.util.Locale.US, "%.6f", v.getX() / 1000.0))
                  .append(", \"lng\": ").append(String.format(java.util.Locale.US, "%.6f", v.getY() / 1000.0))
                  .append(", \"name\": \"").append(escapedName)
                  .append("\", \"id\": ").append(v.getNumber()).append("}");
                if (j < chunk.size() - 1) sb.append(",");
            }
            sb.append("]");

            String jsonString = sb.toString();
            // JSON.parse is safer and faster than executing raw objects
            String script = "window.addCities(JSON.parse('" + jsonString.replace("\\", "\\\\").replace("'", "\\'") + "'));";
            webEngine.executeScript(script);
        }
        webEngine.executeScript("map.invalidateSize();");
        outputArea.appendText("Map updated with cities.\n");
    }

    @FXML
    protected void onFindPathClick() {
        if (graph == null) {
            outputArea.appendText("Error: Load graph data first.\n");
            return;
        }

        String startDisplayName = startCityCombo.getValue();
        String targetDisplayName = targetCityCombo.getValue();

        if (startDisplayName == null || targetDisplayName == null) {
            outputArea.appendText("Error: Select start and target cities.\n");
            return;
        }

        Vertex start = findVertexByDisplayName(startDisplayName);
        Vertex target = findVertexByDisplayName(targetDisplayName);

        if (start == null || target == null) return;

        // Reset results if searching for a different pair
        String searchKey = start.getNumber() + " -> " + target.getNumber();
        if (!searchKey.equals(lastSearchKey)) {
            currentResults.clear();
            lastSearchKey = searchKey;
        }

        PathfindingResult result = null;
        String algoName = "";

        if (bfsRadio.isSelected()) {
            algoName = "BFS";
            result = BFS.findShortestPath(start, target, graph);
            result.setAlgorithmName(algoName);
        } else if (dijkstraRadio.isSelected()) {
            algoName = "Dijkstra";
            result = Dijkstra.findShortestPath(start, target, graph);
            result.setAlgorithmName(algoName);
        } else if (astarRadio.isSelected()) {
            algoName = "A*";
            result = AStar.findShortestPath(start, target, graph);
            result.setAlgorithmName(algoName);
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
        outputArea.appendText("Fastest Algorithm: " + fastest.getAlgorithmName() + " (" + String.format("%.2f", fastest.getTimeMs()) + " ms)\n");
        outputArea.appendText("Most Efficient (Nodes Visited): " + leastNodes.getAlgorithmName() + " (" + leastNodes.getNodesVisited() + ")\n");
        outputArea.appendText("--------------------------------\n\n");
    }

    private Vertex findVertexByDisplayName(String displayName) {
        if (displayName == null || !displayName.contains(" (#")) return null;
        try {
            int startIdx = displayName.lastIndexOf(" (#") + 3;
            int endIdx = displayName.lastIndexOf(")");
            int id = Integer.parseInt(displayName.substring(startIdx, endIdx));
            if (id >= 0 && id < graph.getVertices().size()) {
                Vertex v = graph.getVertices().get(id);
                if (v.getNumber() == id) return v;
            }
            // Fallback just in case
            return graph.getVertices().stream()
                    .filter(v -> v.getNumber() == id)
                    .findFirst()
                    .orElse(null);
        } catch (Exception e) {
            return null;
        }
    }

    private void displayResult(PathfindingResult result) {
        List<Vertex> path = result.getPath();
        List<Vertex> explored = result.getExploredNodes();
        double durationMs = result.getTimeMs();
        int visited = result.getNodesVisited();
        double totalDistance = result.getTotalCost();

        if (path.isEmpty()) {
            outputArea.appendText("No path found.\n");
            distanceLabel.setText("Distance: N/A");
            timeLabel.setText(String.format("Time: %.2f ms", durationMs));
            nodesLabel.setText("Nodes Visited: " + visited);

            if (!explored.isEmpty()) {
                sendExploredToMap(explored);
            }
            return;
        }

        StringBuilder pathStr = new StringBuilder();
        StringBuilder jsonBuilder = new StringBuilder("[");

        for (int i = 0; i < path.size(); i++) {
            Vertex v = path.get(i);
            pathStr.append(v.getName());
            if (i < path.size() - 1) {
                pathStr.append(" -> ");
            }
            jsonBuilder.append(String.format(java.util.Locale.US, "{\"lat\": %.6f, \"lng\": %.6f}", v.getX() / 1000.0, v.getY() / 1000.0));
            if (i < path.size() - 1) {
                jsonBuilder.append(",");
            }
        }
        jsonBuilder.append("]");

        outputArea.appendText("Algorithm: " + result.getAlgorithmName() + "\n");
        outputArea.appendText("Path found in " + String.format("%.2f", durationMs) + " ms\n");
        outputArea.appendText("Nodes visited: " + visited + "\n");
        outputArea.appendText("Path: " + pathStr.toString() + "\n");
        outputArea.appendText("Total Distance: " + String.format("%.2f km", totalDistance) + "\n\n");

        distanceLabel.setText(String.format("Distance: %.2f km", totalDistance));
        timeLabel.setText(String.format("Time: %.2f ms", durationMs));
        nodesLabel.setText("Nodes Visited: " + visited);

        // Update map
        sendExploredToMap(explored);

        String pathJson = jsonBuilder.toString();
        String escapedPathJson = pathJson.replace("\\", "\\\\").replace("'", "\\'");
        webEngine.executeScript("window.drawPath('" + escapedPathJson + "');");
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

            String jsonString = sb.toString();
            String script = "window.addExplored(JSON.parse('" + jsonString.replace("\\", "\\\\").replace("'", "\\'") + "'));";
            webEngine.executeScript(script);
        }
    }

}
