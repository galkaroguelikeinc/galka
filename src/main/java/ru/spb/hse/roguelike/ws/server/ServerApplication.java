package ru.spb.hse.roguelike.ws.server;

import java.io.IOException;

public class ServerApplication {
    public static void main(String[] args) throws IOException, InterruptedException {
        final RoguelikeServer server = new RoguelikeServer(50051);
        server.start();
        server.blockUntilShutdown();
    }
}
