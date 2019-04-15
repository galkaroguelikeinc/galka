package ru.spb.hse.roguelike.controler.strategy;

import ru.spb.hse.roguelike.model.GameModel;
import ru.spb.hse.roguelike.model.map.Direction;
import ru.spb.hse.roguelike.Point;
import ru.spb.hse.roguelike.model.object.alive.GameCharacter;

import javax.annotation.Nonnull;

public class CowardlyStrategy extends MobStrategy {
    @Override
    public Point move(@Nonnull GameModel gameModel,
                      @Nonnull Point mobPoint) {
        if (isInvalid(gameModel, mobPoint)) {
            throw new SecurityException("unable to get mob from cell " + mobPoint);
        }
        GameCharacter gameCharacter = gameModel.getCharacter();
        Point gameCharacterPoint = gameModel.getAliveObjectPoint(gameCharacter);
        return getNewPosition(gameModel, mobPoint, gameCharacterPoint);
    }

    private Point getNewPosition(@Nonnull GameModel gameModel,
                                 @Nonnull Point mobPoint,
                                 @Nonnull Point gameCharacter) {
        Graph graph = Graph.of(gameModel);
        int initialDistance;
        try {
            initialDistance = graph.bfs(mobPoint, gameCharacter);
        } catch (StrategyException e) {
            return mobPoint;
        }
        for (Direction d : Direction.values()) {
            Point curPoint = new Point(mobPoint.getRow() + d.dx, mobPoint.getCol() + d.dy);
            int curDistance = 0;
            try {
                curDistance = graph.bfs(mobPoint, curPoint);
            } catch (StrategyException ignored) {
            }
            if (curDistance == initialDistance) {
                return curPoint;
            }
        }
        return mobPoint;
    }
}
