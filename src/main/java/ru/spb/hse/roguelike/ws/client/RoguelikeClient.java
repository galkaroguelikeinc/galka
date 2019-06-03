package ru.spb.hse.roguelike.ws.client;


import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import ru.spb.hse.roguelike.Galka;
import ru.spb.hse.roguelike.RoguelikeServiceGrpc;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;


public class RoguelikeClient {
    private static final Logger logger = Logger.getLogger(RoguelikeClient.class.getName());

    private final ManagedChannel channel;
    private final RoguelikeServiceGrpc.RoguelikeServiceBlockingStub blockingStub;

    
    public RoguelikeClient(String host, int port) {
        this(ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build());
    }


    private RoguelikeClient(ManagedChannel channel) {
        this.channel = channel;
        blockingStub = RoguelikeServiceGrpc.newBlockingStub(channel);
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }


    public void ping() {
        Galka.PingRequest request = Galka.PingRequest.newBuilder().setPing("ping").build();
        Galka.PingResponse response;
        try {
            response = blockingStub.ping(request);
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            return;
        }
        logger.info(response.getPong());
    }
}
