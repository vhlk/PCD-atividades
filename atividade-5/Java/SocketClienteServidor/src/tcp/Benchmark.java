package tcp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Benchmark {
	public static final int sNumRepetitions = 10_000;
	private static int sNumClients;

	public static void main(String[] args) {		
		sNumClients = Integer.parseInt(args[0]);


		Thread serverThread = new Thread() {
			public void run() {
				Server.main(args);
			}
		};
		serverThread.start();

		List<Thread> parallelThreadsList = new ArrayList<>();
		for (int i = 0; i < sNumClients - 1; i++) {
			parallelThreadsList.add(new Thread() {
				@Override
				public void run() {
					for (int i = 0; i < sNumRepetitions; i++) {
						try {
							Cliente.runWithouUserInteraction();
						} catch (IOException e) {
							System.err.println("Erro no cliente "+i+": "+e.getMessage());
						}
					}
				}
			});
		}

		Thread client = new Thread() {
			@Override
			public void run() {
				List<Long> allTimes = new ArrayList<>();
				for (int i = 0; i < sNumRepetitions; i++) {
					try {
						long t1 = System.nanoTime();
						Cliente.runWithouUserInteraction();
						long executionTime = System.nanoTime() - t1;
						allTimes.add(executionTime);
					} catch (Exception e) {
						System.err.println("Erro no cliente "+i+": "+e.getMessage());
					}
				}
				
				long mean = getListMean(allTimes);
				System.out.println("Tempo medio: "+mean+" ns");
				System.out.println("Desvio padrao: "+standardDeviation(allTimes, mean));
			}			
		};

		for (int i = 0; i < sNumClients - 1; i++) {
			parallelThreadsList.get(i).start();
		}
		
		client.start();
	}

	public static long getListMean(List<Long> a) {
		long sum = 0;
		for (int i = 0; i < a.size(); i++) sum += a.get(i);
		
		return sum/a.size();
	}

	public static double standardDeviation(List<Long> a, long mean) {
		double standardDev = 0.0;

		for (int i = 0; i < a.size(); i++) 
			standardDev += Math.pow(a.get(i) - mean, 2);
		
		standardDev = standardDev / a.size();

		standardDev = Math.sqrt(standardDev);

		return standardDev;
	}

}
