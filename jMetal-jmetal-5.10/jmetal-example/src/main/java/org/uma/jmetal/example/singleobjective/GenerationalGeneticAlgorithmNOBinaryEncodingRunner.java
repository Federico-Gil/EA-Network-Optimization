package org.uma.jmetal.example.singleobjective;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.singleobjective.geneticalgorithm.GeneticAlgorithmBuilder;
import org.uma.jmetal.example.AlgorithmRunner;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.crossover.impl.HUXCrossover;
import org.uma.jmetal.operator.crossover.impl.SinglePointCrossover;
import org.uma.jmetal.operator.crossover.impl.TwoPointCrossover;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.BitFlipMutation;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.operator.selection.impl.BinaryTournamentSelection;
import org.uma.jmetal.problem.binaryproblem.BinaryProblem;
//import org.uma.jmetal.problem.singleobjective.NOBinary;
import org.uma.jmetal.problem.singleobjective.NOBinaryNuevoFitness;
import org.uma.jmetal.solution.binarysolution.BinarySolution;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;

import org.uma.jmetal.problem.singleobjective.grafo.Edge;
import org.uma.jmetal.problem.singleobjective.grafo.Grafo;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Set;

/**
 * Class to configure and run a generational genetic algorithm. The target problem is OneMax.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class GenerationalGeneticAlgorithmNOBinaryEncodingRunner {
	private static final float COST_PER_KILOMETER = 87310;
	private static final int PRESUPUESTO = 4000000;

	private static final int MAX_EVALUATIONS = 30000;
	private static final int POPULATION_SIZE = 20;
	private static final float CROSSOVER_PROBABILITY = 0.60f;
	private static final float MUTATION_MULTIPLIER = 0.001f;

	private static final int CANTIDAD_DE_NODOS = 54;
	private static final String NOMBRE_ARCHIVO = "C:/Users/Fede/Desktop/AE/EA-Network-Optimization/data/54-nodesWc.csv";

  /**
   * Usage: java org.uma.jmetal.runner.singleobjective.GenerationalGeneticAlgorithmBinaryEncodingRunner
   */
  public static void main(String[] args) throws Exception {
    BinaryProblem problem;
    Algorithm<BinarySolution> algorithm;
    CrossoverOperator<BinarySolution> crossover;
    MutationOperator<BinarySolution> mutation;
    SelectionOperator<List<BinarySolution>, BinarySolution> selection;
	Set<Edge> edges = readEdges(NOMBRE_ARCHIVO);
	List<Edge> possibleEdges = readPossibleEdges("C:\\Users\\Fede\\Desktop\\AE\\EA-Network-Optimization\\data\\costosPosibles54.csv");

    problem = new NOBinaryNuevoFitness(CANTIDAD_DE_NODOS, edges, possibleEdges) ; // esto es lo que se cambia para probar el nuevo fitness

    //crossover = new HUXCrossover(0.95) ;
	//create a 2 point crossover
	crossover = new HUXCrossover(CROSSOVER_PROBABILITY);

    double mutationProbability = MUTATION_MULTIPLIER;
    mutation = new BitFlipMutation(mutationProbability) ;

    selection = new BinaryTournamentSelection<BinarySolution>();

    algorithm = new GeneticAlgorithmBuilder<>(problem, crossover, mutation)
            .setPopulationSize(POPULATION_SIZE)
            .setMaxEvaluations(MAX_EVALUATIONS)
            .setSelectionOperator(selection)
            .build();

    AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm)
            .execute() ;

    BinarySolution solution = algorithm.getResult() ;
    List<BinarySolution> population = new ArrayList<>(1) ;
    population.add(solution) ;

    long computingTime = algorithmRunner.getComputingTime() ;

    new SolutionListOutput(population)
            .setVarFileOutputContext(new DefaultFileOutputContext("VAR.tsv"))
            .setFunFileOutputContext(new DefaultFileOutputContext("FUN.tsv"))
            .print();

    JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");
    JMetalLogger.logger.info("Objectives values have been written to file FUN.tsv");
    JMetalLogger.logger.info("Variables values have been written to file VAR.tsv");

    JMetalLogger.logger.info("Fitness: " + solution.getObjective(0)) ;
    JMetalLogger.logger.info("Solution: " + solution.getVariable(0)) ;
    
    validate(solution, (NOBinaryNuevoFitness) problem);
  }  
  
  private static void validate(BinarySolution solution, NOBinaryNuevoFitness problem) {
	  Grafo grafo; 
	  Set<Edge> asd = readEdges("C:/Users/Fede/Desktop/AE/EA-Network-Optimization/data/54-nodesWc.csv");
	  List<Edge> possibleEdges = readPossibleEdges("C:\\Users\\Fede\\Desktop\\AE\\EA-Network-Optimization\\data\\costosPosibles54.csv");
	  grafo = new Grafo(54, asd, possibleEdges);

	  problem.saveBestIndividuals("C:\\Users\\Fede\\Desktop\\AE\\EA-Network-Optimization\\data\\bestIndividuals54.csv", possibleEdges);
	  
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

	  //System.out.println("Nuevas aristas: " + (cantAristasNuevo-cantAristasOriginal) + " Estas son: " + nuevasAristas);
	  double R = gt.monteCarlo((int) 1e6,0.05f)[0];
	  System.out.println("Costo de la actualizacion: " + (costoNuevo-costoOriginal));
	  //print the porcentage of the money that is going to be used
	  System.out.println("Porcentaje del presupuesto usado: " + (costoNuevo-costoOriginal)*100.0/PRESUPUESTO + " %");
	  System.out.println("Confiabilidad de la nueva red: " + R);

	  //print the new edges, one per line
	  System.out.println("New edges:");
	  for(Edge e : nuevasAristas) {
		  System.out.println(e);
	  }	  	  


	  //save the new edges to a csv file
	  try {
		FileWriter writer = new FileWriter("C:/Users/Fede/Desktop/AE/EA-Network-Optimization/data/newEdges54.csv");
		for(Edge e : nuevasAristas) {
			writer.append(e.toCsv());
			writer.append("\n");
		}
		writer.flush();
		writer.close();
		} catch (IOException err1) {
			err1.printStackTrace();
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
