import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

public class CalculatorBenchmark {
    private static int sNumClients;

    public static void main(String[] args) {
        try {
            sNumClients = Integer.parseInt(args[0]);

            List<Thread> paralledThreads = new ArrayList<>();

            for (AtomicInteger i = new AtomicInteger(0); i.get() < sNumClients - 1; i.getAndIncrement()) {
                paralledThreads.add(new Thread(() -> {
                        parallelClients(i.get());
                }));
            }

            ConnectionFactory factory = new ConnectionFactory();
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();
            channel.queueDeclare("CalculatorRequest", false, false, false, null);
            channel.queueDeclare("CalculatorResponse", false, false, false, null);
            Gson gson = new Gson();

            int id = 0;
            AtomicInteger consumedMessages = new AtomicInteger();
            List<Long> allTimes = new ArrayList<>(10000);

            channel.basicConsume("CalculatorResponse", true, (consumerTag, message) -> {
                CalculatorResponse calculatorResponse = gson.fromJson(new String(message.getBody()), CalculatorResponse.class);
                allTimes.set(calculatorResponse.id, System.nanoTime() - allTimes.get(calculatorResponse.id));
                consumedMessages.getAndIncrement();

                if (consumedMessages.get() == 10_000) {
                    long mean = getListMean(allTimes);
                    double sd = standardDeviation(allTimes, mean);

                    System.out.println("Tempo medio: "+mean + " ns");
                    System.out.println("Desvio padrao: " + new BigDecimal(sd).toPlainString());
                }
            }, consumerTag -> {});

            for (int i = 0; i < sNumClients - 1; i++)
                paralledThreads.get(i).start();

            for (int i = 0; i < 10_000; i++) {
                CalculatorRequest calculatorRequest = new CalculatorRequest(1, 2, '+', id);

                String json = new Gson().toJson(calculatorRequest);

                allTimes.add(System.nanoTime());
                channel.basicPublish("", "CalculatorRequest", false, null, json.getBytes());

                id++;
            }

        } catch (IOException | TimeoutException ignored) { }
    }

    public static void parallelClients(int id) {
        try {
            ConnectionFactory factory = new ConnectionFactory();
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();
            channel.queueDeclare("CalculatorRequest"+id, false, false, false, null);
            channel.queueDeclare("CalculatorResponse"+id, false, false, false, null);
            Gson gson = new Gson();

            channel.basicConsume("CalculatorResponse"+id, true, (consumerTag, message) -> {
                gson.fromJson(new String(message.getBody()), CalculatorResponse.class);
            }, consumerTag -> {
            });

            for (int i = 0; i < 10_000; i++) {
                CalculatorRequest calculatorRequest = new CalculatorRequest(1, 2, '+', id);

                String json = new Gson().toJson(calculatorRequest);

                channel.basicPublish("", "CalculatorRequest"+id, false, null, json.getBytes());

                id++;
            }
        } catch (Exception ignored) {}
    }

    public static long getListMean(List<Long> a) {
        long sum = 0;
        for (Long aLong : a) sum += aLong;

        return sum/a.size();
    }

    public static double standardDeviation(List<Long> a, long mean) {
        double standardDev = 0.0;

        for (Long aLong : a) standardDev += Math.pow(aLong - mean, 2);

        standardDev = standardDev / a.size();

        standardDev = Math.sqrt(standardDev);

        return standardDev;
    }
}
