package ru.spb.hse.roguelike;

import ru.spb.hse.roguelike.controler.Controller;
import ru.spb.hse.roguelike.exceptions.MapGeneratorException;
import ru.spb.hse.roguelike.model.GameModel;
import ru.spb.hse.roguelike.model.Generator;
import ru.spb.hse.roguelike.model.map.GameMapCellType;
import ru.spb.hse.roguelike.view.TerminalView;
import ru.spb.hse.roguelike.view.View;

public class Main {
    public static void main(String[] args) {
        try {
            final GameModel model = Generator.generateModel(2,7, 7);
            /*
            для проверки карты
            for (int i = 0; i < 7; i ++) {
                for (int j = 0; j < 7;j++) {
                    if (model.getCell(i, j).getGameMapCellType() == GameMapCellType.empty) {
                        System.out.print(".");
                    };
                    if (model.getCell(i, j).getGameMapCellType() == GameMapCellType.tunnel) {
                        System.out.print("0");
                    };
                    if (model.getCell(i, j).getGameMapCellType() == GameMapCellType.room) {
                        System.out.print("W");
                    };
                    if (model.getCell(i, j).getGameMapCellType() == GameMapCellType.wall) {
                        System.out.print("-");
                    };
                }
                System.out.println();
            }*/
            final View view = new TerminalView(model);
            Controller controller = new Controller(view, model);
            controller.runGame();
        } catch (MapGeneratorException e) {
            System.out.println("Невозможно создать карту");
        }
    }
}
