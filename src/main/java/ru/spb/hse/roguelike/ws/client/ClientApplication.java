package ru.spb.hse.roguelike.ws.client;

public class ClientApplication {
    public static void main(String[] args) throws Exception {
        RoguelikeClient client = new RoguelikeClient("localhost", 50051);
        try {
            client.ping();
        } finally {
            client.shutdown();
        }
    }
}
