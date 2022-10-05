package grafo;
import java.util.*;

public class Grafo {
	private Integer nVertices;
	private Set<Edge> edges;
	private Map<Integer, Set<Integer>> adjacencyList;

	//the grafo constructor takes the number of vertices and the edges and creates the instance
	public Grafo(Integer nVertices, Set<Edge> edges) {
		this.nVertices = nVertices;
		this.edges = edges;
		this.adjacencyList = new HashMap<>();
		//create the adjacency list

		for (int i = 1; i <= nVertices; i++) {
			adjacencyList.put(i, new HashSet<>());
		}

		for (Edge edge : edges) {
			adjacencyList.get(edge.getV1()).add(edge.getV2());
			adjacencyList.get(edge.getV2()).add(edge.getV1());
		}
	}

	//get sample method
	public GInstance getSample() {
		return new GInstance(nVertices, edges);
	}

	//run a monte carlo simulation of the graph to find the probability of the graph being connected
	public double[] monteCarlo(Integer nSamples, float delta) {
		//the number of connected graphs
		Long nConnected = 0L;

		//run the monte carlo simulation
		for (int i = 0; i < nSamples; i++) {
			//get a sample
			GInstance sample = getSample();

			//if the sample is connected
			if (sample.isConnected()) {
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

	//parallel implementation of the monte carlo simulation


	//return the edges
	public Set<Edge> getEdges() {
		return edges;
	}

	//add edge method
	public void addEdge(Edge edge) {
		edges.add(edge);
	}

	
	//calulate the distance between two vertices v1 and v2 using dijkstra's algorithm where distance is the sum of the weights of the edges. Without a priority queue
	public double distance(int v1, int v2) {
		//the distance array
		double[] distance = new double[nVertices+1];

		//the visited array
		boolean[] visited = new boolean[nVertices+1];

		//initialize the distance array
		for (int i = 1; i <= nVertices; i++) {
			distance[i] = Double.MAX_VALUE;
		}

		//set the distance of the source vertex to 0
		distance[v1] = 0;

		//for each vertex
		for (int i = 1; i <= nVertices; i++) {
			//find the vertex with the minimum distance
			int minVertex = -1;
			double minDistance = Double.MAX_VALUE;
			for (int j = 1; j <= nVertices; j++) {
				if (!visited[j] && distance[j] < minDistance) {
					minVertex = j;
					minDistance = distance[j];
				}
			}

			//if the minimum distance is infinity, then break
			if (minDistance == Double.MAX_VALUE) {
				break;
			}

			//mark the vertex as visited
			visited[minVertex] = true;

			//for each neighbor of the vertex
			for (int neighbor : adjacencyList.get(minVertex)) {
				//if the neighbor is not visited
				if (!visited[neighbor]) {
					//find the edge between the vertex and the neighbor
					Edge edge = null;
					for (Edge e : edges) {
						if ((e.getV1() == minVertex && e.getV2() == neighbor) || (e.getV2() == minVertex && e.getV1() == neighbor)) {
							edge = e;
							break;
						}
					}

					//if the distance of the neighbor is greater than the distance of the vertex plus the weight of the edge, then update the distance of the neighbor
					if (distance[neighbor] > distance[minVertex] + edge.getWeight()) {
						distance[neighbor] = distance[minVertex] + edge.getWeight();
					}
				}
			}
		}

		//return the distance of the destination vertex
		return distance[v2]/0.05;
	}
}