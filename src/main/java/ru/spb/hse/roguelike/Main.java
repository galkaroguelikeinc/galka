package ru.spb.hse.roguelike;

import ru.spb.hse.roguelike.controler.Controller;
import ru.spb.hse.roguelike.exceptions.MapGeneratorException;
import ru.spb.hse.roguelike.model.GameModel;
import ru.spb.hse.roguelike.model.Generator;
import ru.spb.hse.roguelike.view.TerminalView;
import ru.spb.hse.roguelike.view.View;

public class Main {
    public static void main(String[] args) {
        try {
            final GameModel model = Generator.generateModel(4,20, 20);
            final View view = new TerminalView(model);
            Controller controller = new Controller(view, model);
            controller.runGame();
        } catch (MapGeneratorException e) {
            System.out.println("Map creation problems: " + e.getMessage());
        }
    }
}
