package me.mylogo.dijkstra.point;

import me.mylogo.dijkstra.graph.Graph;

import java.io.*;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Dennis Heckmann on 19.05.18
 * Copyright (c) 2018 Dennis Heckmann
 * GitHub: https://github.com/Mylogo
 * Web: http://mylogo.me
 * Mail: contact@mylogo.me | denheckmann@googlemail.com
 * Discord: Mylogo#4884 | Skype: Mylogo55
 */
public class PointGraphParser {

    public Graph<Point> parseFile(InputStream input) {
        Graph<Point> graph = new Graph<>();
        BufferedReader br = new BufferedReader(new InputStreamReader(input));
        Set<Point> points = br.lines().map(this::parseLine).collect(Collectors.toSet());
        // inserting edges
        insertKnotsIntoGraph(graph, points);
        return graph;
    }

    private Point parseLine(String line) {
        String[] parts = line.split(",");
        if (parts.length >= 3) {
            String id = parts[0];
            if (id.length() == 0)
                throw new IllegalArgumentException("ID cannot be empty");
            double x = parseDouble(parts[1]);
            double y = parseDouble(parts[2]);
            Point point = new Point(id, x, y);
            for (int i = 3; i < parts.length; i++) {
                point.addConnection(parts[i]);
            }
            return point;
        } else {
            throw new IllegalArgumentException("Entered line must have 3 or more entries.");
        }
    }

    private double parseDouble(String potentialNumber) {
        try {
            return Double.parseDouble(potentialNumber);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Given parameter '" + potentialNumber + "' is not a number.");
        }
    }

    public static Graph<Point> insertKnotsIntoGraph(Graph<Point> graph, Set<Point> points) {
        for (Point point : points) {
            graph.addKnot(point);
            for (String connectedTo : point.getConnected()) {
                points.stream()
                        .filter(p -> p.getId().equals(connectedTo))
                        .findFirst()
                        .ifPresent(foundPoint -> {
                            graph.connect(point, foundPoint, point.distance(foundPoint));
                            foundPoint.addConnection(point.getId());
                        });
            }
        }
        return graph;
    }

    public static Graph<Point> insertKnotsIntoGraphOld(Graph<Point> graph, Set<Point> points) {
        for (Point point : points) {
            graph.addKnot(point);
            for (String connectedTo : point.getConnected()) {
                for (Point otherPoint : points) {
                    if (otherPoint.getId().equals(connectedTo)) {
                        graph.connect(point, otherPoint, point.distance(otherPoint));
                        otherPoint.addConnection(point.getId());
                    }
                }
            }
        }
        return graph;
    }

}
