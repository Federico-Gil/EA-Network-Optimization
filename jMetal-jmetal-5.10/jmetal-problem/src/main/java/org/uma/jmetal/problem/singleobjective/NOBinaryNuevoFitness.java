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
	private List<Edge> aristasPosibles;
	private int bits ;
	private static final Integer PRESUPUESTO = 1000000;
	private int cantidadAristasOriginales;
	private int currentEvaluation;
	private static final float COST_PER_KILOMETER = 87310;
	private static final float ALPHA = 0.75f; //12420000.0f
	private static final float BETA = 0.25f;
	private double cMax;

	private static final Integer POPULATION_SIZE = 100;

	//set of BinarySolutions to store the created individuals
	private List<BinarySolution> greedyIndividuals = new ArrayList<BinarySolution>();
	private List<BinarySolution> bestIndividuals = new ArrayList<BinarySolution>();


	//keep track of the fitness of the best solution in each generation
	private Double bestfitness;

  /** Constructor */
  public NOBinaryNuevoFitness(Integer numberOfNodes, Set<Edge> oEdges, List<Edge> possibleEdges) {
	currentEvaluation = 0;
	this.numberOfNodes = numberOfNodes;
	
	//keep track of the fitness of the best solution in each generation
	bestfitness = Double.MAX_VALUE;

	//store a copy of the original edges and not the reference
	aristasOriginales = oEdges;

	this.cantidadAristasOriginales = oEdges.size();

    setNumberOfVariables(1);
    setNumberOfObjectives(1);
    setName("NOBinaryNF");

    bits =  numberOfNodes*(numberOfNodes-1)/2 - cantidadAristasOriginales;

	aristasPosibles = possibleEdges;

	//calculate de maximum cost, it is, the sum of the cost of all the possible edges multiplied by COST_PER_KILOMETER
	cMax = 0;
	for (Edge edge : aristasPosibles) {
		cMax += edge.getCost();
	}
	cMax *= COST_PER_KILOMETER;
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
	if (Math.random() < 0.3) {
		//while the solution is contained in the set of greedy solutions, generate a new one. Compare the solutions using the comparteSolutions method
		BinarySolution solution;
		do {
			solution = greedy(PRESUPUESTO);
		} while (greedyIndividuals.contains(solution));
		greedyIndividuals.add(solution);
		return solution;
	} else {
		return new DefaultBinarySolution(getListOfBitsPerVariable(), getNumberOfObjectives()) ;
	}
  }

  public BinarySolution greedy(int budget) {
	//posible edges
	Set<Edge> edges = aristasOriginales;
	Integer nVertices = numberOfNodes;

	//create the original graph
	Grafo g = new Grafo(nVertices, edges,aristasPosibles);

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

		//find the edge between the two vertices in the possible edges
		Edge e = null;
		for (Edge edge : aristasPosibles) {
			if ((edge.getV1() == v1 && edge.getV2() == v2) || (edge.getV1() == v2 && edge.getV2() == v1)) {
				e = edge;
				break;
			}
		}

		if (e != null) {
			//add the edge to the graph
			g.addEdge(new Edge(e.getV1(), e.getV2(), e.getWeight(), e.getCost(), true));

			//update the degree of the vertices
			degree.put(v1, degree.get(v1)+1);
			degree.put(v2, degree.get(v2)+1);

			//update the budget
			budget -= e.getCost()*COST_PER_KILOMETER;
		}
		budget--; //to avoid infinite loops		
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
	for (Edge edge : aristasPosibles) {
		if (edgesResult.contains(edge)) {
			individual.getVariable(0).set(i);
		}
		i++;
	}
	return individual;
}

  /** Evaluate() method */
	//funcion de fitness
  @Override
  public void evaluate(BinarySolution solution) {  
	  currentEvaluation++;
	  Grafo gt = new Grafo(numberOfNodes,aristasOriginales,aristasPosibles);
	  Set<Edge> nuevasAristas = new java.util.HashSet<>();
	  BitSet bitset = solution.getVariable(0) ;
	  
	  //for each bit in the bitset, if it is 1, add the corresponding edge to the graph else add the edge with exists = false
		for (int i = 0; i < bitset.length(); i++) {
			if (bitset.get(i)) {
					Edge e = new Edge(aristasPosibles.get(i).getV1(),aristasPosibles.get(i).getV2(),aristasPosibles.get(i).getCost()*0.05f,aristasPosibles.get(i).getCost(),true);
					nuevasAristas.add(e);
					gt.addEdge(e);
				}
			}
	  
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
	  
	  //18444 para un delta = 0.05 y eps 0.01
	  double R = gt.monteCarlo((int) 18444, 0.05f)[0];
	  double fitness = ALPHA*costoUpdate/cMax + BETA*(1.0f/R);

	  //if the budget is exceeded, the fitness is set to 
	  if (costoUpdate > PRESUPUESTO) {
		  fitness *= 1e6;
	}
			  
		if (fitness < bestfitness) {
			bestfitness = fitness;
			Integer generationN = currentEvaluation/POPULATION_SIZE;
			System.out.println(generationN + ", " + bestfitness );
			//add the best solution to the array
			bestIndividuals.add(solution);
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

	//save the best individuals to a file interpreting the edges
	public void saveBestIndividuals(String fileName, java.util.List<Edge> aristasPosibles) {
		try {
			java.io.BufferedWriter writer = new java.io.BufferedWriter(new java.io.FileWriter(fileName));
			for (BinarySolution individual : bestIndividuals) {
				BitSet bitset = individual.getVariable(0);
				for (int i = 0; i < bitset.length(); i++) {
					if (bitset.get(i)) {
						writer.write(aristasPosibles.get(i).getV1() + ", " + aristasPosibles.get(i).getV2() + ", " + aristasPosibles.get(i).getWeight() + ", " + aristasPosibles.get(i).getCost());
						writer.newLine();
					}
				}
				writer.newLine();
			}
			writer.close();
		} catch (java.io.IOException e) {
			e.printStackTrace();
		}
	}
}