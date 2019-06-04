package ru.spb.hse.roguelike.ws.client;

import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import ru.spb.hse.roguelike.controler.Controller;
import ru.spb.hse.roguelike.controler.GameStateSaver;
import ru.spb.hse.roguelike.model.GameModel;
import ru.spb.hse.roguelike.model.Generator;
import ru.spb.hse.roguelike.model.MapGeneratorException;
import ru.spb.hse.roguelike.model.map.GameCellException;
import ru.spb.hse.roguelike.model.map.GameMapCellType;
import ru.spb.hse.roguelike.view.TerminalView;
import ru.spb.hse.roguelike.view.View;
import ru.spb.hse.roguelike.view.ViewException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Optional;

public class ClientApplication {

    public static void main(String[] args) throws Exception {
        TerminalScreen terminalScreen;
        DefaultTerminalFactory defaultTerminalFactory = new DefaultTerminalFactory();
        try {
            terminalScreen = new TerminalScreen(defaultTerminalFactory.createTerminal());
            terminalScreen.startScreen();
            terminalScreen.setCursorPosition(null);
        } catch (IOException e) {
            throw new ViewException();
        }
        terminalScreen.clear();
        printString(terminalScreen,"press up for local playing, press down for multi-players");
        terminalScreen.refresh();
        KeyStroke key = terminalScreen.readInput();
        terminalScreen.close();
        switch (key.getKeyType()) {
            case ArrowDown: {
                terminalScreen.clear();
                printString(terminalScreen,"print hostname and port number");
                terminalScreen.refresh();
                String host = readWord(terminalScreen);
                String port = readWord(terminalScreen);
                printString(terminalScreen,"print game id or -1 fro new game");
                int gameId = Integer.valueOf(readWord(terminalScreen));
                //TODO: подключиться к серверу, получить от него карту, создать terminalView
                // и запустить в бесконечном цикле запросы к серверу (как внутри контроллера)
                RoguelikeClient client = new RoguelikeClient("localhost", 50051);
                int userId = client.getUserId();
                if (gameId == -1) {
                    gameId = client.startNewGame(userId);
                } else {
                    client.connectToExistingGame(userId, gameId);
                }
                GameModel model = client.getMap(gameId);
                View view = new TerminalView(model);

                while (true) {

                }

            }
            case ArrowUp: {
                printString(terminalScreen,"print model args");
                GameModel model = getGameModel(new String[0]);
                View view = new TerminalView(model);
                Controller controller = new Controller(view, model);
                controller.runGame();
            }
        }
    }

    private static void printString(TerminalScreen terminalScreen, String string) {
        char[] array = string.toCharArray();
        for (int i = 0; i < array.length; i++) {
            terminalScreen.setCharacter(i, 0, new TextCharacter(array[i]));
        }
    }

    private static String readWord(TerminalScreen terminalScreen) throws IOException {
        StringBuilder ans = new StringBuilder();
        while (true) {
            KeyStroke key = terminalScreen.readInput();
            if (key.getCharacter().equals(' ') || key.getCharacter().equals(System.lineSeparator())) {
                return ans.toString();
            }
            ans.append(key.getCharacter());
        }
    }

    private static GameModel getGameModel(String[] args) throws IOException, MapGeneratorException, GameCellException {
        Optional<GameModel> prevGameModel = GameStateSaver.getSavedState();
        if (!prevGameModel.isPresent()) {
            return getNewGameModel(args);
        }
        boolean startNewGame = TerminalView.getAnswer("Начать новую игру или продолжить?" +
                        "\n Нажмите 1, чтобы прождолжить" +
                        "\n Нажмите 2 чтобы начать новую игру",
                character -> character == '2');
        if (startNewGame) {
            return getNewGameModel(args);
        }
        return prevGameModel.get();
    }

    private static GameModel getNewGameModel(String[] args) throws FileNotFoundException, MapGeneratorException, GameCellException {
        Generator generator = new Generator();
        if (args.length > 0) {
            return generator.generateModel(args[0], character -> {
                if (character == '#') {
                    return GameMapCellType.TUNNEL;
                }
                if (character == '.') {
                    return GameMapCellType.ROOM;
                }
                return GameMapCellType.EMPTY;
            });
        }
        return generator.generateModel(3, 20, 20);
    }
}
