package ru.spb.hse.roguelike.controler.strategy;

import ru.spb.hse.roguelike.Point;
import ru.spb.hse.roguelike.model.GameModel;
import ru.spb.hse.roguelike.model.UnknownObjectException;
import ru.spb.hse.roguelike.model.map.Direction;
import ru.spb.hse.roguelike.model.object.alive.GameCharacter;

import javax.annotation.Nonnull;

public class AggressiveStrategy extends MobStrategy {
    @Override
    public Point move(@Nonnull GameModel gameModel,
                      @Nonnull Point mobPoint) throws UnknownObjectException {
        GameCharacter gameCharacter = gameModel.getCharacter();
        Point gameCharacterPoint = gameModel.getAliveObjectPoint(gameCharacter);
        if (isInvalid(gameModel, mobPoint)) {
            throw new SecurityException("unable to get mob from cell " + mobPoint);
        }

        return getNewPosition(gameModel, mobPoint, gameCharacterPoint);
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
