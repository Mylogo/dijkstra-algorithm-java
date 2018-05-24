package me.mylogo.dijkstra.point;

import me.mylogo.dijkstra.graph.Graph;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Dennis Heckmann on 21.05.18
 * Copyright (c) 2018 Dennis Heckmann
 * GitHub: https://github.com/Mylogo
 * Web: http://mylogo.me
 * Mail: contact@mylogo.me | denheckmann@googlemail.com
 * Discord: Mylogo#4884 | Skype: Mylogo55
 */
public class PointGraphWriter {

    public void writeFile(File file, Graph<Point> graph) throws Exception {
        BufferedWriter bw = new BufferedWriter(new FileWriter(file));
        Set<Point> knots = graph.getKnots();
        for (String line : knots.stream().map(this::serializePoint).collect(Collectors.toSet())) {
            bw.write(line);
            bw.newLine();
        }
        bw.flush();
    }

    private String serializePoint(Point point) {
        String serializedConnections = point.getConnected().stream().collect(Collectors.joining(","));
        return point.getId() + "," + point.getX() + "," + point.getY() + (serializedConnections.length() > 0 ? "," + serializedConnections : "");
    }


}
