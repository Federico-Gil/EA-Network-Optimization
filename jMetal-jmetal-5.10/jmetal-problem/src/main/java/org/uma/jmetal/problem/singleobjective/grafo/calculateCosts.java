package org.uma.jmetal.problem.singleobjective.grafo;

import java.util.*;
public class calculateCosts {
    //read the edges from the file and return a set of edges, the edges are of this shape: 1,2,0.5,0.5,true
    public static java.util.Set<Edge> readEdges(String fileName) {
        //create a set of edges
        java.util.Set<Edge> edges = new java.util.HashSet<>();

        //read the file
        try {
            java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(fileName));
            String line = reader.readLine();
            while (line != null) {
                //split the line into the vertices and the probability
                String[] lineSplit = line.split(", ");
                //add the edge to the set
                edges.add(new Edge(Integer.parseInt(lineSplit[0]), Integer.parseInt(lineSplit[1]), Float.parseFloat(lineSplit[2]), Float.parseFloat(lineSplit[3]),true));
                //read the next line
                line = reader.readLine();
            }
            reader.close();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }

        //return the set of edges
        return edges;
    }

    public static void main(String[] args) {
        //read the edges from the file
        java.util.Set<Edge> edges = readEdges("C:/Users/Fede/Desktop/AE/EA-Network-Optimization/Network reliability optimization/data/24-nodesWc.csv");
        //create a graph with the edges
        Grafo g = new Grafo(24, edges);
        int numberOfNodes = 24;

        List<List<Integer>> posibleEdges;

        posibleEdges = new ArrayList<List<Integer>>();
        for (int i = 1; i <= numberOfNodes; i++) {
            for (int j = i+1; j <= numberOfNodes; j++) {
                List<Integer> edge = new ArrayList<Integer>();
                edge.add(i);
                edge.add(j);
                // if the edge is not in the original graph, add it to the list of posible edges, without using contains
                if (!edges.contains(new Edge(i,j,0f,0f,true))) {
                    posibleEdges.add(edge);
                }
            }
        }

        System.out.println("Posible edges: " + posibleEdges);

        //print the distance between the nodes 
        for (int i = 1; i <= numberOfNodes; i++) {
            for (int j = i+1; j <= numberOfNodes; j++) {
                System.out.println("Distance between " + i + " and " + j + ": " + g.distance(i, j));
            }
        }
    }
}
