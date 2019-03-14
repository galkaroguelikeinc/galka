package ru.spb.hse.roguelike;

import ru.spb.hse.roguelike.controler.Controller;
import ru.spb.hse.roguelike.exceptions.MapGeneratorException;
import ru.spb.hse.roguelike.map.MapGenerator;
import ru.spb.hse.roguelike.model.GameModel;
import ru.spb.hse.roguelike.view.TerminalView;
import ru.spb.hse.roguelike.view.View;

public class Main {
    public static void main(String[] args) {
        GameModel model = null;
        try {
            model = MapGenerator.generate(4, 80, 80);
        } catch (MapGeneratorException e) {
            System.out.println("Невозможно создать карту");
        }
        View view = new TerminalView(model);
        Controller controller = new Controller(view, model);
        controller.runGame();
    }
}
