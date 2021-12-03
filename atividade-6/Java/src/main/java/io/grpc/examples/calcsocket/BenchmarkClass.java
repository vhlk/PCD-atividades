package io.grpc.examples.calcsocket;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.ArrayList;
import java.util.List;

public class BenchmarkClass {

    private static ManagedChannel channel;
    private static Client client;
    private static List<Long> results;

    public static void main(String[] args){
         channel = ManagedChannelBuilder.forTarget("localhost:"+ 8888).usePlaintext().build();
         client = new Client(channel);
         results = new ArrayList<>();

         run();
    }

    public static void run(){
        for (int i = 0; i < 10; i++) {

            long startTime = System.nanoTime();

            for (int j = 0; j < 10000; j++) {
                client.run();
            }

            long finishTime = System.nanoTime() - startTime;
            results.add(finishTime);
        }

        long sum = 0;
        for (int i = 0; i < 10; i++) {
            System.out.printf("Execution %d result: %d ns \n", i, results.get(i));
            sum += results.get(i);
        }

        long average = sum / results.size();
        System.out.println("Average: " + average);
    }
}
