module org.graph.graphproject {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;

    opens org.graph.graphproject to javafx.fxml;
    exports org.graph.graphproject;
}