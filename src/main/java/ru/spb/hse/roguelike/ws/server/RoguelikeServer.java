package ru.spb.hse.roguelike.ws.server;


import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * Server that manages startup/shutdown of a {@code Greeter} server.
 */
public class RoguelikeServer {
    private static final Logger logger = Logger.getLogger(RoguelikeServer.class.getName());
    private final Server server;

    public RoguelikeServer(int port) throws IOException {
        server = ServerBuilder.forPort(port)
                .addService(new RoguelikeServiceImpl())
                .build()
                .start();
        logger.info("Server started, listening on " + port);
    }

    public void start() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.err.println("*** shutting down gRPC server since JVM is shutting down");
            RoguelikeServer.this.stop();
            System.err.println("*** server shut down");
        }));
    }

    private void stop() {
        if (server != null) {
            server.shutdown();
        }
    }

    public void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }
}