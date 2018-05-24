package me.mylogo.dijkstra.graph;

import java.util.*;

/**
 * Created by Dennis Heckmann on 15.05.18
 * Copyright (c) 2018 Dennis Heckmann
 * GitHub: https://github.com/Mylogo
 * Web: http://mylogo.me
 * Mail: contact@mylogo.me | denheckmann@googlemail.com
 * Discord: Mylogo#4884 | Skype: Mylogo55
 */
public class Dijkstra<E> {

    private Graph<E> graph;

    public Dijkstra(Graph<E> graph) {
        this.graph = graph;
    }


    // ---------------------------------------------------------
    // Actual beginning of implementation of Dijkstra
    // ---------------------------------------------------------


    // Knots we need to visit
    private Set<E> toVisit;
    // Key: Knot | Value: Distance
    private Map<E, Double> distances;
    // Key: Knot we're going to | Value: previous knot
    private Map<E, E> before;

    public List<E> dijkstra(E start, E to) {
        // Initialization of attributes
        before = new HashMap<>();
        distances = new HashMap<>();
        toVisit = graph.getKnots();
        // If we haven't visited a knot yet, the distance to it is infinity
        toVisit.forEach(knot -> distances.put(knot, Double.MAX_VALUE));
        // Distance to starting knot is of course 0 though
        distances.put(start, 0d);

        while (!toVisit.isEmpty()) { // There's more to visit!
            E smallest = this.getKnotWithSmallestDistance();
            toVisit.remove(smallest);
            if (smallest == null) {
                // No reachable knot available -> break out
                toVisit.clear();
            }
            for (Edge<E> edge : graph.getEdges(smallest)) {
                E neighbor = edge.getOther(smallest);
                if (toVisit.contains(neighbor)) {
                    this.updateDistance(smallest, neighbor);
                }
            }
        }
        return getPath(to);
    }

    private E getKnotWithSmallestDistance() {
        E smallest = null;
        double distance = Double.MAX_VALUE;

        for (E knot : toVisit) {
            double alternativeDistance = distances.get(knot);
            if (alternativeDistance < distance) {
                smallest = knot;
                distance = alternativeDistance;
            }
        }
        return smallest;
    }

    private void updateDistance(E from, E to) {
        double current = distances.get(to);
        double alternativeDistance = graph.getEdge(from, to).getWeight() + distances.get(from);
        if (alternativeDistance < current) {
            distances.put(to, alternativeDistance);
            before.put(to, from);
        }
    }

    private List<E> getPath(E to) {
        List<E> path = new ArrayList<>();
        path.add(to);
        while (before.get(to) != null) {
            path.add(0, to = before.get(to));
        }
        return path;
    }


    // ---------------------------------------------------------
    // End of implementation of Dijkstra
    // ---------------------------------------------------------


    // Ignore this - more or less unimportant
    public Connection calculate(E from, E to) {
        List<E> path = dijkstra(from, to);
        Connection con = new Connection(from, to);
        con.path = path;
        return con;
    }


    // ---------------------------------------------------------
    // Another algorithm for finding the shortest path
    // ---------------------------------------------------------


    private Connection currentShortestConnection;

    /**
     * @param traveller  Recursive walker
     * @param to         the knot we want to reach. Always stays the same
     * @param connection Describes the path the traveller walked to far
     */
    private void calculate(E traveller, E to, Connection connection) {
        if (connection == null) {
            connection = new Connection(traveller, to);
        }
        Set<Edge<E>> edgesOfTraveller = graph.getEdges(traveller);
        for (Edge<E> edge : edgesOfTraveller) {
            E next = edge.getOther(traveller); // because it's an undirected graph
            if (!connection.contains(next)) { // needed for not going back

                // our traveller walked to the next knot
                Connection newConnection = connection.clone();
                newConnection.add(next, edge.getWeight());

                if (to.equals(next)) {
                    if (currentShortestConnection == null) {
                        // no path found for now, let's keep this one
                        currentShortestConnection = newConnection;
                    } else {
                        if (currentShortestConnection.weight > newConnection.weight) {
                            // we found a better path!
                            currentShortestConnection = newConnection;
                        }
                    }
                } else {
                    // recursion: next knot needs to visit his own neighbored knots
                    calculate(next, to, newConnection);
                }
            }
        }
    }

    public class Connection {
        private E from;
        private E to;
        private double weight;
        private List<E> path;

        private Connection(E from, E to) {
            from = from;
            to = to;
            path = new ArrayList<>();
            path.add(from);
        }

        private void add(E e, double weight) {
            path.add(e);
            weight += weight;
        }

        private boolean contains(E e) {
            return path.contains(e);
        }

        public Connection clone() {
            Connection con = new Connection(from, to);
            con.weight = weight;
            con.path = new ArrayList<>();
            con.path.addAll(path);
            return con;
        }

        public List<E> getPath() {
            return path;
        }

    }

}
