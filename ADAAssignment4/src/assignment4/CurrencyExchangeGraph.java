/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package assignment4;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author alex
 */
public class CurrencyExchangeGraph<E> {

    //graph is the original graph
    protected double[][] graph;

    protected BellmanFord<E> bellman;
    
    protected BridgeGraph<E> bridgeGraph;
        

    //relates curriencie name to integer in the graph for easy referencing
    protected Map<E, Integer> curriencies;

    public CurrencyExchangeGraph(double[][] graph, E[] currenciesArr) {
        this.graph = graph;
        this.curriencies = new HashMap<>();

        for (int i = 0; i < graph.length; i++) {
            this.curriencies.put(currenciesArr[i], i);
        }

        this.bellman = new BellmanFord<>(graph, currenciesArr);
        
        bridgeGraph = new BridgeGraph<>(graph, currenciesArr);
        
        bridgeGraph.findBridges();
    }

    public String bestConversionFinder(E element, E endElement) {

        List<CurrencyEdge> spt = new ArrayList<>();
        String output = "Finding best path between: " + element + "-" + endElement + "\n";
        DecimalFormat df = new DecimalFormat("##.###");

        double weight = bellman.getShortestPathsTree(element, endElement, spt);

        if (weight == Double.NEGATIVE_INFINITY) {
            output += ("Path could not be found due to a direct arbitradge in the system");
        } else if (weight == Double.POSITIVE_INFINITY) {
            output += (element + "-" + endElement + " is not a possible path / not vertices present in this graph");
        } else {
            double per = 0;
            for (CurrencyEdge edge : spt) {
                per = per == 0 ? edge.exchangePer : per * edge.exchangePer;
            }
            output += "Best exchange rate between " + element + "-" + endElement + " is: " + df.format(per);
            output += "\nFrom the path: " + spt;
        }

        output += "\n";

        return output;
    }

    public String arbitradgeFinder(E element) {
        String output = "";

        ArrayList<CurrencyEdge> spt = new ArrayList<>();
        DecimalFormat df = new DecimalFormat("#0.#######");

        int count = 0;
        double percentage = 0;
        List<CurrencyEdge> list = bellman.findArbitradge(element);
        output += ("Finding Arbitradge for: " + element + "\n");
        if (list.isEmpty()) {
            output += ("No Arbitradge Found\n");
        } else {
            output += ("Path: ");
            String temp = null;
            for (CurrencyEdge e : list) {
                if (temp != null) {
                    if (temp.equals(e.element1)) {
                        output += (e.element2 + ((count == list.size() - 1) ? "" : "-"));
                    } else {
                        output += (e.printConnection() + ((count == list.size() - 1) ? "" : "-"));
                    }
                } else {
                    output += (e.printConnection() + ((count == list.size() - 1) ? "" : "-"));
                }
                temp = e.element2;
                if (percentage == 0) {
                    percentage = e.exchangePer;
                } else {
                    percentage *= e.exchangePer;
                }
                count++;
            }
            output += (" Exchanges to: " + df.format(percentage));
            output += ("\n");
        }

        return output;
    }
    
    public String getBridges() {
        return bridgeGraph.bridges.toString();
    }

    //nice print method to print the graph inputted
    @Override
    public String toString() {
        String output = "Graph: \n";
        String[] set = new String[graph.length];

        DecimalFormat df = new DecimalFormat("##.###");

        int k = 0;
        for (E s : bellman.vertices) {
            set[k] = s + "";
            k++;
            output += "\t" + s;
        }

        output += "\n";

        for (int i = 0; i < graph.length; i++) {
            output += set[i];
            for (int j = 0; j < graph.length; j++) {
                output += "\t" + df.format(graph[i][j]);
            }
            output += "\n";
        }

        return output;
    }
}
