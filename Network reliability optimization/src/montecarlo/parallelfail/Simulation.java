/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package montecarlo.parallelfail;

import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.Set;


import grafo.Grafo;
import grafo.Edge;

/**
 *
 * @author LAPTOP
 */
public class Simulation {
    
    public Simulation(int cantHilos,int cantI) {
        this.cantHilos = cantHilos;
        cantIter = cantI;
        hilos = new LinkedList<>();
        threadPool = Executors.newFixedThreadPool(cantHilos);
        iniciarProcesos();
    }
    
    public void iniciarProcesos(){
        Random rnd = new Random();
        //create the graph and add the edges
        for(int i = 0; i < cantHilos; i++)
            hilos.add(new SubProcess(rnd,cantIter/cantHilos, new Grafo(118, readEdges("C:\\Users\\Fede\\Desktop\\AE\\EA-Network-Optimization\\Network reliability optimization\\data\\118-nodes.csv")))); //Lo que le tocara a cada hilo
    }
    
    public void iniciarTodos(){
        for(SubProcess s : hilos){
            threadPool.execute(s);
        }
        threadPool.shutdown();    
    }
    
    public double pi(){
        iniciarTodos();
        long totalDentro = 0;long totalTotal = 0;
            while (!threadPool.isTerminated());///Barrera
            for (SubProcess t : hilos) {
                totalDentro += t.getCantidadDentro();
                totalTotal += t.getCantidadTotal();
            }
            return totalDentro / totalTotal;
    }

    public static Set<Edge> readEdges(String fileName) {
		Set<Edge> edges = new java.util.HashSet<>();
		try {
			java.util.Scanner scanner = new java.util.Scanner(new java.io.File(fileName));
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				String[] parts = line.split(",");
				edges.add(new Edge(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Float.parseFloat(parts[2])));
			}
			scanner.close();
		} catch (java.io.FileNotFoundException e) {
			e.printStackTrace();
		}
		return edges;
	}
      
    private int cantHilos = 0;
    private int cantIter = 0;
    private LinkedList<SubProcess> hilos;
    private ExecutorService threadPool;
}