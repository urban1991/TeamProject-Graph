# 🚀 TeamProject-Graph — Shortest Paths (BFS, Dijkstra, A*) with Swing UI

This project implements shortest-path search in graphs using classical and heuristic algorithms. The code is written in Java and will include a multi‑functional Swing UI to visualize the search process and the resulting paths.

## 🎯 Project Goal
Design and implement a system that finds shortest paths in a graph (e.g., grid, map, network) between selected start and goal nodes. The system supports:
- BFS (Breadth-First Search) — shortest paths in unweighted graphs,
- Dijkstra’s Algorithm — shortest paths in weighted graphs (non‑negative weights),
- A* (A‑star) — heuristic search (e.g., Euclidean or Manhattan), optimal under an admissible heuristic.

The project also covers complexity and performance analysis and an optional visualization of the search state (visited nodes, frontier/open set, final path) in the Swing UI.

## 📦 Current Status (WIP)
The repository contains an initial graph representation of Polish cities loaded from `polandcities.csv` and a simple nearest‑neighbor edge builder (up to ~10 km). Implementations of BFS, Dijkstra, A*, and the Swing UI are planned next.

## ✨ Planned Features
- Adjacency‑list graph representation (`Vertex`, `Edge`, `Graph`).
- FileReaders for CSV.
- Shortest path algorithms:
  - BFS — for unweighted graphs (minimize edge count),
  - Dijkstra — for weighted graphs (minimize total weight),
  - A* — heuristic version with optimality under an admissible heuristic (e.g., Euclidean/Manhattan).
- Performance comparison (time/memory) across different graph sizes/densities.
- Swing UI for interactive visualization:
  - graph view, start/goal selection,
  - visualization of visited nodes and edges,
  - final path and statistics (visited count, cost, time),
  - dialog to choose algorithm and A* heuristic.

## 🧱 Project Structure
- `src/Main.java` — app entry point, CSV loading, graph initialization.
- `src/Graph.java` — graph representation, edge building, helpers.
- `src/Vertex.java` — graph node (ID, name, coordinates, edge list).
- `src/Edge.java` — graph edge (target node, weight/distance).
- `polandcities.csv` — input data (Polish cities). (for development only)

Planned additions: `BfsPathfinder`, `DijkstraPathfinder`, `AStarPathfinder`, common `Pathfinder` interface, and Swing modules (`GraphPanel`, `ControlPanel`, etc.).

## 🛠 Prerequisites
- Java 17+ (JDK 17 or newer recommended).
- IntelliJ IDEA or another Java IDE.
- OS: Windows/macOS/Linux.

Note: `Main.java` currently contains an example absolute Windows path to the CSV. Adjust it for your environment or switch to a relative path before running.

## ▶️ Build & Run (for now)
Since there is no build system yet, compile/run directly:

1) Compile sources (from project root):
```bash
javac -d out src/*.java
```
2) Run:
```bash
java -cp out Main
```

In IntelliJ IDEA: create an Application run configuration with `Main` as the main class, and ensure the CSV path is valid.

We plan to add a build tool (e.g., Gradle) and CLI/GUI parameters for data file selection.

## 🧠 Algorithms & Properties
### BFS
- Use case: unweighted graphs.
- Guarantee: returns a path with the minimum number of edges.
- Complexity: O(V + E) time, O(V) space.

### Dijkstra
- Use case: weighted graphs with non‑negative weights.
- Guarantee: minimum total weight from the source to all nodes (or a target).
- Complexity: O((V + E) log V) with a priority queue (`PriorityQueue`).

### A*
- Use case: large search spaces; a good heuristic reduces explored nodes.
- Example heuristics: Euclidean, Manhattan (for grids).
- Optimality condition: admissible heuristic (never overestimates) ⇒ optimal path.
- Complexity: heuristic‑dependent; often explores fewer nodes than Dijkstra in practice.

## 📈 Performance Analysis (plan)
- Compare time and memory for BFS / Dijkstra / A* across varying graph sizes/densities.
- Collect metrics: visited node count, priority queue ops, execution time, memory usage.
- Produce a short report with figures.

## 🖥 Swing UI (plan)
- Graph drawing panel (zoom, pan, node selection).
- Controls for algorithm choice, start/goal selection, animation speed.
- Inspect neighbors/weights and frontier/open set state.
- Import/export graph (CSV/JSON) and screenshots.

## 📂 Input Data
- `polandcities.csv`: city name, latitude, longitude.
- Edge building: currently connects city pairs within ~10 km (simple threshold using scaled Euclidean distance).

## 🧪 Tests (plan)
- Unit/integration tests for:
  - path correctness (BFS/Dijkstra/A*),
  - edge cases (disconnected graph, multiple goals, no path),
  - weight/heuristic consistency,
  - stability and performance on larger graphs.

## 🤝 Contributing
1. Create a feature/bugfix branch.
2. Add tests for new functionality.
3. Write clear, concise commit messages.
4. Open a Pull Request for review.

