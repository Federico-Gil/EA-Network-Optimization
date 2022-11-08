package org.uma.jmetal.problem.singleobjective;

import org.uma.jmetal.problem.binaryproblem.impl.AbstractBinaryProblem;
import org.uma.jmetal.solution.binarysolution.BinarySolution;
import org.uma.jmetal.solution.binarysolution.impl.DefaultBinarySolution;
import org.uma.jmetal.util.JMetalException;

import org.uma.jmetal.problem.singleobjective.grafo.Edge;
import org.uma.jmetal.problem.singleobjective.grafo.Grafo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Class representing problem OneMax. The problem consist of maximizing the
 * number of '1's in a binary string.
 */
@SuppressWarnings("serial")
public class NOBinaryNuevoFitness extends AbstractBinaryProblem {
	private int numberOfNodes;
	private Set<Edge> aristasOriginales;
	private int bits ;
	private static final Integer PRESUPUESTO = 500000;
	private int cantidadAristasOriginales;
	private int currentEvaluation;
	private List<List<Integer>> posibleEdges;
	private static final float COST_PER_KILOMETER = 45000;
	private static final float ALPHA = 0.2f/12420000.0f;
	private static final float BETA = 0.8f;

	private static final Integer POPULATION_SIZE = 50;
	private static final Integer MAX_EVALUATIONS = 15000;

	//set of BinarySolutions to store the created individuals
	private List<BinarySolution> greedyIndividuals = new ArrayList<BinarySolution>();


	//keep track of the fitness of the best solution in each generation
	private Map<Integer,Double> bestFitnessPerGeneration;
	//keep an array of the fitness of all the solutions in the current generation
	private double[] currentFitness;

  /** Constructor */
  public NOBinaryNuevoFitness(Integer numberOfNodes, Set<Edge> oEdges) {
	currentEvaluation = 0;
	this.numberOfNodes = numberOfNodes;
	
	//keep track of the fitness of the best solution in each generation
	bestFitnessPerGeneration = new HashMap<Integer,Double>();

	//keep an array of the fitness of all the solutions in the current generation
	currentFitness = new double[POPULATION_SIZE];

	//store a copy of the original edges and not the reference
	aristasOriginales = oEdges;

	this.cantidadAristasOriginales = oEdges.size();

    setNumberOfVariables(1);
    setNumberOfObjectives(1);
    setName("NOBinaryNF");

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
	//with a 50% chance, the solution will be greedy or random
	if (Math.random() < 0.5f) {
		//while the solution is contained in the set of greedy solutions, generate a new one. Compare the solutions using the comparteSolutions method
		BinarySolution solution;
		do {
			solution = greedy(PRESUPUESTO);
		} while (greedyIndividuals.contains(solution));
		greedyIndividuals.add(solution);
		System.out.println(solution.getVariable(0));
		return solution;
	} else {
		System.out.println("random solution");
		return new DefaultBinarySolution(getListOfBitsPerVariable(), getNumberOfObjectives()) ;
	}
  }

