package ru.spb.hse.roguelike.controler;

import ru.spb.hse.roguelike.controler.strategy.AggressiveStrategy;
import ru.spb.hse.roguelike.controler.strategy.CowardlyStrategy;
import ru.spb.hse.roguelike.controler.strategy.PassiveStrategy;
import ru.spb.hse.roguelike.model.GameModel;
import ru.spb.hse.roguelike.model.map.Point;
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
        while (executeCommand()){
            moveMobs();
        }
        gameModel.end();
    }

    private void moveMobs() throws ViewException {
        for (AliveObject mob : gameModel.getMobs()) {
            Point point = null;
            switch (((Mob) mob).getMobStrategyType()) {
                case PASSIVE:
                    point = new PassiveStrategy().move(gameModel,
                            new Point(gameModel.getAliveObjectRow(mob), gameModel.getAliveObjectCol(mob)));
                    break;
                case AGGRESSIVE:
                    point = new AggressiveStrategy().move(gameModel,
                            new Point(gameModel.getAliveObjectRow(mob), gameModel.getAliveObjectCol(mob)));
                    break;
                case COWARDLY:
                    point = new CowardlyStrategy().move(gameModel,
                            new Point(gameModel.getAliveObjectRow(mob), gameModel.getAliveObjectCol(mob)));
                    break;
            }
            int oldRow = gameModel.getAliveObjectRow(mob);
            int oldCol = gameModel.getAliveObjectCol(mob);
            gameModel.moveAliveObject(mob, point.getRow(), point.getCol());
            view.showChanges(oldRow, oldCol);
            view.showChanges(point.getRow(), point.getCol());
        }

    }

    boolean executeCommand() throws ViewException {
        Command command = view.readCommand();
        if (command == null) {
            return true;
        }
        switch (command) {
            case LEFT: {
                return handleMove(0, -1);
            }
            case RIGHT: {
                return handleMove(0, 1);
            }
            case UP: {
                return handleMove(-1, 0);
            }
            case DOWN: {
                return handleMove(1, 0);
            }
        }
        return true;
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
                return fightMob(oldRow + rowDiff, oldCol + colDiff);
            }
        }
        return true;
    }

    private boolean fightMob(int row, int col) throws ViewException {
        int gameCharacterHit = RANDOM.nextInt(2);
        int mobHit = RANDOM.nextInt(2);
        if (mobHit == 1) {
            gameModel.getCharacter().changeHealth(gameModel.getCharacter().getHealth() - gameModel.getCell(row, col).getAliveObject().getPower());
            if (gameModel.getCharacter().getHealth() == 0) {
                return false;
            }
        }
        if (gameCharacterHit == 1) {
            gameModel.getCell(row, col).getAliveObject().changeHealth(gameModel.getCell(row, col).getAliveObject().getHealth() - character.getPower());
            if (gameModel.getCell(row, col).getAliveObject().getHealth() == 0) {
                gameModel.removeAliveObject(gameModel.getCell(row, col).getAliveObject());
                view.showChanges(row, col);
            }
        }
        return true;
    }
}
