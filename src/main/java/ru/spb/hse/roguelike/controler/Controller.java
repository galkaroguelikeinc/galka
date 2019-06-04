package ru.spb.hse.roguelike.controler;

import ru.spb.hse.roguelike.Point;
import ru.spb.hse.roguelike.controler.strategy.*;
import ru.spb.hse.roguelike.model.CannotDropWearableException;
import ru.spb.hse.roguelike.model.CannotPickItemException;
import ru.spb.hse.roguelike.model.GameModel;
import ru.spb.hse.roguelike.model.UnknownObjectException;
import ru.spb.hse.roguelike.model.map.GameCellException;
import ru.spb.hse.roguelike.model.object.alive.GameCharacter;
import ru.spb.hse.roguelike.model.object.alive.NonPlayerCharacter;
import ru.spb.hse.roguelike.model.object.items.CannotApplyFoodMultipleTimesException;
import ru.spb.hse.roguelike.view.CommandName;
import ru.spb.hse.roguelike.view.CommandNameId;
import ru.spb.hse.roguelike.view.View;
import ru.spb.hse.roguelike.view.ViewException;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static ru.spb.hse.roguelike.view.CommandName.SKIP;


/**
 * Class to do the actions, which are supposed to be done according the game rules: moving character,
 * do fights, deal with items etc. The class changes the model and also asks View class to show the changes.
 */
public class Controller {
    private static final Random RANDOM = new Random();
    private View view;
    private GameModel gameModel;
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
        Map<Integer, GameCharacter>  characters = gameModel.getGameCharacters();
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
        GameStateSaver.setSavedState(gameModel);
        while (executeCommand()) {
            GameStateSaver.setSavedState(gameModel);
        }
        view.end();
    }

    private boolean moveMobs() throws ViewException, UnknownObjectException, GameCellException {
        for (NonPlayerCharacter npc : gameModel.getNonGameCharacters()) {
            Map<Integer, GameCharacter> characters = gameModel.getGameCharacters();
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
            for (int i = 0; i < characters.size(); i++) {
                if (gameModel.getAliveObjectPoint(characters.get(i)).equals(point)) {
                    if (!fightNPC(i, oldPoint)) {
                        return false;
                    }
                }
            }
            gameModel.moveAliveObject(npc, point);
            view.showChanges(oldPoint);
            view.showChanges(point);
        }
        return true;
    }

    boolean executeCommand() throws ViewException, UnknownObjectException, GameCellException {
        CommandNameId commandNameId = view.readCommand();
        if (commandNameId == null || commandNameId.getCommandName() == null || commandNameId.getCommandName() == SKIP) {
            return true;
        }
        System.out.println("executeCommand");
        boolean result = invoker.executeCommand(commandNameId.getCommandName(), commandNameId.getId());
        moveMobs();
        return result;
    }

    boolean applyItem(int playerId) {
        try {
            gameModel.makeCharacterApplyItem(playerId);
        } catch (CannotPickItemException | CannotApplyFoodMultipleTimesException ignored) {
            // TODO: APPLY_ITEM or DROP_WEARABLE should not count as a move if it didn't succeed
        }
        return true;
    }

    boolean dropWearable(int playerId) {
        try {
            gameModel.makeCharacterDropTopWearable(playerId);
        } catch (CannotDropWearableException ignored) {
        }
        return true;
    }

    boolean confuseMobs() {
        gameModel.confuseMobs();
        return true;
    }

    boolean handleMove(int playerId, int rowDiff, int colDiff) throws ViewException, UnknownObjectException {
        Map<Integer, GameCharacter> characters = gameModel.getGameCharacters();
        Point diff = new Point(rowDiff, colDiff);
        Point oldPoint = gameModel.getAliveObjectPoint(characters.get(playerId));
        boolean moved = false;
        try {
            moved = gameModel.moveAliveObjectDiff(characters.get(playerId), diff);
        } catch (GameCellException ignored) {
        }
        Point point = oldPoint.add(diff);
        if (moved) {
            view.showChanges(oldPoint);
            view.showChanges(point);
        } else {
            if (gameModel.getCell(point) != null &&
                    gameModel.getCell(point).hasAliveObject()) {
                return fightNPC(playerId, point);
            }
        }
        return true;
    }

    private boolean fightNPC(int playerId, Point point) throws ViewException {
        int gameCharacterHit = RANDOM.nextInt(2);
        int npcHit = RANDOM.nextInt(2);
        Map<Integer, GameCharacter> characters = gameModel.getGameCharacters();
        NonPlayerCharacter npc = (NonPlayerCharacter) gameModel.getCell(point).getAliveObject();
        if (npcHit == 1) {
            characters.get(playerId).setCurrentHealth(characters.get(playerId).getCurrentHealth() - npc.getCurrentPower());
            if (characters.get(playerId).getCurrentHealth() == 0) {
                GameStateSaver.deleteState();
                return false;
            }
        }
        if (gameCharacterHit == 1) {
            npc.setCurrentHealth(npc.getCurrentHealth() - characters.get(playerId).getCurrentPower());
            if (npc.getCurrentHealth() == 0) {
                gameModel.removeAliveObject(point);
                view.showChanges(point);
            }
        }
        return true;
    }
}
