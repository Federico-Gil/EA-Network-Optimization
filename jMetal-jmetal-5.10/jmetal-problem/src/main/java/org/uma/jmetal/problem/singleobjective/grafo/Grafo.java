package org.uma.jmetal.problem.singleobjective.grafo;
import java.util.*;

import org.uma.jmetal.util.binarySet.BinarySet;

public class Grafo {
	private Integer nVertices;
	private Set<Edge> edges;
	private Map<Integer, Set<Integer>> adjacencyList;
	private boolean[] visited;
	private static final float COST_PER_KILOMETER = 45000;
	//posible edges
	List<Edge> possibleEdges;

	//the grafo constructor takes the number of vertices and the edges and creates the instance
	public Grafo(Integer nVertices, Set<Edge> edges, List<Edge> possibleEdges) {
		this.nVertices = nVertices;
		this.edges = new HashSet<>(edges);
		this.adjacencyList = new HashMap<>();
		this.visited = new boolean[nVertices+1];
		
		//create the adjacency list
		for (int i = 1; i <= nVertices+1; i++) {
            adjacencyList.put(i, new HashSet<>());
        }

		for (int i = 0; i <= nVertices; i++) {
			visited[i] = false;			
		}

		for (int i = 1; i <= nVertices; i++) addVertex(i);

		//add the edges to the adjacency list
		for (Edge edge : edges) {
			adjacencyList.get(edge.getV1()).add(edge.getV2());
			adjacencyList.get(edge.getV2()).add(edge.getV1());
		}

		this.possibleEdges = possibleEdges;
	}

	//get and set methods
	public Integer getnVertices() {
		return nVertices;
	}

	public Map<Integer, Set<Integer>> getAdjacencyList() {
		return adjacencyList;
	}

	//add vertex method
	public void addVertex(Integer vertex) {
		adjacencyList.putIfAbsent(vertex, new HashSet<>());
	}

	/* Get sample method, it does the following:
	 * 1. It goes through the edges and updates their existence field based on the weight of the edge
	 * 2. It clears the adjacency list
	 * 3. It updates the adjacency list based on the existence field of the edges
	 * 4. It prints if the graph is connected or not
	 */
	public Grafo updateSample() {
		//the weight of the edges determines the probability of the edge being in the adjacency list
		for (Edge edge : edges) {
			if (Math.random() > edge.getWeight()) {
				edge.setExists(true);
			} else {
				edge.setExists(false);
			}
		}

		//clear the adjacency list
		//adjacencyList.clear();

		for (int i = 1; i <= nVertices; i++) {
            adjacencyList.get(i).clear();
        }

		//update the adjacency list based on the existence field of the edges
		for (int i = 1; i <= nVertices; i++) addVertex(i);
		for (Edge edge : edges) {
			if (edge.getExists()) {
				adjacencyList.get(edge.getV1()).add(edge.getV2());
				adjacencyList.get(edge.getV2()).add(edge.getV1());
			}
		}
		return this;
	}

	//run a monte carlo simulation of the graph to find the probability of the graph being connected
	public double[] monteCarlo(Integer nSamples, float delta) {
		//the number of connected graphs
		int nConnected = 0;

		//run the monte carlo simulation
		for (int i = 0; i < nSamples; i++) {
			//get a sample
			updateSample();

			//if the sample is connected
			if (isConnected()) {
				//increment the number of connected graphs
				nConnected++;
			}
		}

		//double V = nConnected*(1-nConnected)/(nSamples-1);
		//calculate the chevyshev confidence interval
		double beta = 1/Math.sqrt(delta);
		double lower = (nConnected + Math.pow(beta, 2)/2 - beta * Math.sqrt(Math.pow(beta, 2)/4 + nConnected*(nSamples-nConnected)/nSamples)) / (nSamples+Math.pow(beta, 2));
		double upper = (nConnected + Math.pow(beta, 2)/2 + beta * Math.sqrt(Math.pow(beta, 2)/4 + nConnected*(nSamples-nConnected)/nSamples)) / (nSamples+Math.pow(beta, 2));

		//return the probability of the graph being connected and the confidence interval. Also return the number of connected graphs
		return new double[]{nConnected/(double)nSamples, lower, upper, nConnected};
	}

