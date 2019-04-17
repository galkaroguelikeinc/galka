package ru.spb.hse.roguelike.controler;

import ru.spb.hse.roguelike.Point;
import ru.spb.hse.roguelike.controler.strategy.AggressiveStrategy;
import ru.spb.hse.roguelike.controler.strategy.CowardlyStrategy;
import ru.spb.hse.roguelike.controler.strategy.PassiveStrategy;
import ru.spb.hse.roguelike.controler.strategy.RandomStrategy;
import ru.spb.hse.roguelike.model.GameModel;
import ru.spb.hse.roguelike.model.UnknownObjectException;
import ru.spb.hse.roguelike.model.object.alive.GameCharacter;
import ru.spb.hse.roguelike.model.object.alive.Mob;
import ru.spb.hse.roguelike.model.object.alive.NonPlayerCharacter;
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
    public void runGame() throws IOException, InterruptedException, UnknownObjectException {
        while (executeCommand() && moveMobs()) ;
        view.end();
    }

    private boolean moveMobs() throws ViewException, UnknownObjectException {
        for (NonPlayerCharacter npc : gameModel.getNonGameCharacters()) {
            Point oldPoint = gameModel.getAliveObjectPoint(npc);
            Point point = null;
            switch (npc.getNonPlayerCharacterStrategyType()) {
                // TODO: generalize the code
                case PASSIVE:
                    // TODO: make it static and lazy-init
                    point = new PassiveStrategy().move(gameModel, oldPoint);
                    break;
                case AGGRESSIVE:
                    point = new AggressiveStrategy().move(gameModel, oldPoint);
                    break;
                case COWARDLY:
                    point = new CowardlyStrategy().move(gameModel, oldPoint);
                    break;
                case RANDOM:
                    point = new RandomStrategy().move(gameModel, oldPoint);
                    break;
            }
            if (gameModel.getAliveObjectPoint(character).equals(point)) {
                if (!fightMob(oldPoint)) {
                    return false;
                }
            }
            gameModel.moveAliveObject(npc, point);
            view.showChanges(oldPoint);
            view.showChanges(point);
        }
        return true;
    }

    boolean executeCommand() throws ViewException, UnknownObjectException {
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
            case CONFUSE_MOBS: {
                gameModel.confuseMobs();
                return true;
            }
        }
        return true;
    }

    private boolean handleMove(int rowDiff, int colDiff) throws ViewException, UnknownObjectException {
        Point diff = new Point(rowDiff, colDiff);
        Point oldPoint = gameModel.getAliveObjectPoint(character);
        boolean moved = gameModel.moveAliveObjectDiff(character, diff);
        Point point = oldPoint.add(diff);
        if (moved) {
            view.showChanges(oldPoint);
            if (gameModel.getCell(point).hasItem() &&
                    gameModel.getInventory().size() != gameModel.getMaxInventorySize()) {
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
            character.setCurrentHealth(character.getCurrentHealth() - mob.getCurrentPower());
            if (character.getCurrentHealth() == 0) {
                return false;
            }
        }
        if (gameCharacterHit == 1) {
            mob.setCurrentHealth(mob.getCurrentHealth() - character.getCurrentPower());
            if (mob.getCurrentHealth() == 0) {
                gameModel.removeAliveObject(point);
                view.showChanges(point);
            }
        }
        return true;
    }
}
