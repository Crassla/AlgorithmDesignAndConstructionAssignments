/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package assignment4;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author alex
 */
public class ALGFromArray<E> extends AdjacencyListGraph<E> {

    protected Map<Edge<E>, Double> weights;
    protected Map<E, Integer> vfi;

    public ALGFromArray(double[][] graph, double[][] weights, E[] vertices) {
        super(GraphType.DIRECTED);
        this.weights = new HashMap<>();
        this.vfi = new HashMap<>();

        ArrayList<Vertex<E>> verticesTemp = new ArrayList<>();

        for (int i = 0; i < vertices.length; i++) {
            verticesTemp.add(i, new AdjacencyListVertex(vertices[i]));
            vfi.put(vertices[i], i);
        }

        for (int i = 0; i < graph.length; i++) {
            for (int j = 0; j < graph.length; j++) {
                if (graph[i][j] != 0 && graph[i][j] != 1) {
                    Edge<E> edge = this.addEdge(verticesTemp.get(i), verticesTemp.get(j));
                    this.weights.put(edge, weights[i][j]);
                }
            }
        }
    }

    public ALGFromArray(double[][] graph, E[] vertices) {
        super(GraphType.DIRECTED);
        this.weights = new HashMap<>();
        this.vfi = new HashMap<>();

        ArrayList<Vertex<E>> verticesTemp = new ArrayList<>();

        for (int i = 0; i < vertices.length; i++) {
            verticesTemp.add(i, new AdjacencyListVertex(vertices[i]));
            vfi.put(vertices[i], i);
        }

        for (int i = 0; i < graph.length; i++) {
            for (int j = 0; j < graph.length; j++) {
                if (graph[i][j] != 0 && i != j) {
                    Edge<E> edge = this.addEdge(verticesTemp.get(i), verticesTemp.get(j));
                    this.weights.put(edge, graph[i][j]);
                }
            }
        }
    }

    public Vertex<E> getVertex(E element) {
        return new AdjacencyListVertex(element);
    }

    public Vertex<E> duplicateVertex(Vertex<E> vertex) {
        return new AdjacencyListVertex(vertex.getUserObject());
    }

    public Map<Edge<E>, Double> getWeights() {
        return this.weights;
    }

    @Override
    public String toString() {
        String output = "Graph:\n";

        DecimalFormat df = new DecimalFormat("#0.000");

        for (Vertex<E> vertex : vertices) {
            output += "" + vertex + " has edges:[";
            int i = adjacencyLists.get(vertex).size();
            for (Edge<E> edge : adjacencyLists.get(vertex)) {
                i--;
                output += edge + "[" + df.format(weights.get(edge)) + "]" + ((i == 0) ? "]" : ", ");
            }
            output += "\n";
        }

        return output;
    }
}
