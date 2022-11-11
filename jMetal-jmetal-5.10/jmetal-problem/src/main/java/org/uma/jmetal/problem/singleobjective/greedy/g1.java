package org.uma.jmetal.problem.singleobjective.greedy;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.uma.jmetal.problem.singleobjective.grafo.Edge;
import org.uma.jmetal.problem.singleobjective.grafo.Grafo;

public class g1 {
    //this is a greedy algorithm that does the following:
    //1. It takes the graph, and P the max cost. And calculates the degree of each vertex and stores it in a map
    //while cost < P
    //2. It takes the two vertices with the lowest degree and adds an edge between them
    //3. It updates the degree of the vertices that are connected to the two vertices
    //4. It updates the cost

    //the algorithm returns the cost and the graph

    public static void main(String[] args) {
        //first we create the graph
        Set<Edge> oEdges = readEdges("C:/Users/Fede/Desktop/AE/EA-Network-Optimization/data/24-nodes.csv");
        List<Edge> possibleEdges = readPossibleEdges("C:\\Users\\Fede\\Desktop\\AE\\EA-Network-Optimization\\data\\costosPosibles.csv");
        Grafo grafo = new Grafo(24, oEdges,possibleEdges);
        Set<Edge> newEdges = new HashSet<>();

        //then we set the max cost
        int P = 5000000;

        //we calculate the degree of each vertex
        //first we create the map
        Map<Integer, Integer> degree = new HashMap<>();

        //initialize the map
        for (int i = 1; i <= grafo.getnVertices(); i++) {
            degree.put(i, 0);
        }

        //then we calculate the degree of each vertex
        for (Edge edge : grafo.getEdges()) {
            degree.put(edge.getV1(), degree.get(edge.getV1()) + 1);
            degree.put(edge.getV2(), degree.get(edge.getV2()) + 1);
        }
        
        
        /*
        //print the degree of each vertex
        for (int i = 1; i <= grafo.getnVertices(); i++) {
            System.out.println("The degree of vertex " + i + " is " + degree.get(i));
        }*/

        
        //we calculate the cost
        int cost = 0;
        for (Edge edge : grafo.getEdges()) {
            if (edge.getExists()) {
                cost += 450000;
            }
        }

        System.out.println("The initial cost is: " + cost);

        //we start the algorithm
        while (P-450000 > 0) {
            //we find the two vertices with the lowest degree such that they are not connected
            int min1 = 1000000;
            int min2 = 1000000;
            int v1 = 0;
            int v2 = 0;
            for (int i = 1; i <= grafo.getnVertices(); i++) {
                if (degree.get(i) < min1) {
                    min1 = degree.get(i);
                    v1 = i;
                }
            }
            for (int i = 1; i <= grafo.getnVertices(); i++) {
                if (degree.get(i) < min2 && i != v1) {
                    min2 = degree.get(i);
                    v2 = i;
                }
            }

            //we add the edge between the two vertices
            newEdges.add(new Edge(v1, v2, 0.05f,0.0f,true));
            cost += 450000;
            P -= 450000;

            //we update the degree of the vertices that are connected to the two vertices
            for (Edge edge : grafo.getEdges()) {
                if (edge.getV1() == v1 || edge.getV2() == v1) {
                    degree.put(edge.getV1(), degree.get(edge.getV1()) + 1);
                    degree.put(edge.getV2(), degree.get(edge.getV2()) + 1);
                }
                if (edge.getV1() == v2 || edge.getV2() == v2) {
                    degree.put(edge.getV1(), degree.get(edge.getV1()) + 1);
                    degree.put(edge.getV2(), degree.get(edge.getV2()) + 1);
                }
            }

        }

        System.out.println("El presupuesto restante es: " + P);

        //we print the graph
        for (Edge edge : newEdges) {
            if (edge.getExists()) {
                System.out.println(edge);
            }
        }
    }

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
				edges.add(new Edge(Integer.parseInt(lineSplit[0]), Integer.parseInt(lineSplit[1]), Float.parseFloat(lineSplit[2]), 0f ,true));
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

     //method that reads the possible edges from a csv file
     public static java.util.List<Edge> readPossibleEdges(String fileName) {
        //create a list of edges
        java.util.List<Edge> edges = new java.util.ArrayList<>();

        //read the file
        try {
            java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(fileName));
            String line = reader.readLine();
            while (line != null) {
                //split the line into the vertices and the probability
                String[] lineSplit = line.split(", ");
                //add the edge to the set
                edges.add(new Edge(Integer.parseInt(lineSplit[0]), Integer.parseInt(lineSplit[1]), Float.parseFloat(lineSplit[2])*0.05f, Float.parseFloat(lineSplit[2]) ,false));
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
}
