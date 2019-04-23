package ru.spb.hse.roguelike.controler;

import ru.spb.hse.roguelike.Point;
import ru.spb.hse.roguelike.controler.strategy.AggressiveStrategy;
import ru.spb.hse.roguelike.controler.strategy.CowardlyStrategy;
import ru.spb.hse.roguelike.controler.strategy.PassiveStrategy;
import ru.spb.hse.roguelike.controler.strategy.RandomStrategy;
import ru.spb.hse.roguelike.controler.strategy.StrategyException;
import ru.spb.hse.roguelike.model.CannotDropWearableException;
import ru.spb.hse.roguelike.model.CannotPickItemException;
import ru.spb.hse.roguelike.model.GameModel;
import ru.spb.hse.roguelike.model.UnknownObjectException;
import ru.spb.hse.roguelike.model.map.GameCellException;
import ru.spb.hse.roguelike.model.object.alive.GameCharacter;
import ru.spb.hse.roguelike.model.object.alive.NonPlayerCharacter;
import ru.spb.hse.roguelike.view.CommandName;
import ru.spb.hse.roguelike.model.object.items.CannotApplyFoodMultipleTimesException;
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
    private Invoker invoker = new Invoker();

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
        invoker.setCommand(CommandName.UP, new UpMoveCommand(this));
        invoker.setCommand(CommandName.DOWN, new DownMoveCommand(this));
        invoker.setCommand(CommandName.LEFT, new LeftMoveCommand(this));
        invoker.setCommand(CommandName.RIGHT, new RightMoveCommand(this));
        invoker.setCommand(CommandName.CONFUSE_MOBS, new ConfuseMobsCommand(this));
        invoker.setCommand(CommandName.APPLY_ITEM, new ApplyItemCommand(this));
        invoker.setCommand(CommandName.DROP_WEARABLE, new DropWearableCommand(this));
    }

    /**
     * Runs the read commands until game ends.
     */
    public void runGame() throws IOException, InterruptedException, UnknownObjectException, GameCellException {
        Saveilka.setSavedState(gameModel);
        while (executeCommand() && moveMobs()) {
            Saveilka.setSavedState(gameModel);
        }
        view.end();
    }

    private boolean moveMobs() throws ViewException, UnknownObjectException, GameCellException {
        for (NonPlayerCharacter npc : gameModel.getNonGameCharacters()) {
            Point oldPoint = gameModel.getAliveObjectPoint(npc);
            Point point = null;
            try {
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
            } catch (StrategyException e) {
                point = oldPoint;
            }
            if (gameModel.getAliveObjectPoint(character).equals(point)) {
                if (!fightNPC(oldPoint)) {
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
        CommandName commandName = view.readCommand();
        if (commandName == null) {
            return true;
        }
        return invoker.executeCommand(commandName);
    }

    boolean applyItem() {
        try {
            gameModel.makeCharacterApplyItem();
        } catch (CannotPickItemException | CannotApplyFoodMultipleTimesException ignored) {
            // TODO: APPLY_ITEM or DROP_WEARABLE should not count as a move if it didn't succeed
        }
        return true;
    }

    boolean dropWearable() {
        try {
            gameModel.makeCharacterDropTopWearable();
        } catch (CannotDropWearableException ignored) {
        }
        return true;
    }

    boolean confuseMobs() {
        gameModel.confuseMobs();
        return true;
    }

    boolean handleMove(int rowDiff, int colDiff) throws ViewException, UnknownObjectException {
        Point diff = new Point(rowDiff, colDiff);
        Point oldPoint = gameModel.getAliveObjectPoint(character);
        boolean moved = false;
        try {
            moved = gameModel.moveAliveObjectDiff(character, diff);
        } catch (GameCellException ignored) {
        }
        Point point = oldPoint.add(diff);
        if (moved) {
            view.showChanges(oldPoint);
            view.showChanges(point);
        } else {
            if (gameModel.getCell(point) != null &&
                    gameModel.getCell(point).hasAliveObject()) {
                return fightNPC(point);
            }
        }
        return true;
    }

    private boolean fightNPC(Point point) throws ViewException {
        int gameCharacterHit = RANDOM.nextInt(2);
        int npcHit = RANDOM.nextInt(2);
        NonPlayerCharacter npc = (NonPlayerCharacter) gameModel.getCell(point).getAliveObject();
        if (npcHit == 1) {
            character.setCurrentHealth(character.getCurrentHealth() - npc.getCurrentPower());
            if (character.getCurrentHealth() == 0) {
                Saveilka.deleteState();
                return false;
            }
        }
        if (gameCharacterHit == 1) {
            npc.setCurrentHealth(npc.getCurrentHealth() - character.getCurrentPower());
            if (npc.getCurrentHealth() == 0) {
                gameModel.removeAliveObject(point);
                view.showChanges(point);
            }
        }
        return true;
    }
}
