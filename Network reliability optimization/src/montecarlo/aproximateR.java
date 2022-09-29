package montecarlo;

import java.time.Duration;
import java.time.Instant;

import grafo.Grafo;

public class aproximateR {

	public static void main(String[] args) {
		Instant start = Instant.now();
		Grafo g = new Grafo();
		
		float buenos = 0;
		Integer N = 100000;
		float delta = (float) 0.05;
		
		for (int n = 0; n <= N; n++) {
			g = new Grafo();
			if (g.isConnected()){
				buenos++;
			}
		}
		
		float Q = buenos/N;
		float V = Q*(1-Q)/(N-1);
		System.out.println("Estimador de la anti-confiablilidad:  " + Q);
		System.out.println("Estimador de la confiablilidad:  " + (1-Q));
		System.out.println("Desvio del estimador:  " + V);
		
		//IDC de chevyshev, pesimista pero garantiza el nivel de cobertura
		float z = buenos;
		float beta = (float) (1/Math.sqrt(delta));
		float lb = (float) ( (z + Math.pow(beta, 2)/2 - beta * Math.sqrt(Math.pow(beta, 2)/4 + z*(N-z)/N)) / (N+Math.pow(beta, 2)));
		float ub = (float) ( (z + Math.pow(beta, 2)/2 + beta * Math.sqrt(Math.pow(beta, 2)/4 + z*(N-z)/N)) / (N+Math.pow(beta, 2)));
		System.out.println("IDC: [" + lb + ", " + ub + "]");
					
		long timeElapsed = Duration.between(start, Instant.now()).toMillis();
		
		System.out.println("Time Elapsed:  " + timeElapsed);
	}

}
