package udp;

import java.io.IOException;

public class Benchmark {
	private static final int SERVER_PORT = 8888;
	public static final int sNumRepetitions = 10_000;

	public static void main(String[] args) {
		Server server = new Server(SERVER_PORT);
		server.start();
		
		long t1 = System.nanoTime();
		for (int i = 0; i < sNumRepetitions; i++) {
			try {
				Client.runWithouUserInteraction();
			} catch (IOException e) {
				System.err.println("Erro no cliente "+i+": "+e.getMessage());
			}
		}
		long executionTime = System.nanoTime() - t1;
		
		double averageTime = (executionTime / sNumRepetitions);
		
		System.out.println("Tempo medio: "+averageTime+"ns"); // Tempo medio: 420735.0ns, 402675.0ns, 395403.0ns
	}

}
