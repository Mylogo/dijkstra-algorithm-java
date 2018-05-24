package me.mylogo.dijkstra.gui;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import me.mylogo.dijkstra.graph.Dijkstra;
import me.mylogo.dijkstra.graph.Graph;
import me.mylogo.dijkstra.point.Point;
import me.mylogo.dijkstra.point.PointGraphParser;
import me.mylogo.dijkstra.point.PointGraphWriter;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Dennis Heckmann on 19.05.18
 * Copyright (c) 2018 Dennis Heckmann
 * GitHub: https://github.com/Mylogo
 * Web: http://mylogo.me
 * Mail: contact@mylogo.me | denheckmann@googlemail.com
 * Discord: Mylogo#4884 | Skype: Mylogo55
 */
public class CreateGraph {

    @FXML
    private CheckBox drawAxesBox;
    @FXML
    private CheckBox showWeightsBox;
    @FXML
    private Button moveManuallyButton;
    @FXML
    private CheckBox showAllEdgesCheck;
    @FXML
    private ChoiceBox<Point> fromChoice;
    @FXML
    private ChoiceBox<Point> toChoice;
    @FXML
    private TextField nameField;
    @FXML
    private TextField xField;
    @FXML
    private TextField yField;
    @FXML
    private TextField connectedField;
    @FXML
    private Label errorLabel;
    @FXML
    private Label pointsLabel;
    @FXML
    private HBox actionsBox;
    @FXML
    private VBox inspectorBox;
    @FXML
    private VBox pointsBox;
    @FXML
    private ListView<Point> pointsListView;
    @FXML
    private Canvas canvas;

    private Graph<Point> graph;
    private Dijkstra<Point>.Connection connection;
    private GraphProjection projection;
    private Stage stage;

    public void initialize() {
        graph = new Graph<>();
        canvas.autosize();
        canvas.heightProperty().bind(((StackPane) canvas.getParent()).heightProperty().subtract(20));
        canvas.widthProperty().bind(canvas.heightProperty());
        pointsListView.prefHeightProperty().bind(pointsBox.heightProperty().subtract(50));
        canvas.setLayoutX(0);
        canvas.setLayoutY(0);
        this.projection = new GraphProjection(this, canvas, graph);
        canvas.setOnMouseClicked(click -> {
            if (click.getButton() == MouseButton.SECONDARY) {
                double x = projection.unprojectX(click.getX());
                double y = projection.unprojectY(click.getY());
                Set<Point> knots = graph.getKnots();
                if (knots.size() > 0) {
                    Iterator<Point> iterator = knots.iterator();
                    Point nearest = iterator.next();
                    double distance = nearest.distanceSquared(x, y);
                    while (iterator.hasNext()) {
                        Point next = iterator.next();
                        double newDistance = next.distanceSquared(x, y);
                        if (newDistance < distance) {
                            distance = newDistance;
                            nearest = next;
                        }
                    }
                    selectPoint(nearest);
                }
            } else {
                if (moving) {
                    if (selected != null) {
                        double x = projection.unprojectX(click.getX());
                        double y = projection.unprojectY(click.getY());
                        selected.setX(x);
                        selected.setY(y);
                        refreshGraph();
                        xField.setText(String.valueOf(x));
                        yField.setText(String.valueOf(y));
                    }
                } else {
                    selectPoint(null);
                }
            }
        });
        initFields();
    }

    public void setGraph(Graph<Point> graph) {
        this.graph = graph;
        if (this.projection != null)
            this.projection.stopProjection();
        this.projection = new GraphProjection(this, canvas, graph);
        refreshListView();
        paintCanvas();
    }

    private Point selected;
    private void selectPoint(Point point) {
        selected = point;
        if (selected != null) {
            nameField.setText(point.getId());
            xField.setText(String.valueOf(point.getX()));
            yField.setText(String.valueOf(point.getY()));
            connectedField.setText(point.getConnected().stream().collect(Collectors.joining(",")));
        } else {
            nameField.setText(null);
            xField.setText(null);
            yField.setText(null);
            connectedField.setText(null);
        }
    }

    private void refreshListView() {
        ObservableList<Point> points = FXCollections.observableArrayList(graph.getKnots());
        FXCollections.sort(points, (p1, p2) -> p1.getId().compareTo(p2.getId()));
        pointsListView.setItems(points);
        pointsListView.refresh();
        StringConverter<Point> stringConverter = new StringConverter<Point>() {
            @Override
            public String toString(Point object) {
                return object.getId();
            }

            @Override
            public Point fromString(String string) {
                return null;
            }
        };
        fromChoice.setItems(points);
        fromChoice.setConverter(stringConverter);
        toChoice.setItems(points);
        toChoice.setConverter(stringConverter);
    }

    public Dijkstra<Point>.Connection getConnection() {
        return connection;
    }

    private void paintCanvas() {
        projection.paintCanvas();
    }

    public Point getSelectedPoint() {
        return selected;
    }

    public void onPlusClick(MouseEvent event) {
        String id = calculateNewId();
        Point newPoint = new Point(id, 0, 0);
        graph.addKnot(newPoint);
        selectPoint(newPoint);
        refreshListView();
    }

    private String calculateNewId() {
        return String.valueOf(calculateNewId(1));
    }

