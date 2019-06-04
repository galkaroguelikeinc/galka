package ru.spb.hse.roguelike.ws.server;


import io.grpc.Server;
import io.grpc.ServerBuilder;
import ru.spb.hse.roguelike.controler.Controller;
import ru.spb.hse.roguelike.model.GameModel;
import ru.spb.hse.roguelike.model.Generator;
import ru.spb.hse.roguelike.model.MapGeneratorException;
import ru.spb.hse.roguelike.model.map.GameCellException;
import ru.spb.hse.roguelike.view.CommandName;
import ru.spb.hse.roguelike.view.ServerView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * Server that manages startup/shutdown of a {@code Greeter} server.
 */
public class RoguelikeServer {
    private final Map<Integer, GameInfo> games = new HashMap<>();
    private static final Logger logger = Logger.getLogger(RoguelikeServer.class.getName());
    private final Server server;
    private static int userCounter = 0;
    private static int gameCounter = 0;

    RoguelikeServer(int port) throws IOException {
        server = ServerBuilder.forPort(port)
                .addService(new RoguelikeServiceImpl(this))
                .build()
                .start();
        logger.info("Server started, listening on " + port);
    }

    public void start() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.err.println("*** shutting down gRPC server since JVM is shutting down");
            RoguelikeServer.this.stop();
            System.err.println("*** server shut down");
        }));
    }

    private void stop() {
        if (server != null) {
            server.shutdown();
        }
    }

    void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    int getUserId() {
        int userId = userCounter;
        userCounter++;
        return userId;
    }

    private int getGameId() {
        int gameId = gameCounter;
        gameCounter++;
        return gameId;
    }

    int createNewGame(int userId) throws MapGeneratorException, GameCellException {
        Generator generator = new Generator();
        GameModel model = generator.generateModel(3, 20, 20, userId);
        ServerView serverView = new ServerView();
        Controller controller = new Controller(serverView, model);
        List<Integer> users = new ArrayList<>();
        users.add(userId);
        GameInfo gameInfo = new GameInfo(model, users, controller, serverView);
        int gameId = getGameId();
        games.put(gameId, gameInfo);
        return gameId;
    }

    boolean connectToGame(int userId,
                          int gameId) throws GameCellException {
        if (!games.containsKey(gameId)) {
            return false;
        }
        games.get(gameId).userIds.add(userId);
        games.get(gameId).gameModel.addCharacter(userId);
        //TODO как то добавить в мапку нового игрока и сказать об этом контроллеру
        return true;
    }

    Integer getCurUser(int gameId) {
        if (!games.containsKey(gameId)) {
            throw new IllegalArgumentException("Failed to get game with Id = " + gameId);
        }
        return games.get(gameId).getCurUser();
    }

    String getMapString(int gameId) throws IOException {
        return games.get(gameId) == null
                ? ""
                : GameModel.toString(games.get(gameId).gameModel);
    }

    public void addMove(int userId,
                        int gameId,
                        CommandName commandName) {
        if (!games.containsKey(gameId)) {
            throw new IllegalArgumentException("Failed to get game with Id = " + gameId);
        }
        if (!games.get(gameId).userIds.contains(userId)) {
            throw new IllegalArgumentException("Failed to get user for  game = " + gameId);
        }
        ServerView serverView = games.get(gameId).serverView;
        serverView.addCommand(commandName, userId);
        games.get(gameId).incCurUser();
    }

    public class GameInfo {
        private final GameModel gameModel;
        private final List<Integer> userIds;
        private final Controller controller;
        private final ServerView serverView;
        private int curUser = 0;

        GameInfo(GameModel gameModel,
                 List<Integer> userIds,
                 Controller controller,
                 ServerView serverView) {
            this.gameModel = gameModel;
            this.userIds = userIds;
            this.controller = controller;
            this.serverView = serverView;
        }

        @Override
        public int hashCode() {
            return Objects.hash(gameModel, userIds, controller, serverView);
        }

        int getCurUser() {
            if (userIds.size() == 0) {
                throw new IllegalArgumentException("Failed to get curUser. There are no players");
            }
            return curUser;
        }

        public void incCurUser() {
            if (userIds.size() == 0) {
                throw new IllegalArgumentException("Failed to get curUser. There are no players");
            }
            curUser = (curUser + 1) % userIds.size();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            GameInfo that = (GameInfo) o;
            return Objects.equals(gameModel, that.gameModel)
                    && Objects.equals(userIds, that.userIds)
                    && Objects.equals(controller, that.controller)
                    && Objects.equals(serverView, that.serverView);
        }

        @Override
        public String toString() {
            return "GameInfo {" +
                    "gameModel=" + gameModel +
                    "userIds=" + userIds +
                    "controller" + controller +
                    "serverView" + serverView +
                    "}";
        }
    }
}