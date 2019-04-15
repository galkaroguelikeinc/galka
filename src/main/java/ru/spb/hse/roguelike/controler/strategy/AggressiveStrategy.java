package ru.spb.hse.roguelike.controler.strategy;

import ru.spb.hse.roguelike.Point;
import ru.spb.hse.roguelike.model.GameModel;
import ru.spb.hse.roguelike.model.map.Direction;
import ru.spb.hse.roguelike.model.object.alive.AliveObject;
import ru.spb.hse.roguelike.model.object.alive.GameCharacter;
import ru.spb.hse.roguelike.model.object.alive.Mob;
import ru.spb.hse.roguelike.model.object.alive.MobStrategyType;

import javax.annotation.Nonnull;

public class AggressiveStrategy implements MobStrategy {
    @Override
    public Point move(@Nonnull GameModel gameModel,
                      @Nonnull Point mobPoint) {
        GameCharacter gameCharacter = gameModel.getCharacter();
        Point gameCharacterPoint = gameModel.getAliveObjectPoint(gameCharacter);
        if (!valid(gameModel, mobPoint, gameCharacterPoint)) {
            throw new SecurityException("unable to get mob from cell " + mobPoint);
        }

        return getNewPosition(gameModel, mobPoint, gameCharacterPoint);
    }

    private boolean valid(@Nonnull GameModel gameModel,
                          @Nonnull Point mobPoint,
                          @Nonnull Point gameCharacterPoint) {
        if (mobPoint.getRow() < 0 || mobPoint.getRow() >= gameModel.getRows()
                || mobPoint.getCol() < 0 || mobPoint.getCol() >= gameModel.getCols()) {
            System.out.println(1);
            return false;
        }
        if (!gameModel.getCell(mobPoint).hasAliveObject()) {
            System.out.println(2);
            return false;
        }
        if (gameCharacterPoint.getRow() < 0 || gameCharacterPoint.getRow() >= gameModel.getRows()
                || gameCharacterPoint.getCol() < 0 || gameCharacterPoint.getCol() >= gameModel.getCols()) {
            System.out.println(3);
            return false;
        }
        if (!gameModel.getCell(gameCharacterPoint).hasAliveObject()) {
            System.out.println(4);
            return false;
        }
        AliveObject gameCharacter = gameModel.getCell(gameCharacterPoint).getAliveObject();
        if (!(gameCharacter instanceof GameCharacter)) {
            System.out.println(5);
            return false;
        }
        AliveObject aliveObject = gameModel.getCell(mobPoint).getAliveObject();
        if (!(aliveObject instanceof Mob)) {
            System.out.println(6);
            return false;
        }
        Mob mob = (Mob) aliveObject;
        return mob.getMobStrategyType() == MobStrategyType.AGGRESSIVE;
    }

    private Point getNewPosition(@Nonnull GameModel gameModel,
                                 @Nonnull Point mobPoint,
                                 @Nonnull Point gameCharacter) {
        Graph graph = Graph.of(gameModel);
        Point pointWithMinDistance = mobPoint;
        int minDistance;
        try {
            minDistance = graph.bfs(mobPoint, gameCharacter);
        } catch (StrategyException e) {
            return mobPoint;
        }
        for (Direction d : Direction.values()) {
            Point curPoint = new Point(mobPoint.getRow() + d.dx, mobPoint.getCol() + d.dy);
            int curDistance = 0;
            try {
                curDistance = graph.bfs(curPoint, gameCharacter);
                if (curDistance < minDistance) {
                    minDistance = curDistance;
                    pointWithMinDistance = curPoint;
                }
            } catch (StrategyException ignored) {
            }
        }
        return pointWithMinDistance;
    }
}
