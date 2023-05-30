/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package assignment4;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.*;

/**
 * @author alex
 */
public class BellmanFord<E> {

    private final ALGFromArray<E> graph;
    private final double[][] ini_weights;
    private final double[][] ini_graph;
    protected final E[] vertices;

    private final double INFINITY = Integer.MAX_VALUE;

    public BellmanFord(double[][] graph, E[] vertices) {
        this.ini_graph = graph;
        this.ini_weights = this.createWeightTable(graph);
        this.graph = new ALGFromArray<>(graph, ini_weights, vertices);
        this.vertices = vertices;
    }

    //pasess through two elements to search for and a list to return things to
    //returns negative infinity if arbitradge, positive infinity if unvalid inputs
    //anything else is the weight found. 
    public double getShortestPathsTree(E element, E endElement, List<CurrencyEdge> spt) {
        if (!checkInputs(element, endElement)) {
            return Double.POSITIVE_INFINITY;
        }
        //Vertex<E> source = null;
        Vertex<E> endSource = null;
        Map<Vertex<E>, Edge<E>> leastEdges = new HashMap<>();
        final Map<Vertex<E>, Double> d = new HashMap<>();

        for (Vertex<E> vertex : graph.vertexSet()) {
            leastEdges.put(vertex, null);
            if (vertex.getUserObject() != element) {
                d.put(vertex, INFINITY);
                if (vertex.getUserObject() == endElement) {
                    endSource = vertex;
                }
            } else {
                d.put(vertex, 0.0d);
                //source = vertex;
            }
        }

        int n = graph.getVertices();

        for (int i = 1; i < (n - 1); i++) {
            for (Edge<E> edge : graph.edgeSet()) {
                Vertex<E> u = edge.endVertices()[0];
                Vertex<E> v = edge.endVertices()[1];

                if (d.get(u) < INFINITY) {
                    if (d.get(u) + graph.weights.get(edge) < d.get(v)) {
                        double newV = d.get(u) + graph.weights.get(edge);
                        d.replace(v, newV);
                        leastEdges.put(v, edge);
                    }
                }
            }
        }

        if (endSource == null) {
            return Double.POSITIVE_INFINITY;
        }

        Vertex<E> temp = endSource;
        Stack<Vertex<E>> stack = new Stack<>();
        Set<Vertex<E>> tempTracker = new HashSet<>();
        boolean infinLoop = false;
        while (temp.getUserObject() != element && !infinLoop) {
            tempTracker.add(temp);
            stack.push(temp);
            temp = leastEdges.get(temp).endVertices()[0];
            if (tempTracker.contains(temp)) {
                infinLoop = true;
            } else {
                tempTracker.add(temp);
            }
        }

        stack.push(temp);

        if (infinLoop) {
            return Double.NEGATIVE_INFINITY;
        }

        Vertex<E> vertex1 = null;
        Vertex<E> vertex2 = null;
        while (!stack.empty()) {
            if (vertex1 != null) {
                vertex2 = graph.duplicateVertex(vertex1);
            }
            vertex1 = graph.duplicateVertex(stack.pop());
            if (vertex2 != null) {
                int temp1 = graph.vfi.get(vertex1.getUserObject());
                int temp2 = graph.vfi.get(vertex2.getUserObject());
                CurrencyEdge edge = new CurrencyEdge(vertex2.getUserObject() + "", vertex1.getUserObject() + "", ini_graph[temp2][temp1], ini_weights[temp2][temp1]);
                spt.add(edge);
            }
        }

        return d.get(endSource);
    }

    public List<CurrencyEdge> findArbitradge(E element) {

        List<CurrencyEdge> output = new ArrayList<>();

        if (!checkInputs(element)) {
            return null;
        }

        //Vertex<E> source = null;
        Map<Vertex<E>, Edge<E>> leastEdges = new HashMap<>();
        final Map<Vertex<E>, Double> d = new HashMap<>();

        for (Vertex<E> vertex : graph.vertexSet()) {
            leastEdges.put(vertex, null);
            if (vertex.getUserObject() != element) {
                d.put(vertex, INFINITY);
            } else {
                d.put(vertex, 0.0d);
                //source = vertex;
            }
        }

        int n = graph.getVertices();

        for (int i = 1; i < (n - 1); i++) {
            for (Edge<E> edge : graph.edgeSet()) {
                Vertex<E> u = edge.endVertices()[0];
                Vertex<E> v = edge.endVertices()[1];

                if (d.get(u) < INFINITY) {
                    if (d.get(u) + graph.weights.get(edge) < d.get(v)) {
                        double newV = d.get(u) + graph.weights.get(edge);
                        d.replace(v, newV);
                        leastEdges.put(v, edge);
                    }
                }
            }
        }

        boolean isNegative = false;
        Vertex<E> start = null;
        for (Edge<E> edge : graph.edgeSet()) {
            Vertex<E> u = edge.endVertices()[0];
            Vertex<E> v = edge.endVertices()[1];

            if (d.get(u) + graph.weights.get(edge) < d.get(v)) {
                isNegative = true;
                start = v;
                break;
            }
        }

        if (isNegative) {
            //get to start of endvertices
            for (int i = 0; i < n; i++) {
                start = leastEdges.get(start).endVertices()[0];
            }

            Stack<Vertex<E>> completeCycle = new Stack<>();
            for (Vertex<E> v = start; ; v = leastEdges.get(v).endVertices()[0]) {
                completeCycle.push(v);

                if (v == start && completeCycle.size() > 1) {
                    break;
                }
            }

            Vertex<E> vertex1 = null;
            Vertex<E> vertex2 = null;
            while (!completeCycle.empty()) {
                if (vertex1 != null) {
                    vertex2 = graph.duplicateVertex(vertex1);
                }
                vertex1 = graph.duplicateVertex(completeCycle.pop());

                if (vertex2 != null) {
                    int temp1 = graph.vfi.get(vertex1.getUserObject());
                    int temp2 = graph.vfi.get(vertex2.getUserObject());
                    CurrencyEdge edge = new CurrencyEdge(vertex2.getUserObject() + "", vertex1.getUserObject() + "", ini_graph[temp2][temp1], ini_weights[temp2][temp1]);
                    output.add(edge);
                }
            }
        }

        return output; //no arbitradge if empty
    }

    private boolean checkInputs(E input1, E input2) {
        boolean flag1 = false;
        boolean flag2 = false;
        for (E e : vertices) {
            flag1 = flag1 || input1 == e;
            flag2 = flag2 || input2 == e;
        }

        return flag1 && flag2;
    }

    private boolean checkInputs(E input1) {
        boolean flag1 = false;

        for (E e : vertices) {
            flag1 = flag1 || input1 == e;
        }

        return flag1;
    }

    private double[][] createWeightTable(double[][] graph) {
        double[][] weight = new double[graph.length][graph.length];
        int v = graph.length;

        for (int i = 0; i < v; i++) {
            for (int j = 0; j < v; j++) {
                if (i != j && graph[i][j] != 0) {
                    weight[i][j] = Math.log(1 / graph[i][j]);
                } else {
                    weight[i][j] = 0;
                }
            }
        }

        return weight;
    }

    @Override
    public String toString() {
        return this.graph.toString();
    }
}
