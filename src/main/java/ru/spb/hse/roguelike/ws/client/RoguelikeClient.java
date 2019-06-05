package ru.spb.hse.roguelike.ws.client;


import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import ru.spb.hse.roguelike.Galka;
import ru.spb.hse.roguelike.RoguelikeServiceGrpc;
import ru.spb.hse.roguelike.model.GameModel;
import ru.spb.hse.roguelike.view.CommandName;

import java.io.IOException;

/**
 * Class for reading server commands and working with them.
 */
public class RoguelikeClient {
    private final RoguelikeServiceGrpc.RoguelikeServiceBlockingStub blockingStub;


    RoguelikeClient(String host, int port) {
        this(ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build());
    }


    private RoguelikeClient(ManagedChannel channel) {
        blockingStub = RoguelikeServiceGrpc.newBlockingStub(channel);
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

    void connectToExistingGame(int userId,
                               int gameId) {
        Galka.ConnectToExistingGameRequest request = Galka.ConnectToExistingGameRequest
                .newBuilder()
                .setUserId(userId)
                .setGameId(gameId)
                .build();
        blockingStub.connectToExistGame(request);
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
        blockingStub.addMove(request);
    }

}
