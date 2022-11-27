package org.uma.jmetal.problem.singleobjective.greedy;

import java.util.BitSet;

import java.util.List;
import java.util.Set;

import org.uma.jmetal.problem.binaryproblem.BinaryProblem;
import org.uma.jmetal.problem.singleobjective.NOBinaryNuevoFitness;
import org.uma.jmetal.problem.singleobjective.grafo.Edge;
import org.uma.jmetal.problem.singleobjective.grafo.Grafo;
import org.uma.jmetal.solution.binarysolution.BinarySolution;

public class g2 {
    private static final float COST_PER_KILOMETER = 87310;
	private static final int PRESUPUESTO = 5000000;
	private static final int CANTIDAD_DE_NODOS = 118;
	private static final String NOMBRE_ARCHIVO = "C:/Users/Fede/Desktop/AE/EA-Network-Optimization/data/118-nodesWc.csv";

    //the algorithm returns the cost and the graph

    public static void main(String[] args) {
    BinaryProblem problem;
	Set<Edge> edges = readEdges(NOMBRE_ARCHIVO);
	List<Edge> possibleEdges = readPossibleEdges("C:\\Users\\Fede\\Desktop\\AE\\EA-Network-Optimization\\data\\costosPosibles118.csv");

    problem = new NOBinaryNuevoFitness(CANTIDAD_DE_NODOS, edges, possibleEdges) ; // esto es lo que se cambia para probar el nuevo fitness

	BinarySolution solution = ((NOBinaryNuevoFitness) problem).greedy2(PRESUPUESTO); 
	//print the solution
	System.out.println("Solution: " + solution.getVariable(0));
	validate(solution, (NOBinaryNuevoFitness) problem);

  }
  
  private static void validate(BinarySolution solution, NOBinaryNuevoFitness problem) {
	  Grafo grafo; 
	  Set<Edge> asd = readEdges("C:/Users/Fede/Desktop/AE/EA-Network-Optimization/data/118-nodesWc.csv");
	  List<Edge> possibleEdges = readPossibleEdges("C:\\Users\\Fede\\Desktop\\AE\\EA-Network-Optimization\\data\\costosPosibles118.csv");
	  grafo = new Grafo(118, asd, possibleEdges);

	  problem.saveBestIndividuals("C:\\Users\\Fede\\Desktop\\AE\\EA-Network-Optimization\\data\\bestIndividuals.csv", possibleEdges);
	  
	  Grafo gt = grafo.copy();
	  
	  //aristas del grafo original
	  Set<Edge> nuevasAristas = new java.util.HashSet<>();
	  BitSet bitset = solution.getVariable(0) ;
	  

	  //for each bit in the bitset, if it is 1, add the corresponding edge to the graph else add the edge with exists = false
	  for (int i = 0; i < bitset.length(); i++) {
		if (bitset.get(i)) {
				Edge e = new Edge(possibleEdges.get(i).getV1(),possibleEdges.get(i).getV2(),possibleEdges.get(i).getCost()*0.05f,possibleEdges.get(i).getCost(),true);
				nuevasAristas.add(e);
				gt.addEdge(e);
			}
		}
		
	  long costoOriginal = 0L;
	  long costoNuevo = 0L;
	  
	  for (Edge e : grafo.getEdges()) {
		  costoOriginal += e.getCost()*COST_PER_KILOMETER;
	  }

	  for (Edge e : gt.getEdges()) {
		  costoNuevo += e.getCost()*COST_PER_KILOMETER;
	  }	  

	  double[] R = gt.monteCarlo((int) 2649158,0.01f);

	  //print the monte carlo results
	  System.out.println("Confiabilidad: " + R[0]);
	  System.out.println("IDC: " + R[1] + ", " + R[2]);	  

	  //print the porcentage of the money that is going to be used
	  System.out.println((costoNuevo-costoOriginal)*100.0/PRESUPUESTO + ","+R[0]);
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
