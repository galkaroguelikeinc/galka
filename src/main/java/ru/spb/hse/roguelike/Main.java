package ru.spb.hse.roguelike;

import ru.spb.hse.roguelike.controler.Controller;
import ru.spb.hse.roguelike.model.GameModel;
import ru.spb.hse.roguelike.view.TerminalView;
import ru.spb.hse.roguelike.view.View;

public class Main {
    public static void main(String[] args) {
        GameModel model = new GameModel();
        View view = new TerminalView(model);
        Controller controller = new Controller(view, model);
        controller.runGame();
    }
}
