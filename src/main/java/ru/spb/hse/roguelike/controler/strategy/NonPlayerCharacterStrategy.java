package ru.spb.hse.roguelike.controler.strategy;

import ru.spb.hse.roguelike.Point;
import ru.spb.hse.roguelike.model.GameModel;
import ru.spb.hse.roguelike.model.UnknownObjectException;
import ru.spb.hse.roguelike.model.object.alive.AliveObject;
import ru.spb.hse.roguelike.model.object.alive.GameCharacter;
import ru.spb.hse.roguelike.model.object.alive.NonPlayerCharacter;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class representing mob strategy.
 */
abstract class NonPlayerCharacterStrategy {
    static boolean hasNoNonPlayerCharacter(Point point, GameModel gameModel) {
        if (!gameModel.getCell(point).hasAliveObject()) {
            return true;
        }
        AliveObject aliveObject = gameModel.getCell(point).getAliveObject();
        return !(aliveObject instanceof NonPlayerCharacter);
    }

    /**
     * Method to move mob according to the stategy.
     * @param gameModel model where the game goes
     * @param mobPoint point where mob stands
     * @return point where mob moved
     * @throws StrategyException if strategy is unknown
     * @throws UnknownObjectException in fo mob in the point
     */
    abstract Point move(@Nonnull GameModel gameModel,
                        @Nonnull Point mobPoint) throws StrategyException, UnknownObjectException;

    private boolean isOutsideOfMap(Point point, GameModel gameModel) {
        if (point.getRow() < 0 || point.getRow() >= gameModel.getRows()
                || point.getCol() < 0 || point.getCol() >= gameModel.getCols()) {
            return true;
        }
        return !gameModel.getCell(point).hasAliveObject();
    }

    boolean isInvalid(@Nonnull GameModel gameModel,
                      @Nonnull Point mobPoint) throws UnknownObjectException {
        List<GameCharacter> gameCharacters = gameModel.getCharacters();
        List<Point> gameCharacterPoints = new ArrayList<>();
        for (GameCharacter gameCharacter : gameCharacters) {
            Point aliveObjectPoint = gameModel.getAliveObjectPoint(gameCharacter);
            gameCharacterPoints.add(aliveObjectPoint);
        }
        for (Point gameCharacterPoint : gameCharacterPoints) {
            if (isOutsideOfMap(gameCharacterPoint, gameModel)) {
                System.out.println(1);
                return true;
            }
            if (isOutsideOfMap(mobPoint, gameModel)) {
                System.out.println(2);
                return true;
            }
            AliveObject gameCharacter = gameModel.getCell(gameCharacterPoint).getAliveObject();
            if (!(gameCharacter instanceof GameCharacter)) {
                System.out.println(3);
                return true;
            }
            AliveObject aliveObject = gameModel.getCell(mobPoint).getAliveObject();
            if (!(aliveObject instanceof NonPlayerCharacter)) {
                System.out.println(4);
                return true;
            }
        }
        return false;
    }
}
