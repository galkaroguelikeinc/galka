package ru.spb.hse.roguelike.controler;

import ru.spb.hse.roguelike.model.GameModel;
import ru.spb.hse.roguelike.model.object.alive.GameCharacter;
import ru.spb.hse.roguelike.view.Command;
import ru.spb.hse.roguelike.view.View;
import ru.spb.hse.roguelike.view.ViewException;

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
        while (true) {
            Command command = view.readCommand();
            if (command == null) {
                continue;
            }
            switch (command) {
                case LEFT: {
                    handleMove(-1, 0);
                    break;
                }
                case RIGHT: {
                    handleMove(1, 0);
                    break;
                }
                case UP: {
                    handleMove(0, -1);
                    break;
                }
                case DOWN: {
                    handleMove(0, 1);
                    break;
                }
            }
        }
    }

    private void handleMove(int rowDiff, int colDiff) throws ViewException {
        int oldRow = gameModel.getAliveObjectRow(character);
        int oldCol = gameModel.getAliveObjectCol(character);
        boolean moved = gameModel.moveAliveObjectDiff(character, rowDiff, colDiff);
        if (moved) {
            view.showChanges(oldRow, oldCol);
            int newRow = gameModel.getAliveObjectRow(character);
            int newCol = gameModel.getAliveObjectCol(character);
            view.showChanges(newRow, newCol);
        }
    }
}
