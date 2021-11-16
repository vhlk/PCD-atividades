package tcp;

import java.io.IOException;

public class Benchmark {
	public static final int sNumRepetitions = 10_000;

	public static void main(String[] args) {
		Thread serverThread = new Thread() {
			public void run() {
				Server.main(args);
			}
		};
		serverThread.start();

		long t1 = System.nanoTime();
		for (int i = 0; i < sNumRepetitions; i++) {
			try {
				Cliente.runWithouUserInteraction();
			} catch (IOException e) {
				System.err.println("Erro no cliente "+i+": "+e.getMessage());
			}
		}
		
		long executionTime = System.nanoTime() - t1;
		
		double averageTime = (executionTime / sNumRepetitions);
		
		System.out.println("Tempo medio: "+averageTime+"ns"); // 763487.0ns, 830769.0ns, 811971.0ns
	}

}
