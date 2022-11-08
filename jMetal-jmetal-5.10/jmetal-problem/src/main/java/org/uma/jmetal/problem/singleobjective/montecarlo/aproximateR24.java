package org.uma.jmetal.problem.singleobjective.montecarlo;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Set;

import org.uma.jmetal.problem.singleobjective.grafo.Edge;
import org.uma.jmetal.problem.singleobjective.grafo.Grafo;
/*
 * Corre montecarlo para estimar la probabilidad de que el grafo sea conexo
 */
public class aproximateR24 {

	public static void main(String[] args) {
		
		int nVertices = 24;
		
		//parse the edges array into a set of edges
		java.util.Set<Edge> edgesSet = readEdges("C:\\Users\\Fede\\Desktop\\AE\\EA-Network-Optimization\\data\\24-nodes.csv");
		
		Instant start = Instant.now();
		//create the graph
		Grafo graph = new Grafo(nVertices, edgesSet);
		graph = graph.updateSample();
		Map<Integer, Set<Integer>> adjList = graph.getAdjacencyList();

		//print the adjacency list
		for (int i = 1; i < adjList.size(); i++) {
			System.out.println("Node " + i + " is connected to: " + adjList.get(i));
		}
		
		double[] results = null;
		for (int i = 0; i < 10; i++) {
			results = graph.monteCarlo((int) 1e3, (float) 0.05);					
		}
		
		System.out.println("The probability of the graph being connected is: " + results[0]);
		System.out.println("The lower bound of the confidence interval is: " + results[1]);
		System.out.println("The upper bound of the confidence interval is: " + results[2]);

		Instant end = Instant.now();

		/*
		//print the edges of the graph
		System.out.println(graph.getSample().isConnected());
		*/

		System.out.println("The program took " + Duration.between(start, end).toMillis() + " milliseconds to run.");
	}

	//function to read the edges from csv file, the file contains the edges of the graph and the probability of each edge.
	// each line looks like this: 1, 5, 0.05
	// where 1 and 5 are the vertices of the edge and 0.05 is the probability of the edge
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
				edges.add(new Edge(Integer.parseInt(lineSplit[0]), Integer.parseInt(lineSplit[1]), Float.parseFloat(lineSplit[2]),0f,true));
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

