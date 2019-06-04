package ru.spb.hse.roguelike.ws.server;

import io.grpc.stub.StreamObserver;
import ru.spb.hse.roguelike.Galka;
import ru.spb.hse.roguelike.RoguelikeServiceGrpc;
import ru.spb.hse.roguelike.model.MapGeneratorException;
import ru.spb.hse.roguelike.model.map.GameCellException;

import java.io.IOException;

public class RoguelikeServiceImpl extends RoguelikeServiceGrpc.RoguelikeServiceImplBase {

    private final RoguelikeServer server;

    public RoguelikeServiceImpl(RoguelikeServer server) {
        this.server = server;
    }

    @Override
    public void ping(Galka.Empty request,
                     StreamObserver<Galka.PingResponse> responseObserver) {
        Galka.PingResponse response = Galka.PingResponse.newBuilder()
                .setPong("pong")
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getUserId(ru.spb.hse.roguelike.Galka.GetUserIdRequest request,
                          io.grpc.stub.StreamObserver<ru.spb.hse.roguelike.Galka.GetUserIdResponse> responseObserver) {
        long userId = server.getUserId();
        Galka.GetUserIdResponse response = Galka.GetUserIdResponse.newBuilder()
                .setUserId(userId)
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void startNewGame(ru.spb.hse.roguelike.Galka.StartNewGameRequest request,
                             io.grpc.stub.StreamObserver<ru.spb.hse.roguelike.Galka.StartNewGameResponse> responseObserver) {
        try {
            long gameId = server.createNewGame(request.getUserId());
            Galka.StartNewGameResponse response = Galka.StartNewGameResponse.newBuilder()
                    .setGameId(gameId)
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }

    }

    @Override
    public void connectToExistGame(ru.spb.hse.roguelike.Galka.ConnectToExistingGameRequest request,
                                   io.grpc.stub.StreamObserver<ru.spb.hse.roguelike.Galka.ConnectToExistingGameResponse> responseObserver) {
        boolean result = server.connectToGame(request.getUserId(), request.getGameId());
        Galka.ConnectToExistingGameResponse response = Galka.ConnectToExistingGameResponse.newBuilder()
                .setResult(result)
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void addMove(ru.spb.hse.roguelike.Galka.AddMoveRequest request,
                        io.grpc.stub.StreamObserver<ru.spb.hse.roguelike.Galka.AddMoveResponse> responseObserver) {
        //TODO запрос к контроллеру. ЧТо этот пользователь сделал такой то ход в такой то игре
        Galka.AddMoveResponse response = Galka.AddMoveResponse.newBuilder()
                .setResult(true)
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getCurUser(ru.spb.hse.roguelike.Galka.GetCurUserRequest request,
                           io.grpc.stub.StreamObserver<ru.spb.hse.roguelike.Galka.GetCurUserResponse> responseObserver) {
        long curUser = server.getCurUser(request.getGameId());
        Galka.GetCurUserResponse response = Galka.GetCurUserResponse.newBuilder()
                .setUserId(curUser)
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getMap(ru.spb.hse.roguelike.Galka.GetMapRequest request,
                       io.grpc.stub.StreamObserver<ru.spb.hse.roguelike.Galka.GetMapResponse> responseObserver) {
        try {
            String mapString = server.getMapString(request.getGameId());
            Galka.GetMapResponse response = Galka.GetMapResponse.newBuilder()
                    .setMapString(mapString)
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (IOException e) {
            responseObserver.onError(e);
        }
    }
}