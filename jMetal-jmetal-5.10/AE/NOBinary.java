package org.uma.jmetal.problem.singleobjective;

import org.uma.jmetal.problem.binaryproblem.impl.AbstractBinaryProblem;
import org.uma.jmetal.solution.binarysolution.BinarySolution;
import org.uma.jmetal.solution.binarysolution.impl.DefaultBinarySolution;
import org.uma.jmetal.util.JMetalException;

import grafo.Edge;
import grafo.Grafo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;
import java.util.Set;

/**
 * Class representing problem OneMax. The problem consist of maximizing the
 * number of '1's in a binary string.
 */
@SuppressWarnings("serial")
public class NOBinary extends AbstractBinaryProblem {
	private int numberOfNodes;
	private Set<Edge> aristasOriginales;
	private int bits ;
	private static final long PRESUPUESTO = 500000L;
	private int cantidadAristasOriginales;
	private int currentEvaluation;
	private List<List<Integer>> posibleEdges;
	private static final float COST_PER_KILOMETER = 45000;

  /** Constructor */
  public NOBinary(Integer numberOfNodes, Set<Edge> oEdges) {
	currentEvaluation = 0;
	this.numberOfNodes = numberOfNodes;

	//store a copy of the original edges and not the reference
	aristasOriginales = oEdges;

	this.cantidadAristasOriginales = oEdges.size();

    setNumberOfVariables(1);
    setNumberOfObjectives(1);
    setName("NOBinary");

    bits =  numberOfNodes*(numberOfNodes-1)/2 - cantidadAristasOriginales;

	/* the posible edges are the edges that can be added to the original graph,
	 * meaning that they are not already in the original graph */
	posibleEdges = new ArrayList<List<Integer>>();
	for (int i = 1; i <= numberOfNodes; i++) {
		for (int j = i+1; j <= numberOfNodes; j++) {
			List<Integer> edge = new ArrayList<Integer>();
			edge.add(i);
			edge.add(j);
			// if the edge is not in the original graph, add it to the list of posible edges, without using contains
			if (!aristasOriginales.contains(new Edge(i,j,0f,0f,true))) {
				posibleEdges.add(edge);
			}
		}
	}
  }


  public int getCurrentEvaluation() {
	  return currentEvaluation;
  }

  @Override
  public int getBitsFromVariable(int index) {
  	if (index != 0) {
  		throw new JMetalException("Problem NO has only a variable. Index = " + index) ;
  	}
  	return bits ;
  }

  @Override
  public List<Integer> getListOfBitsPerVariable() {
    return Arrays.asList(bits) ;
  }

  @Override
  public BinarySolution createSolution() {
    return new DefaultBinarySolution(getListOfBitsPerVariable(), getNumberOfObjectives()) ;
  }



  

  /** Evaluate() method */
  @Override
  public void evaluate(BinarySolution solution) {  
	  currentEvaluation++;
	  Grafo gt = new Grafo(numberOfNodes,aristasOriginales);
	  Set<Edge> nuevasAristas = new java.util.HashSet<>();
	  BitSet bitset = solution.getVariable(0) ;
	  
	  //for each bit in the bitset, if it is 1, add the corresponding edge to the graph else add the edge with exists = false
		for (int i = 0; i < bitset.length(); i++) {
			if (bitset.get(i)) {/*
				int x = posibleEdges.get(i).get(0);
				int y = posibleEdges.get(i).get(1);

				if (!aristasOriginales.contains(new Edge(x,y,0f,0f,true))) {
					float d = (float) gt.distance(x, y);
					System.out.println("distance: " + d);
					Edge e = new Edge(x,y,d*0.05, d*0.7,true);*/ 
					Edge e = new Edge(posibleEdges.get(i).get(0),posibleEdges.get(i).get(1),0.05f,1.0f,true);
					nuevasAristas.add(e);
					gt.addEdge(e);
				}
			}
		//}
	  
	  long costoUpdate = 0L;

	  for (Edge e : nuevasAristas) {
		  costoUpdate += e.getCost()*COST_PER_KILOMETER;
	  }

	  //dinamicaly update the number of samples for the montecarlo simulation
	  /*
	  int n = 1000;
	  if (currentEvaluation > 10000) {
		  n = 10000;
	  } */
	  
	  double R = gt.monteCarlo((int) 1e3, 0.05f)[0];
	  double fitness = Math.abs(PRESUPUESTO - costoUpdate)*(1/R);

	  //print the current fitness every 100 evaluations, asuming there are 25000 evaluations print the progress porcentage as well
	  if (currentEvaluation % 100 == 0) {
		  System.out.println("Current fitness: " + fitness + " Progress: " +((currentEvaluation/15000f)*100) + " %" + " Costo Update: " + costoUpdate);
	  }


	  solution.setObjective(0, fitness);
  }
  
  public int[] proy(int i) {
	  int x,y;
	  x = Math.floorDiv(i, numberOfNodes);	
	  y = Math.floorMod(i, numberOfNodes);
	  //System.out.println("i: " + i + " --- x: " + (x+1) + " y: " + (y+1));
	  return new int[]{x+1,y+1};
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
				edges.add(new Edge(Integer.parseInt(lineSplit[0]), Integer.parseInt(lineSplit[1]), Float.parseFloat(lineSplit[2]), Float.parseFloat(lineSplit[3]) ,true));
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


