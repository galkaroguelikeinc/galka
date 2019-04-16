package ru.spb.hse.roguelike;

import ru.spb.hse.roguelike.controler.Controller;
import ru.spb.hse.roguelike.model.MapGeneratorException;
import ru.spb.hse.roguelike.model.GameModel;
import ru.spb.hse.roguelike.model.Generator;
import ru.spb.hse.roguelike.model.UnknownObjectException;
import ru.spb.hse.roguelike.model.map.GameMapCellType;
import ru.spb.hse.roguelike.view.TerminalView;
import ru.spb.hse.roguelike.view.View;

import java.io.IOException;

import java.io.FileNotFoundException;

public class Main {
    public static void main(String[] args) {
        try {
            Generator generator = new Generator();
            GameModel model;
            if (args.length > 0) {
                model = generator.generateModel(args[0], character -> {
                    if (character == '#') {
                        return GameMapCellType.TUNNEL;
                    }
                    if (character == '.') {
                        return GameMapCellType.ROOM;
                    }
                    return GameMapCellType.EMPTY;
                });
            } else {
                model = generator.generateModel(6, 20, 20);
            }

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
}
