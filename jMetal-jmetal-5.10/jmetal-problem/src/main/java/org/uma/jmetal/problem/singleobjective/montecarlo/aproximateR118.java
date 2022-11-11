package org.uma.jmetal.problem.singleobjective.montecarlo;

import java.time.Duration;
import java.time.Instant;
import java.util.Set;

import org.uma.jmetal.problem.singleobjective.grafo.Edge;
import org.uma.jmetal.problem.singleobjective.grafo.Grafo;

public class aproximateR118 {
	public static void main(String[] args) {
		Instant start = Instant.now();
		
		int nVertices = 118;
		
		Set<Edge> edgesSet = readEdges("C:\\Users\\Fede\\Desktop\\AE\\EA-Network-Optimization\\data\\118-nodes.csv");
		java.util.List<Edge> possibleEdges = readPossibleEdges("C:\\Users\\Fede\\Desktop\\AE\\EA-Network-Optimization\\data\\costosPosibles118.csv");
		
		
		//create the graph
		Grafo graph = new Grafo(nVertices, edgesSet,possibleEdges);
		
		/////////////

		/* eps = 0.001, delta = 0.01 n= 2649158 */
		double[] results = graph.monteCarlo((int) 1e5, (float) 0.01);

		Instant end = Instant.now();
		/////////////
		
		System.out.println("The probability of the graph being connected is: " + results[0]);
		System.out.println("The lower bound of the confidence interval is: " + results[1]);
		System.out.println("The upper bound of the confidence interval is: " + results[2]);


		System.out.println("The program took " + Duration.between(start, end).toMillis() + " milliseconds to run.");
		long timeElapsed = Duration.between(start, Instant.now()).toMillis();
		
		System.out.println("Time Elapsed:  " + timeElapsed);

	}

	//function to read the edges from csv file, the file contains the edges of the graph and the probability of each edge.
	// each line looks like this: 1, 5, 0.05
	// where 1 and 5 are the vertices of the edge and 0.05 is the probability of the edge
	//using java scanner to read the file
	public static Set<Edge> readEdges(String fileName) {
		Set<Edge> edges = new java.util.HashSet<>();
		try {
			java.util.Scanner scanner = new java.util.Scanner(new java.io.File(fileName));
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				String[] parts = line.split(", ");
				edges.add(new Edge(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Float.parseFloat(parts[2]),0f,true));
			}
			scanner.close();
		} catch (java.io.FileNotFoundException e) {
			e.printStackTrace();
		}
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

