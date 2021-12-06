package io.grpc.examples.calcsocket;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.ArrayList;
import java.util.List;

import java.math.BigDecimal;

public class BenchmarkClass {

    private static ManagedChannel channel;
    private static Client client;
    private static int sNumClients;

    public static void main(String[] args){        
        sNumClients = Integer.parseInt(args[0]);

         channel = ManagedChannelBuilder.forTarget("localhost:"+ 8888).usePlaintext().build();
         client = new Client(channel);

         run();
    }

    public static void run() {
        List<Thread> paralledThreads = new ArrayList<>();

        for (int i = 0; i < sNumClients - 1; i++) {
            paralledThreads.add(new Thread() {
                @Override
                public void run() {
                    for (int j = 0; j < 10_000; j++) {
                        client.run();
                    }
                }
            });
        }

        Thread cliente = new Thread() {
            @Override
            public void run() {
                List<Long> allTimes = new ArrayList<>();

                for (int j = 0; j < 10_000; j++) {
                    long startTime = System.nanoTime();

                    client.run();

                    long executionTime = System.nanoTime() - startTime;
                    allTimes.add(executionTime);
                }

                long mean = getListMean(allTimes);
                double sd = standardDeviation(allTimes, mean);

                System.out.println("Tempo medio: "+mean + " ns");
                System.out.println("Desvio padrao: " + new BigDecimal(sd).toPlainString());
            }
        };

        for (int i = 0; i < sNumClients - 1; i++) 
            paralledThreads.get(i).start();

        cliente.start();
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