    private void initFields() {
        pointsListView.setCellFactory(cell -> new ListCell<Point>() {
            @Override
            protected void updateItem(Point item, boolean empty) {
                setOnMouseClicked(click -> {
                    if (item != null && !empty)
                        selectPoint(item);
                });
                if (item == null || empty)
                    setText(null);
                else
                    setText(item.getId());
            }
        });
        nameField.setOnKeyPressed(e -> {
            if (selected != null) {
                if (e.getCode() == KeyCode.ENTER) {
                    String text = nameField.getText();
                    if (text.isEmpty()) {
                        setError("Empty ID");
                        return;
                    }
                    Point current = graph.getKnots().stream().filter(knot -> knot.getId().equals(text)).findFirst().orElse(null);
                    if (current == null) {
//                        this.selected.getConnected().stream().map(con -> graph.getKnots().stream().filter(p -> p.getId().equals(con)).findFirst().orElse(null)).filter(Objects::nonNull).forEach(p -> {
//                            p.removeConnection(this.selected.getId());
//                        });
                        this.selected.setId(text);
                        refreshGraph();
                        connection = null;
                        refreshListView();
                    } else {
                        setError("ID exists already");
                    }
                }
            }
        });
        xField.setOnKeyPressed(e -> {
            if (selected != null) {
                if (e.getCode() == KeyCode.ENTER) {
                    String text = xField.getText();
                    if (text.isEmpty()) {
                        setError("Empty X");
                        return;
                    }
                    try {
                        double x = Double.parseDouble(text);
                        selected.setX(x);
                        connection = null;
                    } catch (NumberFormatException ex) {
                        setError("Invalid X");
                    }
                }
            }
        });
        yField.setOnKeyPressed(e -> {
            if (selected != null) {
                if (e.getCode() == KeyCode.ENTER) {
                    String text = yField.getText();
                    if (text.isEmpty()) {
                        setError("Empty Y");
                        return;
                    }
                    try {
                        double y = Double.parseDouble(text);
                        selected.setY(y);
                        connection = null;
                        refreshGraph();
                    } catch (NumberFormatException ex) {
                        setError("Invalid Y");
                    }
                }
            }
        });
        connectedField.setOnKeyPressed(e -> {
            if (selected != null) {
                if (e.getCode() == KeyCode.ENTER) {
                    String text = connectedField.getText();
                    selected.getConnected().stream().map(con -> graph.getKnots().stream().filter(p -> p.getId().equals(con)).findFirst().orElse(null)).filter(Objects::nonNull).forEach(p -> p.removeConnection(selected.getId()));
                    if (text.isEmpty()) {
                        selected.setConnections(new HashSet<>());
                    } else {
                        selected.setConnections(Stream.of(text.split(",")).collect(Collectors.toSet()));
                    }
                    connection = null;
                    refreshGraph();
                }
            }
        });
        fromChoice.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> checkDijkstra(newValue, toChoice.getValue()));
        toChoice.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> checkDijkstra(fromChoice.getValue(), newValue));
    }

    private void checkDijkstra(Point from, Point to) {
        if (from != null && to != null) {
            this.connection = new Dijkstra<>(graph).calculate(from, to);
            if (this.connection == null)
                setError("No path found");
        }
    }

    private ErrorThread errorThread;
    private void setError(String error) {
        if (errorThread != null)
            errorThread.interruptMe();
        errorLabel.setText(error);
        (errorThread = new ErrorThread()).start();
    }

    private void refreshGraph() {
        this.graph = PointGraphParser.insertKnotsIntoGraph(new Graph<>(), this.graph.getKnots());
        this.connection = null;
        this.toChoice.setValue(null);
        this.fromChoice.setValue(null);
    }

    private int calculateNewId(int id) {
        return graph.getKnots().stream().filter(knot -> knot.getId().equals(String.valueOf(id))).findFirst().orElse(null) == null ? id : calculateNewId(id+1);
    }

    public void onExportClick(MouseEvent event) {
        if (graph.getKnots().size() > 0) {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Save Points under");
            chooser.setInitialFileName("export_points.txt");
            File file = chooser.showSaveDialog(stage);
            if (file != null) {
                try {
                    new PointGraphWriter().writeFile(file, this.graph);
                    setError("Saved file");
                } catch (Exception e) {
                    setError(e.getMessage());
                }
            } else {
                setError("No file specified");
            }
        } else {
            setError("No points");
        }
    }

    public Graph<Point> getGraph() {
        return graph;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
        this.stage.setOnCloseRequest(e -> {
            projection.stopProjection();
        });
    }

    boolean showAllEdges() {
        return showAllEdgesCheck.isSelected();
    }

    public void onNewGraphClick(MouseEvent event) {
        projection.stopProjection();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("me/mylogo/dijkstra/fxml/hello_frame.fxml"));
            HBox root = loader.load();
            OpeningFrame controller = loader.getController();
            controller.setStage(stage);
            Scene main = new Scene(root);
            stage.setScene(main);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean moving;
    public void onMoveManuallyClick(MouseEvent event) {
        if (moving) {
            moving = false;
            moveManuallyButton.getStyleClass().remove("move-manually-selected");
        } else {
            moving = true;
            moveManuallyButton.getStyleClass().add("move-manually-selected");
        }
    }

    public boolean showWeights() {
        return showWeightsBox.isSelected();
    }

    public boolean showAxes() {
        return drawAxesBox.isSelected();
    }

    public void onSaveAsImageClick(ActionEvent actionEvent) {
        setError("Not implemented yet");
    }

    private class ErrorThread extends Thread {

        @Override
        public void run() {
            try {
                Thread.sleep(5000);
                if (!interrupted) {
                    Platform.runLater(() -> {
                        errorLabel.setText(null);
                    });
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        private boolean interrupted;
        private void interruptMe() {
            interrupted = true;
        }
    }

}
