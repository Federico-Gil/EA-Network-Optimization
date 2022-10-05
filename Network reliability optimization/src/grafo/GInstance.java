package grafo;

import java.util.*;

//this class is the instance of the graph
public class GInstance {
    //the number of vertices and the edges
    private Integer nVertices;
    private Set<Edge> edges;
    boolean[] visited;

    //the adjacency map
    private Map<Integer, Set<Integer>> adjMap;

    //the constructor of the class takes the number of vertices and the edges and creates the instance
    public GInstance(Integer nVertices, Set<Edge> edges) {
        this.nVertices = nVertices;
        this.edges = edges;
        this.visited = new boolean[nVertices+1];

        //create the adjacency map
        adjMap = new HashMap<>();
        for (int i = 0; i < nVertices; i++) {
            adjMap.put(i, new HashSet<>());
        }

        for (int i = 0; i < nVertices; i++) {
			visited[i] = false;			
		}
		
		for (int i = 1; i <= nVertices; i++) addVertex(i);
        
        //the weight of the edges determines the probability of the edge being in the adjacency map
        for (Edge edge : edges) {
            if (Math.random() > edge.getWeight()) {
                adjMap.get(edge.getV1()).add(edge.getV2());
                adjMap.get(edge.getV2()).add(edge.getV1());
            }
        }
    }

    //add vertex method
    public void addVertex(Integer vertex) {
        adjMap.putIfAbsent(vertex, new HashSet<>());
    }

    //dfs method
    private void dfs(int vertex) {
        visited[vertex] = true;
        for (int v : adjMap.get(vertex)) {
            if (!visited[v]) {
                dfs(v);
            }
        }
    }

    //return adjacency list of a vertex
    public Set<Integer> getAdjList(int vertex) {
        return adjMap.get(vertex);
    }

    //method to see if the graph is connected
    public boolean isConnected() {
		
		for (int i = 0; i < nVertices; i++) {
			visited[i] = false;			
		}
		
		Optional<Integer> firstkey = adjMap.keySet().stream().findFirst();
		
		if (firstkey.isPresent())
			dfs(1);

		for (int i = 1; i < visited.length; i++) {
			if (visited[i] == false)
				return false;
		}
		return true;
	}
}