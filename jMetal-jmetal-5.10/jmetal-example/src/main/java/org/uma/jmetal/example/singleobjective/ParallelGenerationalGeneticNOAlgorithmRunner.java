package org.uma.jmetal.example.singleobjective;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.singleobjective.geneticalgorithm.GeneticAlgorithmBuilder;
import org.uma.jmetal.example.AlgorithmRunner;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.crossover.impl.SinglePointCrossover;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.BitFlipMutation;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.operator.selection.impl.BinaryTournamentSelection;
import org.uma.jmetal.problem.binaryproblem.BinaryProblem;
import org.uma.jmetal.problem.singleobjective.NOBinary;
import org.uma.jmetal.solution.binarysolution.BinarySolution;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.evaluator.impl.MultithreadedSolutionListEvaluator;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;

import org.uma.jmetal.problem.singleobjective.grafo.Edge;
import org.uma.jmetal.problem.singleobjective.grafo.Grafo;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Set;

/**
 * Class to configure and run a parallel (multithreaded) generational genetic algorithm. The number
 * of cores is specified as an optional parameter. A default value is used is the parameter is not
 * provided. The target problem is OneMax
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class ParallelGenerationalGeneticNOAlgorithmRunner {
  private static final int DEFAULT_NUMBER_OF_CORES = 6;
  /**
   * Usage: java org.uma.jmetal.runner.singleobjective.ParallelGenerationalGeneticAlgorithmRunner
   * [cores]
   */
  public static void main(String[] args) throws Exception {
    Algorithm<BinarySolution> algorithm;
    BinaryProblem problem;
    
    problem = new NOBinary(24,readEdges("C:/Users/Fede/Desktop/AE/EA-Network-Optimization/Network reliability optimization/data/24-nodes.csv"));

    int numberOfCores;
    if (args.length == 1) {
      numberOfCores = Integer.valueOf(args[0]);
    } else {
      numberOfCores = DEFAULT_NUMBER_OF_CORES;
    }

    CrossoverOperator<BinarySolution> crossoverOperator = new SinglePointCrossover(0.9);
    MutationOperator<BinarySolution> mutationOperator =
        new BitFlipMutation(1.0 / problem.getBitsFromVariable(0));
    SelectionOperator<List<BinarySolution>, BinarySolution> selectionOperator =
        new BinaryTournamentSelection<BinarySolution>();

    GeneticAlgorithmBuilder<BinarySolution> builder =
        new GeneticAlgorithmBuilder<BinarySolution>(problem, crossoverOperator, mutationOperator)
            .setPopulationSize(100)
            .setMaxEvaluations(1500)
            .setSelectionOperator(selectionOperator)
            .setSolutionListEvaluator(
                new MultithreadedSolutionListEvaluator<BinarySolution>(numberOfCores));

    algorithm = builder.build();

    AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm).execute();

    builder.getEvaluator().shutdown();

    BinarySolution solution = algorithm.getResult();
    List<BinarySolution> population = new ArrayList<>(1);
    population.add(solution);

    long computingTime = algorithmRunner.getComputingTime();

    new SolutionListOutput(population)
        .setVarFileOutputContext(new DefaultFileOutputContext("VAR.tsv"))
        .setFunFileOutputContext(new DefaultFileOutputContext("FUN.tsv"))
        .print();

    JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");
    JMetalLogger.logger.info("Objectives values have been written to file FUN.tsv");
    JMetalLogger.logger.info("Variables values have been written to file VAR.tsv");
    
    validate(solution);
  }
  
  
  private static void validate(BinarySolution solution) {
	  Grafo grafo; 
	  Set<Edge> asd = readEdges("C:/Users/Fede/Desktop/AE/EA-Network-Optimization/Network reliability optimization/data/24-nodes.csv");
	  grafo = new Grafo(24,asd);
	  
	  Grafo gt = grafo.copy();
	  
	  //aristas del grafo original
	  Set<Edge> e1 =  grafo.getEdges();
	  Set<Edge> nuevasAristas = new java.util.HashSet<>();
	  BitSet bitset = solution.getVariable(0) ;
	  
	  //aristas del individuo
	  for(int i= 0; i < bitset.length();i++) {
		  if (bitset.get(i)) {
			  int[] p = proy(i);
			  Edge ed1 = new Edge(p[0], p[1], 0.05f,0f,true);
			  Edge ed2 = new Edge(p[1], p[0], 0.05f,0f,true);
			  if (!(e1.contains(ed1) && e1.contains(ed2)) && !(p[0] == p[1]) ) {
				gt.addEdge(ed1);
				nuevasAristas.add(ed1);
			}
		  }
	  }
	  
	  int cantAristasOriginal = grafo.getEdges().size();
	  int cantAristasNuevo = gt.getEdges().size();
	  long costoOriginal = 0L;
	  long costoNuevo = 0L;
	  
	  costoNuevo += cantAristasNuevo*450000L; 
	  costoOriginal += cantAristasOriginal*450000L;
	  
	  System.out.println("Nuevas aristas: " + (cantAristasNuevo-cantAristasOriginal) + " Estas son: " + nuevasAristas);
	  double R = gt.monteCarlo((int) 1e6,0.05f)[0];
	  long presupuesto = 5000000L;
	  System.out.println("Costo de la actualizacion: " + (costoNuevo-costoOriginal));
	  System.out.println("Porcentaje del presupuesto utilizado: " + ((((costoNuevo-costoOriginal))/presupuesto)*100) + " %");
	  System.out.print("Confiabilidad de la nueva red: " + R);
	  	  
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
				edges.add(new Edge(Integer.parseInt(lineSplit[0]), Integer.parseInt(lineSplit[1]), Float.parseFloat(lineSplit[2]),Float.parseFloat(lineSplit[2]),true));
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
  
  public static int[] proy(int i) {
	  int x,y;
	  x = Math.floorDiv(i, 24);	
	  y = Math.floorMod(i, 24);
	  //System.out.println("i: " + i + " --- x: " + (x+1) + " y: " + (y+1));
	  return new int[]{x+1,y+1};
  } 
  
}
