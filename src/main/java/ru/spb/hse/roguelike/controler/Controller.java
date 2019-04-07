package ru.spb.hse.roguelike.controler;

import ru.spb.hse.roguelike.model.GameModel;
import ru.spb.hse.roguelike.model.object.alive.AliveObject;
import ru.spb.hse.roguelike.model.object.alive.GameCharacter;
import ru.spb.hse.roguelike.model.object.alive.Mob;
import ru.spb.hse.roguelike.view.Command;
import ru.spb.hse.roguelike.view.View;
import ru.spb.hse.roguelike.view.ViewException;

import java.util.Random;

/**
 * Class to do the actions, which are supposed to be done according the game rules: moving character,
 * do fights, deal with items etc. The class changes the model and also asks View class to show the changes.
 */
public class Controller {
    private View view;
    private GameModel gameModel;
    private GameCharacter character;
    private static final Random RANDOM = new Random();

    /**
     * Creates new controller.
     *
     * @param view      view to show changes
     * @param gameModel model to change
     */
    public Controller(View view, GameModel gameModel) {
        this.gameModel = gameModel;
        this.view = view;
        character = gameModel.getCharacter();
    }

    /**
     * Runs the read commands until game ends.
     */
    public void runGame() throws ViewException {
        boolean end = false;
        while (!end) {
            Command command = view.readCommand();
            if (command == null) {
                continue;
            }
            switch (command) {
                case LEFT: {
                    end = handleMove(-1, 0);
                    break;
                }
                case RIGHT: {
                    end = handleMove(1, 0);
                    break;
                }
                case UP: {
                    end = handleMove(0, -1);
                    break;
                }
                case DOWN: {
                    end = handleMove(0, 1);
                    break;
                }
            }
        }
    }

    private boolean handleMove(int rowDiff, int colDiff) throws ViewException {
        int oldRow = gameModel.getAliveObjectRow(character);
        int oldCol = gameModel.getAliveObjectCol(character);
        boolean moved = gameModel.moveAliveObjectDiff(character, rowDiff, colDiff);
        if (moved) {
            view.showChanges(oldRow, oldCol);
            int newRow = gameModel.getAliveObjectRow(character);
            int newCol = gameModel.getAliveObjectCol(character);
            if (gameModel.getCell(newRow, newCol).hasItem() &&
                    gameModel.getInventory().size() != GameModel.getMaxInventorySize()) {
                gameModel.addItem(gameModel.takeCellItem(newRow, newCol));
            }
            view.showChanges(newRow, newCol);
        } else {
            if (gameModel.getCell(oldRow + rowDiff, oldCol + colDiff) != null &&
                    gameModel.getCell(oldRow + rowDiff, oldCol + colDiff).hasAliveObject()) {

            }
        }
    }

    private boolean fightMob(Mob mob) {
        int gameCharacterHit = RANDOM.nextInt(1);
        int mobHit = RANDOM.nextInt(1);
        if (mobHit == 1) {
            gameModel.getCharacter().changeHealth(-mob.getPower());
            //if (gameModel.getCharacter().)
        }

    }

}
