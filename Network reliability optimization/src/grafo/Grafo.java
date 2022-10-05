package grafo;
import java.util.*;

public class Grafo {
	private Integer nVertices;
	private Set<Edge> edges;

	//the grafo constructor takes the number of vertices and the edges and creates the instance
	public Grafo(Integer nVertices, Set<Edge> edges) {
		this.nVertices = nVertices;
		this.edges = edges;
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
}