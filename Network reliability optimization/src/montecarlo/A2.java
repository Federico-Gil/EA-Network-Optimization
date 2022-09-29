package montecarlo;

public class A2 {
	public static void main(String[] args) {
		parallelMC PiVal = new parallelMC(1000000);
		long startTime = System.currentTimeMillis();
		double value = PiVal.getR();
		long stopTime = System.currentTimeMillis();
		System.out.println("Approx value:" + value);
		System.out.println("Available processors: " + Runtime.getRuntime().availableProcessors());
		System.out.println("Time Duration: " + (stopTime - startTime) + "ms");
	}
}