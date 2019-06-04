package ru.spb.hse.roguelike.ws.client;

import ru.spb.hse.roguelike.Point;
import ru.spb.hse.roguelike.model.GameModel;
import ru.spb.hse.roguelike.model.map.GameMapCellType;
import ru.spb.hse.roguelike.view.CommandName;

import java.util.function.Function;

public class OlyaClientApplication {
    public static void main(String[] args) throws Exception {
        RoguelikeClient client = new RoguelikeClient("localhost", 50051);
        int userId = client.getUserId();
        int gameId = client.startNewGame(userId);
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





        // создаю нового пользователя и подключаю к той же игре
        int newUserId = client.getUserId();
        client.connectToExistingGame(newUserId, gameId);


        //передаю действия
        System.out.println(client.getCurUser(gameId));
        client.addMove(userId, gameId, CommandName.SKIP);

        // тут должна быть ошибка
        try {
            client.addMove(userId, gameId, CommandName.SKIP);
        } catch (Exception e) {
            System.out.println("тут ошибка потому что ходит не тот пользователь");
        }


        System.out.println(client.getCurUser(gameId));
        client.addMove(newUserId, gameId, CommandName.SKIP);


        System.out.println(client.getCurUser(gameId));
        client.addMove(userId, gameId, CommandName.SKIP);

    }
}
