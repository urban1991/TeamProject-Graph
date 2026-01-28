module org.graph.graphproject {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires jdk.jsobject;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;

    opens org.graph.graphproject to javafx.fxml, javafx.web;
    exports org.graph.graphproject;
}
