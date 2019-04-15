package ru.spb.hse.roguelike.controler;

import ru.spb.hse.roguelike.controler.strategy.AggressiveStrategy;
import ru.spb.hse.roguelike.controler.strategy.CowardlyStrategy;
import ru.spb.hse.roguelike.controler.strategy.PassiveStrategy;
import ru.spb.hse.roguelike.Point;
import ru.spb.hse.roguelike.model.GameModel;
import ru.spb.hse.roguelike.model.object.alive.AliveObject;
import ru.spb.hse.roguelike.model.UnknownObjectException;
import ru.spb.hse.roguelike.model.object.alive.GameCharacter;
import ru.spb.hse.roguelike.model.object.alive.Mob;
import ru.spb.hse.roguelike.view.Command;
import ru.spb.hse.roguelike.view.View;
import ru.spb.hse.roguelike.view.ViewException;

import java.io.IOException;
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
     * @param gameModel model to setCurrentValue
     */
    public Controller(View view, GameModel gameModel) {
        this.gameModel = gameModel;
        this.view = view;
        character = gameModel.getCharacter();
    }

    /**
     * Runs the read commands until game ends.
     */
    public void runGame() throws IOException, InterruptedException {
        while (executeCommand() && moveMobs());
        view.end();
    }

    private boolean moveMobs() throws ViewException {
        for (AliveObject mob : gameModel.getMobs()) {
            Point oldPoint = gameModel.getAliveObjectPoint(mob);
            Point point = null;
            switch (((Mob) mob).getMobStrategyType()) {
                case PASSIVE:
                    point = new PassiveStrategy().move(gameModel, oldPoint);
                    break;
                case AGGRESSIVE:
                    point = new AggressiveStrategy().move(gameModel, oldPoint);
                    break;
                case COWARDLY:
                    point = new CowardlyStrategy().move(gameModel, oldPoint);
                    break;
            }
            if (gameModel.getAliveObjectPoint(character).equals(point)) {
                if (!fightMob(oldPoint)) {
                    return false;
                }
            }
            gameModel.moveAliveObject(mob, point);
            view.showChanges(oldPoint);
            view.showChanges(point);
        }
        return true;
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
        Point diff = new Point(rowDiff, colDiff);
        Point oldPoint = gameModel.getAliveObjectPoint(character);
        boolean moved = gameModel.moveAliveObjectDiff(character, diff);
        Point point = oldPoint.add(diff);
        if (moved) {
            view.showChanges(oldPoint);
            if (gameModel.getCell(point).hasItem() &&
                    gameModel.getInventory().size() != GameModel.getMaxInventorySize()) {
                gameModel.addItem(gameModel.takeCellItem(point));
            }
            view.showChanges(point);
        } else {
            if (gameModel.getCell(point) != null &&
                 gameModel.getCell(point).hasAliveObject()) {
                return fightMob(point);
            }
        }
        return true;
    }

    private boolean fightMob(Point point) throws ViewException {
        int gameCharacterHit = RANDOM.nextInt(2);
        int mobHit = RANDOM.nextInt(2);
        Mob mob = (Mob) gameModel.getCell(point).getAliveObject();
        if (mobHit == 1) {
            character.changeHealth(character.getHealth() - mob.getPower());
            if (character.getHealth() == 0) {
                return false;
            }
        }
        if (gameCharacterHit == 1) {
            mob.changeHealth(mob.getHealth() - character.getPower());
            if (mob.getHealth() == 0) {
                gameModel.removeAliveObject(point);
                view.showChanges(point);
            }
        }
        return true;
    }
}
