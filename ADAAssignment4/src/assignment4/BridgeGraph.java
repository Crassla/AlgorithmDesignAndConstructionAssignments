package assignment4;

import java.util.*;

public class BridgeGraph<E> extends AdjacencyListGraph<E>{

    public HashSet<Edge<E>> bridges = new HashSet<>();
    private int counter = 0;

    public BridgeGraph(double[][] graph, E[] vertices) {
        super(GraphType.UNDIRECTED);

        ArrayList<Vertex<E>> verticesTemp = new ArrayList<>();

        for (int i = 0; i < vertices.length; i++) {
            verticesTemp.add(i, new AdjacencyListVertex(vertices[i]));
        }

        for (int i = 0; i < graph.length; i++) {
            for (int j = 0; j < graph.length; j++) {
                if (graph[i][j] != 0 && graph[i][j] != 1) {
                    this.addEdge(verticesTemp.get(i), verticesTemp.get(j));
                }
            }
        }
    }

    public void findBridges() {
        // depth first search

        HashMap<Vertex<E>, Boolean> visited = new HashMap<>();
        HashMap<Vertex<E>, Integer> d = new HashMap<>();
        HashMap<Vertex<E>, Integer> m = new HashMap<>();
        HashMap<Vertex<E>, Vertex<E>> parent = new HashMap<>();

        for (Vertex<E> v : this.vertices) {
            visited.put(v, false);
            d.put(v, 0);
            m.put(v, 0);
            parent.put(v, null);
        }

        for (Vertex<E> v : this.vertices) {
            if (!visited.get(v)) {
                DFS(v, visited, d, m, parent);
            }
        }

    }

    private void DFS(Vertex<E> u, HashMap<Vertex<E>, Boolean> visited, HashMap<Vertex<E>, Integer> d, HashMap<Vertex<E>, Integer> m, HashMap<Vertex<E>, Vertex<E>> parent) {
        visited.put(u, true);
        d.put(u, counter);
        m.put(u, counter);
        counter++;

        for (Edge<E> eEdge : this.adjacencyLists.get(u)) {
            Vertex<E> v = eEdge.oppositeVertex(u);

            if (!visited.get(v)) {
                parent.put(v, u);
                DFS(v, visited, d, m, parent);
                m.put(u, Math.min(m.get(u), m.get(v)));

                if (m.get(v) > d.get(u)) {
                    bridges.add(eEdge);
                }
            } else if (v != parent.get(u)) {
                m.put(u, Math.min(m.get(u), d.get(v)));
            }
        }
    }



}
