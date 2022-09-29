package grafo;
import java.util.*;

public class Grafo {
	private Map<Integer, List<Integer>> adjVertices;
	private boolean visited[];
	int size = 24;
	Integer[][] edges = {
			{1, 5}, {1, 9}, {1, 14}, {1, 21}, {2, 3}, {2, 12},	
			{2, 21}, {3, 10}, {3, 16},{3, 23}, {4, 7}, {4, 9},
			{4, 15}, {4, 16}, {5, 6}, {5, 24}, {6, 13},	{6, 17}, 
			{6,22}, {7, 8}, {7, 11}, {7, 19}, {7, 23}, {8, 22}, 
			{10, 16}, {10, 23}, {11, 23}, {14, 18}, {15, 17}, 
			{15, 19}, {17, 22}, {18, 24}, {20, 24} 
	};
	
	public Grafo() {
		adjVertices = new HashMap<>();
		visited = new boolean[size+1];
		for (int i = 0; i < size; i++) {
			visited[i] = false;			
		}
		
		for (int i = 1; i <= 24; i++) addVertex(i);
		
		for (int i = 0; i < edges.length; i++) {
			if (Math.random() > 0.05)
				addEdge(edges[i][0], edges[i][1]);
		}	
}
	
	public void addVertex(Integer id) {
	    adjVertices.putIfAbsent(id, new ArrayList<>());
	}
	
	public void addEdge(Integer v1, Integer v2) {
	    adjVertices.get(v1).add(v2);
	    adjVertices.get(v2).add(v1);
	}
	
	public void removeEdge(Integer v1, Integer v2) {
	    List<Integer> eV1 = adjVertices.get(v1);
	    List<Integer> eV2 = adjVertices.get(v2);
	    if (eV1 != null)
	        eV1.remove(v2);
	    if (eV2 != null)
	        eV2.remove(v1);
	}
	
	private void DFS(int vertex) {
			 visited[vertex] = true;

			 Iterator<Integer> ite = adjVertices.get(vertex).listIterator();
			 while (ite.hasNext()) {
			   int adj = ite.next();
			   if (!visited[adj])
			     DFS(adj);
			 }
	}
	
	public boolean isConnected() {
		
		for (int i = 0; i < size; i++) {
			visited[i] = false;			
		}
		
		Optional<Integer> firstkey = adjVertices.keySet().stream().findAny();
		
		if (firstkey.isPresent())
			DFS(firstkey.get());
		
		int temp = 0;
		for (int i = 0; i < visited.length; i++) {
			if (visited[i] == false)
				temp += 1;
		}
		return temp <= 1;
	}
			
}