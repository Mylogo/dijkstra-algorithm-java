package me.mylogo.dijkstra.point;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Dennis Heckmann on 19.05.18
 * Copyright (c) 2018 Dennis Heckmann
 * GitHub: https://github.com/Mylogo
 * Web: http://mylogo.me
 * Mail: contact@mylogo.me | denheckmann@googlemail.com
 * Discord: Mylogo#4884 | Skype: Mylogo55
 */
public class Point {

    private String id;
    private double x;
    private double y;
    private Set<String> connected;

    public Point(String id, double x, double y) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.connected = new HashSet<>();
    }

    public void addConnection(String connection) {
        this.connected.add(connection);
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Set<String> getConnected() {
        return connected;
    }

    public double distanceSquared(double x, double y) {
        return Math.pow(this.x - x, 2) + Math.pow(this.y - y, 2);
    }

    public double distanceSquared(Point point) {
        return distanceSquared(point.x, point.y);
    }

    public double distance(Point point) {
        return Math.sqrt(distanceSquared(point));
    }

    @Override
    public String toString() {
        return "Point{" +
                "id='" + id + '\'' +
                ", x=" + x +
                ", y=" + y +
                ", connected=" + connected +
                '}';
    }

    public void setConnections(Set<String> connections) {
        this.connected = connections;
    }

    public void removeConnection(String id) {
        connected.remove(id);
    }
}
