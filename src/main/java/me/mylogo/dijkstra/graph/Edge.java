package me.mylogo.dijkstra.graph;

import java.util.Objects;

/**
 * Created by Dennis Heckmann on 15.05.18
 * Copyright (c) 2018 Dennis Heckmann
 * GitHub: https://github.com/Mylogo
 * Web: http://mylogo.me
 * Mail: contact@mylogo.me | denheckmann@googlemail.com
 * Discord: Mylogo#4884 | Skype: Mylogo55
 */
public class Edge<E> {

    private final E firstKnot;
    private final E secondKnot;
    private double weight;

    public Edge(E firstKnot, E secondKnot, double weight) {
        this.firstKnot = firstKnot;
        this.secondKnot = secondKnot;
        this.weight = weight;
    }

    public E getFirstKnot() {
        return firstKnot;
    }

    public E getSecondKnot() {
        return secondKnot;
    }

    public E getOther(E e) {
        return Objects.equals(e, firstKnot) ? secondKnot : firstKnot;
    }

    public double getWeight() {
        return weight;
    }

    public boolean contains(E e) {
        return firstKnot.equals(e) || secondKnot.equals(e);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Edge)) return false;
        Edge edge = (Edge) o;
        return Double.compare(edge.weight, weight) == 0 &&
                ((Objects.equals(firstKnot, edge.firstKnot) && Objects.equals(secondKnot, edge.secondKnot)) ||
                  Objects.equals(firstKnot, edge.secondKnot) && Objects.equals(secondKnot, edge.firstKnot));
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstKnot, secondKnot, weight);
    }

    @Override
    public String toString() {
        return "Edge{" +
                "firstKnot=" + firstKnot +
                ", secondKnot=" + secondKnot +
                ", weight=" + weight +
                '}';
    }
}
