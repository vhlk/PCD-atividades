package io.grpc.examples.calcsocket;

import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client {
    private static final int PORT = 8888;

    private static final Logger logger = Logger.getLogger(Client.class.getName());

    private final CalcSocketGrpc.CalcSocketBlockingStub calcSocketBlockingStub;

    /** Construct client for accessing HelloWorld server using the existing channel. */
    public Client(Channel channel) {
        // 'channel' here is a Channel, not a ManagedChannel, so it is not this code's responsibility to
        // shut it down.

        // Passing Channels to code makes code easier to test and makes it easier to reuse Channels.
        calcSocketBlockingStub = CalcSocketGrpc.newBlockingStub(channel);

    }

    public void run(){
        double firstNumber = 1, secondNumber = 1;
        String operation = "+";

        NumbersRequest numbersRequest = NumbersRequest.newBuilder()
                .setFirst(firstNumber)
                .setSecond(secondNumber)
                .setOperation(operation).build();

        try {
            calcSocketBlockingStub.calc(numbersRequest);
        } catch (StatusRuntimeException ignored) {
        }
    }

    public void calc() {
        Scanner in = new Scanner(System.in);

        while (true) {
            System.out.println("Type the first number or 'q' to quit:");
            String usrInput = in.nextLine();
            if (usrInput.equals("q")) {
                return;
            }
            double firstNumber = Double.parseDouble(usrInput);

            System.out.println("Type the operation you want to do or 'q' to quit:");
            String operation = in.nextLine();
            if (!operation.equals("/") && !operation.equals("*") && !operation.equals("-") && !operation.equals("+")) {
                System.out.println("Operacao indisponível");
                continue;
            }

            System.out.println("Type the second number or 'q' to quit:");
            usrInput = in.nextLine();
            if (usrInput.equals("q")) {
                return;
            }
            double secondNumber = Double.parseDouble(usrInput);

            NumbersRequest numbersRequest = NumbersRequest.newBuilder()
                    .setFirst(firstNumber).setSecond(secondNumber).setOperation(operation).build();
            Response response;
            try {
                response = calcSocketBlockingStub.calc(numbersRequest);
            } catch (StatusRuntimeException e) {
                logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
                return;
            }
            logger.info("Result: " + response.getResult());
        }
    }

    /**
     * Greet server. If provided, the first element of {@code args} is the name to use in the
     * greeting. The second argument is the target server.
     */
//    public static void main(String[] args) throws Exception {
//        // Access a service running on the local machine on port 50051
//        String target = "localhost:"+PORT;
//        // Create a communication channel to the server, known as a Channel. Channels are thread-safe
//        // and reusable. It is common to create channels at the beginning of your application and reuse
//        // them until the application shuts down.
//        ManagedChannel channel = ManagedChannelBuilder.forTarget(target)
//                // Channels are secure by default (via SSL/TLS). For the example we disable TLS to avoid
//                // needing certificates.
//                .usePlaintext()
//                .build();
//        try {
//            Client client = new Client(channel);
//            client.calc();
//        } finally {
//            // ManagedChannels use resources like threads and TCP connections. To prevent leaking these
//            // resources the channel should be shut down when it will no longer be used. If it may be used
//            // again leave it running.
//            channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
//        }
//    }
}
