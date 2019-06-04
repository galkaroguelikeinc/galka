package ru.spb.hse.roguelike.ws.server;


import io.grpc.Server;
import io.grpc.ServerBuilder;
import ru.spb.hse.roguelike.controler.Controller;
import ru.spb.hse.roguelike.model.GameModel;
import ru.spb.hse.roguelike.model.Generator;
import ru.spb.hse.roguelike.model.MapGeneratorException;
import ru.spb.hse.roguelike.model.map.GameCellException;
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
    private final Map<Long, GameInfo> games = new HashMap<>();
    private static final Logger logger = Logger.getLogger(RoguelikeServer.class.getName());
    private final Server server;
    private static long userCounter = 0;
    private static long gameCounter = 0;

    public RoguelikeServer(int port) throws IOException {
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

    public void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    public long getUserId() {
        long userId = userCounter;
        userCounter++;
        return userId;
    }

    public long getGameId() {
        long gameId = gameCounter;
        gameCounter++;
        return gameId;
    }

    public long createNewGame(long userId) throws MapGeneratorException, GameCellException {
        Generator generator = new Generator();
        GameModel model = generator.generateModel(3, 20, 20);
        Controller controller = new Controller(new ServerView(), model);
        List<Long> users = new ArrayList<>();
        users.add(userId);
        GameInfo gameInfo = new GameInfo(model, users, controller);
        long gameId = getGameId();
        games.put(gameId, gameInfo);
        return gameId;
    }

    public boolean connectToGame(long userId,
                                 long gameId) {
        if (!games.containsKey(gameId)) {
            return false;
        }
        games.get(gameId).userIds.add(userId);
        //TODO как то добавить в мапку нового игрока и сказать об этом контроллеру
        return true;
    }

    public Long getCurUser(long gameId) {
        if (!games.containsKey(gameId)) {
            throw new IllegalArgumentException("Failed to get game with Id = " + gameId);
        }
        return games.get(gameId).getCurUser();
    }

    public String getMapString(long gameId) throws IOException {
        return games.get(gameId) == null
                ? ""
                : GameModel.toString(games.get(gameId).gameModel);
    }

    public class GameInfo {
        private final GameModel gameModel;
        private final List<Long> userIds;
        private final Controller controller;
        private int curUser = 0;

        public GameInfo(GameModel gameModel, List<Long> userIds, Controller controller) {
            this.gameModel = gameModel;
            this.userIds = userIds;
            this.controller = controller;
        }

        @Override
        public int hashCode() {
            return Objects.hash(gameModel, userIds, controller);
        }

        public Long getCurUser() {
            if (userIds.size() == 0) {
                throw new IllegalArgumentException("Failed to get curUser. There are no players");
            }
            int ind = curUser;
            //TODO изменяем только когда получили ход или пропусk
            curUser = (curUser + 1) % userIds.size();
            return userIds.get(ind);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            GameInfo that = (GameInfo) o;
            return Objects.equals(gameModel, that.gameModel)
                    && Objects.equals(userIds, that.userIds)
                    && Objects.equals(controller, that.controller);
        }

        @Override
        public String toString() {
            return "GameInfo {" +
                    "gameModel=" + gameModel +
                    "userIds=" + userIds +
                    "controller" + controller +
                    "}";
        }
    }
}