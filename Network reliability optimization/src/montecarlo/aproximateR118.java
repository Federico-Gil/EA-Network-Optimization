package montecarlo;

import java.time.Duration;
import java.time.Instant;
import java.util.Set;

import grafo.Grafo;
import grafo.Edge;

public class aproximateR118 {
	public static void main(String[] args) {
		Instant start = Instant.now();
		
		int nVertices = 118;
		
		Set<Edge> edgesSet = readEdges("C:\\Users\\Fede\\Desktop\\AE\\EA-Network-Optimization\\Network reliability optimization\\data\\118-nodes.csv");
		
		//create the graph
		Grafo graph = new Grafo(nVertices, edgesSet);
		
		/////////////

		/* eps = 0.001, delta = 0.01 */
		double[] results = graph.monteCarlo((int) 2649158, (float) 0.01);

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
				edges.add(new Edge(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Float.parseFloat(parts[2]),true));
			}
			scanner.close();
		} catch (java.io.FileNotFoundException e) {
			e.printStackTrace();
		}
		return edges;
	}
}

