package ru.spb.hse.roguelike.ws.client;


import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import ru.spb.hse.roguelike.Galka;
import ru.spb.hse.roguelike.RoguelikeServiceGrpc;
import ru.spb.hse.roguelike.model.GameModel;
import ru.spb.hse.roguelike.view.CommandName;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Base64;
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
        Galka.PingResponse response;
        try {
            response = blockingStub.ping(Galka.Empty.newBuilder().build());
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            return;
        }
        logger.info(response.getPong());
    }

    int getUserId() {
        Galka.GetUserIdRequest request = Galka.GetUserIdRequest.newBuilder().build();
        Galka.GetUserIdResponse response;
        response = blockingStub.getUserId(request);
        return response.getUserId();
    }

    GameModel getMap(int gameId) throws IOException, ClassNotFoundException {
        Galka.GetMapRequest request = Galka.GetMapRequest.newBuilder().setGameId(gameId).build();
        Galka.GetMapResponse response;
        response = blockingStub.getMap(request);
        String mapString = response.getMapString();
        return GameModel.fromString(mapString);
    }


    int startNewGame(int userId) {
        Galka.StartNewGameRequest request = Galka.StartNewGameRequest
                .newBuilder()
                .setUserId(userId)
                .build();
        Galka.StartNewGameResponse response;
        response = blockingStub.startNewGame(request);
        return response.getGameId();

    }

    boolean connectToExistingGame(int userId,
                                  int gameId) {
        Galka.ConnectToExistingGameRequest request = Galka.ConnectToExistingGameRequest
                .newBuilder()
                .setUserId(userId)
                .setGameId(gameId)
                .build();
        Galka.ConnectToExistingGameResponse response;
        response = blockingStub.connectToExistGame(request);
        return response.getResult();
    }

    int getCurUser(int gameId) {
        Galka.GetCurUserRequest request = Galka.GetCurUserRequest
                .newBuilder()
                .setGameId(gameId)
                .build();
        Galka.GetCurUserResponse response;
        response = blockingStub.getCurUser(request);
        return response.getUserId();
    }

    void addMove(int userId,
                 int gameId,
                 CommandName commandName) {
        Galka.AddMoveRequest request = Galka.AddMoveRequest
                .newBuilder()
                .setUserId(userId)
                .setGameId(gameId)
                .setCommandName(commandName.name())
                .build();
        Galka.AddMoveResponse response;
        blockingStub.addMove(request);
    }

}
