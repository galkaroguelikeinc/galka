package ru.spb.hse.roguelike.ws.client;

import ru.spb.hse.roguelike.Point;
import ru.spb.hse.roguelike.model.GameModel;
import ru.spb.hse.roguelike.model.map.GameMapCellType;

import java.util.function.Function;

public class OlyaClientApplication {
    public static void main(String[] args) throws Exception {
        RoguelikeClient client = new RoguelikeClient("localhost", 50051);
        long userId = client.getUserId();
        long gameId = client.startNewGame(userId);
        GameModel gameModel = client.getMap(gameId);
        Function< GameMapCellType, Character> decoder = type -> {
            if (type == GameMapCellType.ROOM) {
                return '.';
            }
            if (type == GameMapCellType.TUNNEL) {
                return '#';
            }
            return ' ';
        };
        for (int i = 0; i < gameModel.getRows(); i++) {
            for (int j = 0; j < gameModel.getCols(); j++) {
                System.out.print(decoder.apply(gameModel.getCell(new Point(i, j)).getGameMapCellType()));
            }
            System.out.println();
        }
    }
}
