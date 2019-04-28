package ru.spb.hse.roguelike;

import ru.spb.hse.roguelike.controler.Controller;
import ru.spb.hse.roguelike.controler.GameStateSaver;
import ru.spb.hse.roguelike.model.GameModel;
import ru.spb.hse.roguelike.model.Generator;
import ru.spb.hse.roguelike.model.MapGeneratorException;
import ru.spb.hse.roguelike.model.UnknownObjectException;
import ru.spb.hse.roguelike.model.map.GameCellException;
import ru.spb.hse.roguelike.model.map.GameMapCellType;
import ru.spb.hse.roguelike.model.object.items.CannotApplyFoodMultipleTimesException;
import ru.spb.hse.roguelike.view.TerminalView;
import ru.spb.hse.roguelike.view.View;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Optional;

public class Main {
    public static void main(String[] args) throws GameCellException, CannotApplyFoodMultipleTimesException {
        try {
            GameModel model = getGameModel(args);
            View view = new TerminalView(model);
            Controller controller = new Controller(view, model);
            controller.runGame();
        } catch (MapGeneratorException e) {
            System.out.println("Map creation problems: " + e.getMessage());
        } catch (FileNotFoundException e) {
            System.out.println("File " + args[0] + " not found");
        } catch (IOException | InterruptedException e) {
            System.out.println("Game graphics (view) problems. Please, restart the game.");
        } catch (UnknownObjectException e) {
            System.out.println("Unknown exception. Please, restart the game");
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
