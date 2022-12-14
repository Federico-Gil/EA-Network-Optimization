//declare the package
package org.uma.jmetal.problem.singleobjective.grafo;

import java.util.*;

class Graph_pq { 
    double dist[]; 
    Set<Integer> visited; 
    PriorityQueue<Node> pqueue; 
    int V; // Number of vertices 
    List<List<Node> > adj_list; 
    //class constructor
    public Graph_pq(int V) { 
        this.V = V; 
        dist = new double[V]; 
        visited = new HashSet<Integer>(); 
        pqueue = new PriorityQueue<Node>(V, new Node()); 
    }
   
    // Dijkstra's Algorithm implementation 
    public void algo_dijkstra(List<List<Node> > adj_list, int src_vertex) 
    { 
        this.adj_list = adj_list; 
   
        for (int i = 1; i < V; i++) 
            dist[i] = Double.MAX_VALUE; 
   
        // first add source vertex to PriorityQueue 
        pqueue.add(new Node(src_vertex, 0)); 
   
        // Distance to the source from itself is 0 
        dist[src_vertex] = 0f; 
    
        while (visited.size() != V ) { 

            if (pqueue.isEmpty())
                return;

            // u is removed from PriorityQueue and has min distance  
            int u = pqueue.remove().node;

            // Adding the node whose distance is
                // finalized
                if (visited.contains(u))
     
                    // Continue keyword skips execution for
                    // following check
                    continue;
   
            // add node to finalized list (visited)
            visited.add(u); 
            graph_adjacentNodes(u); 


        } 
    } 
  // this methods processes all neighbours of the just visited node 
    private void graph_adjacentNodes(int u)   { 
        double edgeDistance = -1; 
        double newDistance = -1; 
   
        // process all neighbouring nodes of u 
        for (int i = 0; i < adj_list.get(u).size(); i++) { 
            Node v = adj_list.get(u).get(i); 
   
            //  proceed only if current node is not in 'visited'
            if (!visited.contains(v.node)) { 
                edgeDistance = v.cost; 
                newDistance = dist[u] + edgeDistance; 
   
                // compare distances 
                if (newDistance < dist[v.node]) 
                    dist[v.node] = newDistance; 
   
                // Add the current vertex to the PriorityQueue 
                pqueue.add(new Node(v.node, dist[v.node])); 
            } 
        } 
    }
}
class Main{    
    public static void main(String arg[])   { 
        //for each vertex, we create a list of adjacent vertices
        //return a list of maps
        List<Map<Integer, Double>> list = new ArrayList<Map<Integer, Double>>();
        //for each vertex call dijkstra algorithm
        for (int i = 0; i < 25 ;i++) {
            //put the map result of dijkstra algorithm in the list
            list.add(Dijkstra(i));
        }

        //
        Grafo grafo; 
        Set<Edge> asd = readEdges("C:/Users/Fede/Desktop/AE/EA-Network-Optimization/data/24-nodesWc.csv");
        List<Edge> possibleEdges = readPossibleEdges("C:\\Users\\Fede\\Desktop\\AE\\EA-Network-Optimization\\data\\costosPosibles.csv");
        grafo = new Grafo(24,asd,possibleEdges);
        
        //aristas del grafo original
        Set<Edge> e1 =  grafo.getEdges();
        
        ArrayList<List<Integer>> posibleEdges;
        int numberOfNodes = grafo.getnVertices();
        posibleEdges = new ArrayList<List<Integer>>();
        for (int i = 1; i <= numberOfNodes; i++) {
            for (int j = i+1; j <= numberOfNodes; j++) {
                List<Integer> edge = new ArrayList<Integer>();
                edge.add(i);
                edge.add(j);
                // if the edge is not in the original graph, add it to the list of posible edges, without using contains
                if (!e1.contains(new Edge(i,j,0f,0f,true))) {
                    posibleEdges.add(edge);
                }
            }
        }


        //for each posible edge, we calculate the cost of the edge and write it to a csv file

        for (int i = 0; i < posibleEdges.size(); i++) {
            int node1 = posibleEdges.get(i).get(0);
            int node2 = posibleEdges.get(i).get(1);
            double cost = list.get(node1).get(node2);
            System.out.println(node1 + ", " + node2 + ", " + (cost*0.7) + ", ");
        }
    } 

    public static Map<Integer, Double> Dijkstra(int source){
        int V = 25; 
        //read the edges from the .csv file
        Set<Edge> edges = readEdges("C:\\Users\\Fede\\Desktop\\AE\\EA-Network-Optimization\\data\\24-nodesWc.csv");
        List<Edge> possibleEdges = readPossibleEdges("C:\\Users\\Fede\\Desktop\\AE\\EA-Network-Optimization\\data\\costosPosibles.csv");
        //create a graph
        Grafo graph = new Grafo(V, edges,possibleEdges);

        // adjacency list representation of graph
        List<List<Node> > adj_list = new ArrayList<List<Node> >(); 
    
        // Initialize adjacency list for every node in the graph 
        for (int i = 0; i < V; i++) { 
            List<Node> item = new ArrayList<Node>(); 
            adj_list.add(item); 
        } 
 
        //get the edges from the graph
        Set<Edge> edgeSet = graph.getEdges();

        //add edges to the graph
        for (Edge edge : edgeSet ) {
            adj_list.get(edge.getV1()).add(new Node(edge.getV2(), edge.getCost()));
            adj_list.get(edge.getV2()).add(new Node(edge.getV1(), edge.getCost()));
        }
   
        
        // //print the graph
        // for (int i = 1; i < adj_list.size(); i++) { 
        //     List<Node> edgeList = adj_list.get(i); 
        //     for (int j = 1; j < edgeList.size(); j++) { 
        //         System.out.println("node " + i + " is connected to " 
        //                            + edgeList.get(j).node + " with cost " 
        //                            + edgeList.get(j).cost); 
        //     } 
        // }
   
        // call Dijkstra's algo method  
        Graph_pq dpq = new Graph_pq(V); 
        dpq.algo_dijkstra(adj_list, source); 
   
        // Print the shortest path from source node to all the nodes 
        System.out.println("The shorted path from source node to other nodes:"); 
        System.out.println("Source\t\t" + "Node#\t\t" + "Distance");
        for (int i = 1; i < dpq.dist.length; i++) 
            System.out.println(source + " \t\t " + i + " \t\t "  + dpq.dist[i]);

        //create a map with the distances
        Map<Integer, Double> distances = new HashMap<Integer, Double>();
        for (int i = 1; i < dpq.dist.length; i++) {
            distances.put(i, dpq.dist[i]);
        }
        return distances;
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
} 

// Node class  
class Node implements Comparator<Node> { 
    public int node; 
    public double cost; 
    public Node() { } //empty constructor 
   
    public Node(int node, double cost) { 
        this.node = node; 
        this.cost = cost; 
    } 
    @Override
    public int compare(Node node1, Node node2) 
    { 
        if (node1.cost < node2.cost) 
            return -1; 
        if (node1.cost > node2.cost) 
            return 1; 
        return 0; 
    } 
}