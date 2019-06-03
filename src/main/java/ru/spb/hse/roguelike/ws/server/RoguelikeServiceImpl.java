package ru.spb.hse.roguelike.ws.server;

import io.grpc.stub.StreamObserver;
import ru.spb.hse.roguelike.Galka;
import ru.spb.hse.roguelike.RoguelikeServiceGrpc;

public class RoguelikeServiceImpl extends RoguelikeServiceGrpc.RoguelikeServiceImplBase {

    @Override
    public void ping(Galka.PingRequest request,
                     StreamObserver<Galka.PingResponse> responseObserver) {
        Galka.PingResponse response = Galka.PingResponse.newBuilder()
                .setPong(request.getPing() + "-pong")
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}