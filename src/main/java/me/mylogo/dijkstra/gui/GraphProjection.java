package me.mylogo.dijkstra.gui;

import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import me.mylogo.dijkstra.graph.Dijkstra;
import me.mylogo.dijkstra.graph.Graph;
import me.mylogo.dijkstra.point.Point;

import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Created by Dennis Heckmann on 20.05.18
 * Copyright (c) 2018 Dennis Heckmann
 * GitHub: https://github.com/Mylogo
 * Web: http://mylogo.me
 * Mail: contact@mylogo.me | denheckmann@googlemail.com
 * Discord: Mylogo#4884 | Skype: Mylogo55
 */
public class GraphProjection {

    private CreateGraph createGraph;
    private Canvas canvas;

    public GraphProjection(CreateGraph createGraph, Canvas canvas, Graph<Point> points) {
        this.createGraph = createGraph;
        this.canvas = canvas;
    }

    private double minX, maxX, minY, maxY, width, height;

    private void calculateProperSizing() {
        Graph<Point> graph = createGraph.getGraph();
        Set<Point> knots = graph.getKnots();
        width = canvas.getWidth();
        height = canvas.getHeight();
        if (knots.size() > 0) {
            minX = knots.stream().min((p1, p2) -> (int) (p1.getX() - p2.getX())).get().getX();
            maxX = knots.stream().max((p1, p2) -> (int) (p1.getX() - p2.getX())).get().getX();
            minY = knots.stream().min((p1, p2) -> (int) (p1.getY() - p2.getY())).get().getY();
            maxY = knots.stream().max((p1, p2) -> (int) (p1.getY() - p2.getY())).get().getY();
            maxX = Math.max(Math.abs(minX), maxX);
            maxY = Math.max(Math.abs(minY), maxY);
            // We want a square. So maxX = maxY
            // We choose the lower value
            if (maxY > maxX) {
                maxX = maxY;
            } else {
                maxY = maxX;
            }
            if (maxX == 0) {
                maxX = 0.001;
                maxY = 0.001;
            }
        }
    }

    private boolean painting;

    public void paintCanvas() {
        if (painting)
            return;
        else
            painting = true;
        new Thread(() -> {
            while (running) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Platform.runLater(() -> {
                    Graph<Point> graph = createGraph.getGraph();
                    Set<Point> knots = graph.getKnots();
                    GraphicsContext g2 = canvas.getGraphicsContext2D();
                    g2.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
                    if (createGraph.showAxes()) {
                        // draw axes
                        g2.setStroke(new Color(255d / 255d, 203d / 255d, 107d / 255d, 1));
                        g2.strokeLine(0, canvas.getHeight() / 2, canvas.getWidth(), canvas.getHeight() / 2);
                        g2.strokeLine(canvas.getWidth() / 2, 0, canvas.getWidth() / 2, canvas.getHeight());
                    }
                    if (knots.size() > 0) {
                        calculateProperSizing();
                        if (createGraph.showAllEdges()) {
                            g2.setStroke(new Color(1, 1, 1, 0.2));
                            for (Point knot : knots) {
                                double fromX = projectX(knot.getX());
                                double fromY = projectY(knot.getY());
                                knot.getConnected().stream().map(id -> graph.getKnots().stream().filter(p -> p.getId().equals(id)).findFirst().orElse(null)).filter(Objects::nonNull).forEach(found -> {
                                    double toX = projectX(found.getX());
                                    double toY = projectY(found.getY());
                                    g2.strokeLine(fromX, fromY, toX, toY);
                                    if (createGraph.showWeights()) {
                                        g2.setFill(new Color(128 / 255d, 203 / 255d, 250 / 255d, 1));
                                        double weight = knot.distance(found);
                                        double weightX = fromX + (toX - fromX) / 2;
                                        double weightY = fromY + (toY - fromY) / 2 - 10;
                                        g2.fillText(String.format("%.1f", weight), weightX, weightY);
                                    }
                                });
                            }
                        }

                        // draw connections
                        Point selected = createGraph.getSelectedPoint();
                        if (selected != null) {
                            g2.setStroke(new Color(255d / 255d, 203d / 255d, 107d / 255d, 1));
                            double fromX = projectX(selected.getX());
                            double fromY = projectY(selected.getY());
                            selected.getConnected().stream().map(id -> graph.getKnots().stream().filter(p -> p.getId().equals(id)).findFirst().orElse(null)).filter(Objects::nonNull).forEach(found -> {
                                g2.strokeLine(fromX, fromY, projectX(found.getX()), projectY(found.getY()));
                            });
                        }


                        // Draw shortest connection if available
                        Dijkstra<Point>.Connection connection = createGraph.getConnection();
                        if (connection != null) {
                            g2.setStroke(new Color(1, 80 / 255d, 50 / 255d, 1));
                            List<Point> path = connection.getPath();
                            for (int i = 0; i < path.size() - 1; i++) {
                                Point now = path.get(i);
                                Point next = path.get(i + 1);
                                g2.strokeLine(projectX(now.getX()), projectY(now.getY()), projectX(next.getX()), projectY(next.getY()));
                            }
                        }

                        // Draw the points and their name next to them
                        for (Point knot : knots) {
                            double x = projectX(knot.getX());
                            double y = projectY(knot.getY());
                            if (createGraph.getSelectedPoint() == knot) {
                                g2.setFill(new Color(130 / 255d, 170 / 255d, 1, 1));
                            } else {
                                g2.setFill(new Color(128 / 255d, 203 / 255d, 196 / 255d, 1));
                            }
                            g2.fillOval(x - 3, y - 3, 6, 6);
                            g2.fillText(knot.getId(), x + 3, y - 3);
                        }
                    }
                    g2.restore();
                });
            }
        }).start();
    }

    private boolean running = true;

    void stopProjection() {
        running = false;
    }

    public double projectX(double x) {
        double halfWidth = width / 2;
        return halfWidth + ((x / maxX) * halfWidth) * 0.95;
    }

    public double projectY(double y) {
        double halfHeight = height / 2;
        return halfHeight - ((y / maxY) * halfHeight) * 0.95;
    }

    public double unprojectX(double x) {
        double halfWidth = width / 2;
        return (x - halfWidth) / 0.95 / halfWidth * maxX;
    }

    public double unprojectY(double y) {
        double halfHeight = height / 2;
        return (halfHeight - y) / 0.95 / halfHeight * maxY;
    }

}
