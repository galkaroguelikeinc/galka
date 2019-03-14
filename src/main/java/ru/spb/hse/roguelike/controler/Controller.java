package ru.spb.hse.roguelike.controler;

import ru.spb.hse.roguelike.model.Generator;
import ru.spb.hse.roguelike.model.map.GameCell;
import ru.spb.hse.roguelike.model.object.alive.GameCharacter;
import ru.spb.hse.roguelike.model.map.GameMapCellType;
import ru.spb.hse.roguelike.model.GameModel;
import ru.spb.hse.roguelike.view.View;

/**
 * Class to do the actions, which are supposed to be done according the game rules: moving character,
 * do fights, deal with items etc. The class changes the model and also asks View class to show the changes.
 */
public class Controller {
    private View view;
    private GameModel gameModel;
    private GameCharacter character;

    /**
     * Creates new controller.
     * @param view view to show changes
     * @param gameModel model to change
     */
    public Controller(View view, GameModel gameModel) {
        this.gameModel = gameModel;
        this.view = view;
        character = Generator.generateCharacter();
    }

    /**
     * Runs the read commands until game ends.
     */
    public void runGame() {
        while(true) {
            String command = view.readCommand();
            switch (command) {
                case "left": {
                    int newX = character.getXPos() - 1;
                    int newY = character.getYPos();
                    move(newX, newY);
                    break;
                }
                case "right": {
                    int newX = character.getXPos() + 1;
                    int newY = character.getYPos();
                    move(newX, newY);break;
                }
                case "up": {
                    int newX = character.getXPos();
                    int newY = character.getYPos() - 1;
                    move(newX, newY);
                    break;
                }
                case "down": {
                    int newX = character.getXPos();
                    int newY = character.getYPos() + 1;
                    move(newX, newY);
                    break;
                }
            }
        }
    }

    private void move(int newX, int newY) {
        if (isFreeCell(newX, newY)) {
            character.move(newX, newY);
            if (gameModel.getCell(newY, newX).hasItem()
                    && gameModel.getInventory().size() != GameModel.getMaxInventorySize()){
                gameModel.addInventory(gameModel.takeCellItem(newX, newY));
            }
        }
    }

    private boolean isFreeCell(int x, int y) {
        GameCell cell = gameModel.getCell(x, y);
        return (cell.getGameMapCellType().equals(GameMapCellType.room)
                || cell.getGameMapCellType().equals(GameMapCellType.tunnel));
    }
}
