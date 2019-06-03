package ru.spb.hse.roguelike.ws.server;

import io.grpc.stub.StreamObserver;
import ru.spb.hse.roguelike.Galka;
import ru.spb.hse.roguelike.RoguelikeServiceGrpc;

import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;

public class RoguelikeServiceImpl extends RoguelikeServiceGrpc.RoguelikeServiceImplBase {

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
        //TODO получить userId с контроллера
        long userId = 123L;
        Galka.GetUserIdResponse response = Galka.GetUserIdResponse.newBuilder()
                .setUserId(userId)
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void startNewGame(ru.spb.hse.roguelike.Galka.StartNewGameRequest request,
                             io.grpc.stub.StreamObserver<ru.spb.hse.roguelike.Galka.StartNewGameResponse> responseObserver) {
        //TODO начать новую игру в контроллере
        long gameId = 123L;
        Galka.StartNewGameResponse response = Galka.StartNewGameResponse.newBuilder()
                .setGameId(gameId)
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void connectToExistGame(ru.spb.hse.roguelike.Galka.ConnectToExistingGameRequest request,
                                   io.grpc.stub.StreamObserver<ru.spb.hse.roguelike.Galka.ConnectToExistingGameResponse> responseObserver) {
        //TODO запрос к контроллеру. ЧТо этот пользователь добавился к такой то игре
        Galka.ConnectToExistingGameResponse response = Galka.ConnectToExistingGameResponse.newBuilder()
                .setResult(true)
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
    public void getMap(ru.spb.hse.roguelike.Galka.GetMapRequest request,
                       io.grpc.stub.StreamObserver<ru.spb.hse.roguelike.Galka.GetMapResponse> responseObserver) {
        //TODO запрос контроллеру на получение карты для игры
        Galka.GetMapResponse response = Galka.GetMapResponse.newBuilder()
                .setMapString("это будет серилизованная карта")
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}