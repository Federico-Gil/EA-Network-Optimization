package grafo;
import java.util.*;

public class Grafo {
	private Integer nVertices;
	private Set<Edge> edges;
	private Map<Integer, Set<Integer>> adjacencyList;
	private boolean[] visited;

	//the grafo constructor takes the number of vertices and the edges and creates the instance
	public Grafo(Integer nVertices, Set<Edge> edges) {
		this.nVertices = nVertices;
		this.edges = new HashSet<>(edges);
		this.adjacencyList = new HashMap<>();
		this.visited = new boolean[nVertices+1];
		
		//create the adjacency list
		for (int i = 1; i <= nVertices; i++) {
            adjacencyList.put(i, new HashSet<>());
        }

		for (int i = 0; i <= nVertices; i++) {
			visited[i] = false;			
		}

		for (int i = 1; i <= nVertices; i++) addVertex(i);
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
    private void dfs(int vertex) {
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

	//funtion that returns a copy of the graph
	public Grafo copy() {
		Set<Edge> edgesCopy = new HashSet<>();
		for (Edge edge : edges) {
			edgesCopy.add(edge.copy());
		}
		return new Grafo(nVertices, edgesCopy);
	}
}