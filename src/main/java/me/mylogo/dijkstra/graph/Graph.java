package me.mylogo.dijkstra.graph;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Dennis Heckmann on 15.05.18
 * Copyright (c) 2018 Dennis Heckmann
 * GitHub: https://github.com/Mylogo
 * Web: http://mylogo.me
 * Mail: contact@mylogo.me | denheckmann@googlemail.com
 * Discord: Mylogo#4884 | Skype: Mylogo55
 */
public class Graph<E> {

    private Set<E> knots;
    private Set<Edge<E>> edges;

    public Graph() {
        this.edges = new HashSet<>();
        this.knots = new HashSet<>();
    }

    public void addKnot(E knot) {
        knots.add(knot);
    }

    public void connect(E first, E second, double weight) {
        if (!knots.contains(first))
            knots.add(first);
        if (!knots.contains(second))
            knots.add(second);
        edges.add(new Edge<>(first, second, weight));
    }

    public Set<Edge<E>> getEdges(E knot) {
        return edges.stream()
                .filter(edge -> edge.contains(knot))
                .collect(Collectors.toSet());
    }

    public Edge<E> getEdge(E from, E to) {
        return getEdges(from).stream()
                .filter(edge -> edge.contains(to))
                .findFirst()
                .orElse(null);
    }
    public Set<E> getKnots() {
        return new HashSet<>(knots);
    }

    @Override
    public String toString() {
        return "Graph{" +
                "knots=" + knots +
                ", edges=" + edges +
                '}';
    }
}
