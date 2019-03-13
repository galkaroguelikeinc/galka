package ru.spb.hse.roguelike.controler;

import ru.spb.hse.roguelike.model.GameCell;
import ru.spb.hse.roguelike.model.object.alive.GameCharacter;
import ru.spb.hse.roguelike.model.GameMapCellType;
import ru.spb.hse.roguelike.model.GameModel;
import ru.spb.hse.roguelike.view.View;

public class Controller {
    private View view;
    private GameModel gameModel;
    private GameCharacter character;

    public Controller(View view, GameModel gameModel) {
        this.gameModel = gameModel;
        this.view = view;
        character = gameModel.generateCharacter();
    }

    public void runGame() {
        while(true) {
            String command = view.readCommand();
            switch (command) {
                case "left": {
                    int newX = character.getxPos() - 1;
                    int newY = character.getyPos();
                    move(newX, newY);
                    break;
                }
                case "right": {
                    int newX = character.getxPos() + 1;
                    int newY = character.getyPos();
                    move(newX, newY);break;
                }
                case "up": {
                    int newX = character.getxPos();
                    int newY = character.getyPos() - 1;
                    move(newX, newY);
                    break;
                }
                case "down": {
                    int newX = character.getxPos();
                    int newY = character.getyPos() + 1;
                    move(newX, newY);
                    break;
                }
                case "exit": {
                    return;
                }

            }
        }
    }

    private void move(int newX, int newY) {
        if (isFreeCell(newX, newY)) {
            character.move(newX, newY);
            if (gameModel.getCell(newY, newX).hasItem()
                    && gameModel.getInventory().size() != character.getMaxInventorySize()){
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
