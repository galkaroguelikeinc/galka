package ru.spb.hse.roguelike.controler.strategy;

import ru.spb.hse.roguelike.model.GameModel;
import ru.spb.hse.roguelike.Point;
import ru.spb.hse.roguelike.model.object.alive.AliveObject;
import ru.spb.hse.roguelike.model.object.alive.GameCharacter;
import ru.spb.hse.roguelike.model.object.alive.Mob;

import javax.annotation.Nonnull;

abstract class MobStrategy {
    abstract Point move(@Nonnull GameModel gameModel,
               @Nonnull Point mobPoint) throws StrategyException;

    boolean isInvalid(@Nonnull GameModel gameModel,
                          @Nonnull Point mobPoint) {
        Point gameCharacterPoint = gameModel.getAliveObjectPoint(gameModel.getCharacter());
        if (!checkPoint(gameCharacterPoint, gameModel)) {
            System.out.println(1);
            return true;
        }
        if (!checkPoint(mobPoint, gameModel)) {
            System.out.println(2);
            return true;
        }
        AliveObject gameCharacter = gameModel.getCell(gameCharacterPoint).getAliveObject();
        if (!(gameCharacter instanceof GameCharacter)) {
            System.out.println(3);
            return true;
        }
        AliveObject aliveObject = gameModel.getCell(mobPoint).getAliveObject();
        if (!(aliveObject instanceof Mob)) {
            System.out.println(4);
            return true;
        }
        return false;
    }

    private boolean checkPoint(Point point, GameModel gameModel) {
        if (point.getRow() < 0 || point.getRow() >= gameModel.getRows()
                || point.getCol() < 0 || point.getCol() >= gameModel.getCols()) {
            return false;
        }
        return gameModel.getCell(point).hasAliveObject();
    }
}