	//check if the graph is connected
	//dfs method
    private void dfs(int vertex) { //deberiamos hacerlo iterativo
        visited[vertex] = true;
        for (int v : adjacencyList.get(vertex)) {
            if (!visited[v]) {
                dfs(v);
            }
        }
    }
    //method to see if the graph is connected
    public boolean isConnected() {
		
		for (int i = 0; i <= nVertices; i++) {
			visited[i] = false;			
		}
		
		dfs(1);

		for (int i = 1; i < visited.length; i++) {
			if (visited[i] == false)
				return false;
		}
		return true;
	}


	//return the edges
	public Set<Edge> getEdges() {
		return edges;
	}

	//add edge method
	public void addEdge(Edge edge) {
		edges.add(edge);
	}
	
	//funtion that returns a copy of the graph
	public Grafo copy() {
		Set<Edge> edgesCopy = new HashSet<>();
		for (Edge edge : edges) {
			edgesCopy.add(edge.copy());
		}
		return new Grafo(nVertices, edgesCopy, possibleEdges);
	}


	/* The following funtion is used to obtain an initial population using a greedy algorithm
	 * the population is a BitSet, where each bit represents the existence of an edge in the graph
	 * The algorithm is as follows:
	 * 1. update sample
	 * 2. calculate the degree of each vertex
	 * 3. pick the two vertices with the lowest degree and add an edge between them
	 * 4. update the degrees of the vertices adjacent to the two vertices
	 * 5. repeat steps 3 and 4 until the budget is reached 
	 */
	public BinarySet greedy(int budget) {

		//calculate original edges size
		int originalEdgesSize = edges.size();
		
		//create a BitSet to store the population
		BinarySet population = new BinarySet(nVertices*(nVertices-1)/2 - originalEdgesSize);
		
		
		//create a set to store the edges
		Set<Edge> edges = new HashSet<>();
		
		//create a set to store the vertices
		Set<Integer> vertices = new HashSet<>();
		for (int i = 1; i <= nVertices; i++) {
			vertices.add(i);
		}
		
		//create a map to store the degree of each vertex
		Map<Integer, Integer> degree = new HashMap<>();
		for (int i = 1; i <= nVertices; i++) {
			degree.put(i, 0);
		}
		
		//update the degree of each vertex
		for (Edge edge : edges) {
			degree.put(edge.getV1(), degree.get(edge.getV1())+1);
			degree.put(edge.getV2(), degree.get(edge.getV2())+1);
		}
		
		//while the budget is not reached
		while (budget-COST_PER_KILOMETER > 0) {
			
			//get the two vertices with the lowest degree
			int min1 = -1;
			int min2 = -1;
			for (int v : vertices) {
				if (min1 == -1) min1 = v;
				else if (degree.get(v) < degree.get(min1)) {
					min2 = min1;
					min1 = v;
				}
				else if (min2 == -1) min2 = v;
				else if (degree.get(v) < degree.get(min2)) {
					min2 = v;
				}
			}
			
			//find the position of the pair of vertices in the list of posible edges
			int pos = -1;
			for (int i = 0; i < possibleEdges.size(); i++) {
				if (possibleEdges.get(i).getV1()== min1 && possibleEdges.get(i).getV2() == min2 || possibleEdges.get(i).getV1() == min2 && possibleEdges.get(i).getV2() == min1) {
					pos = i;
					break;
				}
			}

			//if the position is not -1, set the bit in the BinarySet
			if (pos != -1) {
				population.set(pos);
				//update the budget
				budget -= possibleEdges.get(pos).getCost()*COST_PER_KILOMETER;
			}
			
			//update the degree of the vertices adjacent to the two vertices
			for (int v : adjacencyList.get(min1)) {
				degree.put(v, degree.get(v)+1);
			}
			for (int v : adjacencyList.get(min2)) {
				degree.put(v, degree.get(v)+1);
			}
		}

		return population;
	}

	//funtion that returns a greedy population
	public List<BinarySet> greedyPopulation(int size, int budget) {
		List<BinarySet> population = new ArrayList<>();
		for (int i = 0; i < size; i++) {
			updateSample();
			population.add(greedy(budget));
		}
		return population;
	}

	//method of get possible edges
	public List<Edge> getPossibleEdges() {
		return possibleEdges;
	}
}
