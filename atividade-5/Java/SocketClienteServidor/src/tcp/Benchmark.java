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
		
		long allTimes = 0;
		for (int i = 0; i < sNumRepetitions; i++) {
			long t1 = System.nanoTime();
			try {
				Cliente.runWithouUserInteraction();
			} catch (IOException e) {
				System.err.println("Erro no cliente "+i+": "+e.getMessage());
			}
			allTimes += System.nanoTime() - t1;
		}
		
		double averageTime = (allTimes / sNumRepetitions);
		
		System.out.println("Tempo medio: "+averageTime+"ns"); // Tempo medio: 791840.0ns
	}

}
