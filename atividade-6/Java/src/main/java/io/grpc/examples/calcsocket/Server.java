package io.grpc.examples.calcsocket;

import com.google.protobuf.Any;
import com.google.rpc.Code;
import com.google.rpc.Status;
import io.grpc.ServerBuilder;
import io.grpc.protobuf.StatusProto;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class Server {
    private static final int PORT = 8888;

    private static final Logger logger = Logger.getLogger(Server.class.getName());

    private io.grpc.Server grpcServer;

    private void start() throws IOException {
        /* The port on which the server should run */
        grpcServer = ServerBuilder.forPort(PORT)
                .addService(new CalcSocket())
                .build()
                .start();
        logger.info("Server started, listening on " + PORT);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                // Use stderr here since the logger may have been reset by its JVM shutdown hook.
                System.err.println("*** shutting down gRPC server since JVM is shutting down");
                try {
                    Server.this.stop();
                } catch (InterruptedException e) {
                    e.printStackTrace(System.err);
                }
                System.err.println("*** server shut down");
            }
        });
    }

    private void stop() throws InterruptedException {
        if (grpcServer != null) {
            grpcServer.shutdown().awaitTermination(30, TimeUnit.SECONDS);
        }
    }

    /**
     * Await termination on the main thread since the grpc library uses daemon threads.
     */
    private void blockUntilShutdown() throws InterruptedException {
        if (grpcServer != null) {
            grpcServer.awaitTermination();
        }
    }

    /**
     * Main launches the server from the command line.
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        final Server server = new Server();
        server.start();
        server.blockUntilShutdown();
    }

    static class CalcSocket extends CalcSocketGrpc.CalcSocketImplBase {

        @Override
        public void calc(NumbersRequest req, StreamObserver<Response> responseObserver) {
            Response reply = null;
            if (req.getOperation().equals("+"))
                reply = Response.newBuilder().setResult(req.getFirst() + req.getSecond()).build();
            else if (req.getOperation().equals("-"))
                reply = Response.newBuilder().setResult(req.getFirst() - req.getSecond()).build();
            else if (req.getOperation().equals("/")) {
                if (req.getSecond() == 0) {
                    Status status = Status.newBuilder()
                            .setCode(Code.INVALID_ARGUMENT.getNumber())
                            .setMessage("0 division")
                            .build();
                    responseObserver.onError(StatusProto.toStatusRuntimeException(status));
                    responseObserver.onCompleted();
                    return;
                }
                reply = Response.newBuilder().setResult(req.getFirst() / req.getSecond()).build();
            }
            else if (req.getOperation().equals("*"))
                reply = Response.newBuilder().setResult(req.getFirst() * req.getSecond()).build();
            else {
                Status status = Status.newBuilder()
                        .setCode(Code.INVALID_ARGUMENT.getNumber())
                        .setMessage("operation not found")
                        .build();
                responseObserver.onError(StatusProto.toStatusRuntimeException(status));
                responseObserver.onCompleted();
                return;
            }

            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        }
    }
}