  public BinarySolution greedy(int budget) {
	//posible edges
	Set<Edge> edges = aristasOriginales;
	Integer nVertices = numberOfNodes;
	List<List<Integer>> posibleEdges;
	posibleEdges = new ArrayList<List<Integer>>();
	for (int i = 1; i <= nVertices; i++) {
		for (int j = i+1; j <= nVertices; j++) {
			List<Integer> edge = new ArrayList<Integer>();
			edge.add(i);
			edge.add(j);
			// if the edge is not in the original graph, add it to the list of posible edges, without using contains
			if (!edges.contains(new Edge(i,j,0f,0f,true))) {
				posibleEdges.add(edge);
			}
		}
	}

	//create the original graph
	Grafo g = new Grafo(nVertices, edges);

	//update a sample of the graph
	g = g.updateSample();

	//create a map that will store the degree of each vertex
	Map<Integer, Integer> degree = new HashMap<Integer, Integer>();
	for (int i = 1; i <= nVertices; i++) {
		degree.put(i,0);
	}

	//calculate the degree of each vertex in the sampled graph
	for (Edge e : g.getEdges()) {
		if (e.getExists()){
			degree.put(e.getV1(), degree.get(e.getV1())+1);
			degree.put(e.getV2(), degree.get(e.getV2())+1);
		}
	}

	//while in budget add an edge between the two (diferent) vertices with the lowers degree
	while (budget - COST_PER_KILOMETER > 0) {
		//find the two vertices with the lowest degree
		int min1 = Integer.MAX_VALUE;
		int min2 = Integer.MAX_VALUE;
		int v1 = 0;
		int v2 = 0;
		for (int i = 1; i <= nVertices; i++) {
			if (degree.get(i) < min1) {
				min2 = min1;
				v2 = v1;
				min1 = degree.get(i);
				v1 = i;
			} else if (degree.get(i) < min2) {
				min2 = degree.get(i);
				v2 = i;
			}
		}

		//add the edge to the graph
		g.addEdge(new Edge(v1,v2,0.05f,1.0f,true));

		//update the degree of the vertices
		degree.put(v1, degree.get(v1)+1);
		degree.put(v2, degree.get(v2)+1);

		//update the budget
		budget -= COST_PER_KILOMETER;
	}

	//create the solution
	BinarySolution individual = new DefaultBinarySolution(getListOfBitsPerVariable(), getNumberOfObjectives());

	//get the edges of the resulting graph
	Set<Edge> edgesResult = g.getEdges();


	//first set all the bits to 0
	for (int i = 0; i < bits; i++) {
		individual.getVariable(0).set(i, false);
	}

	//for each new edge, set the corresponding bit to 1 in the solution where the bits are ordered by the posible edges
	int i = 0;
	for (List<Integer> edge : posibleEdges) {
		if (edgesResult.contains(new Edge(edge.get(0),edge.get(1),0f,0f,true)) || edgesResult.contains(new Edge(edge.get(1),edge.get(0),0f,0f,true))) {
			individual.getVariable(0).set(i, true);
		}
		i++;
	}
	return individual;
}

	//boolean function to compare to binary solutions and override the equals method
	public Boolean compareSolutions(BinarySolution s1, BinarySolution s2) {
			for (int j = 0; j < s1.getVariable(0).getBinarySetLength() ; j++) {
				if (s1.getVariable(0).get(j) != s2.getVariable(0).get(j)) {
					return false;
				}
			}
		return true;
	}



  /** Evaluate() method */
	//funcion de fitness
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
	  double fitness = ALPHA*costoUpdate + BETA*(1.0f/R);

	  //if the budget is exceeded, the fitness is set to 
	  if (costoUpdate > PRESUPUESTO) {
		  fitness *= 1e6;
	}

	//store the fitness to currentFitness in the position of the current evaluation modulo the population size
	currentFitness[currentEvaluation % POPULATION_SIZE] = fitness;


	  //print the current fitness every 100 evaluations, asuming there are 25000 evaluations print the progress porcentage as well
	 // if (currentEvaluation % 100 == 0) {
	//	  System.out.println("Current fitness: " + fitness + " Progress: " +((currentEvaluation/15000f)*100) + " %" + " Costo Update: " + costoUpdate);
	  //}

	  if (currentEvaluation % POPULATION_SIZE == 0) {
		  //calculate the average fitness of the population
/*
		  double averageFitness = 0;
		  for (int i = 0; i < POPULATION_SIZE; i++) {
			  averageFitness += currentFitness[i];
		  }
		  averageFitness /= POPULATION_SIZE;
*/

		  //calculate the best fitness of the population
		  
		  double bestFitness = Double.MAX_VALUE;
		  for (int i = 0; i < POPULATION_SIZE; i++) {
			  if (currentFitness[i] < bestFitness) {
				  bestFitness = currentFitness[i];
			  }
		  }

		  //print the average fitness of the population
		  //System.out.println("Average fitness: " + averageFitness);
		  System.out.println(bestFitness + ",");
		  //add the value to the map
		  bestFitnessPerGeneration.put(currentEvaluation/POPULATION_SIZE, bestFitness);
	  }


	  solution.setObjective(0, fitness);
  }

  //a getter for the map bestFitnessPerGeneration
  public Map<Integer, Double> getBestFitnessPerGeneration() {
	  return bestFitnessPerGeneration;
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

