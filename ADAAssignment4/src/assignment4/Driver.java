/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package assignment4;

/**
 *
 * @author alex
 */
public class Driver {
        public static void main(String[] args) {

        double[][] value_graph1 = new double[][]{
            {1.0d, 0.5d, 0.0d, 0.5d, 0.25d},
            {0.0d, 1.0d, 0.0d, 0.2d, 0.125d},
            {0.0d, 0.0d, 1.0d, 0.0d, 0.05d},
            {0.0d, 5.0d, 0.0d, 1.0d, 0.5d},
            {4d, 8.0d, 20.00d, 0.0d, 1.0d}
        };

        double[][] value_graph2 = new double[][]{
            {1.0d, 0.741d, 0.657d, 1.061d, 1.005d},
            {1.349d, 1.0d, 0.888d, 1.433d, 1.366d},
            {1.521d, 1.126d, 1.0d, 0.614d, 1.538d},
            {0.942d, 0.698d, 0.619d, 1.0d, 0.953d},
            {0.995d, 0.732d, 0.650d, 1.049d, 1.0d}
        };

        double[][] rates = new double[][]{
            {1, 0.61, 0, 1.08, 0.72},
            {1.64, 1, 0, 1.77, 1.18},
            {0, 0, 1, 0, 0.047},
            {0.92, 0.56, 0, 1, 0.67},
            {1.39, 0.85, 21.19, 1.5, 1}
        };

        String[] names = {
            "AUD",
            "EUR",
            "MXN",
            "NZD",
            "USD"
        };

        CurrencyExchangeGraph<String> graph = new CurrencyExchangeGraph<>(value_graph2, names);
        System.out.println(graph);
        System.out.println(graph.bestConversionFinder("EUR", "AUD"));
        System.out.println(graph.arbitradgeFinder("USD"));
        System.out.println("Bridge Edges: " + graph.getBridges());
    }
}
