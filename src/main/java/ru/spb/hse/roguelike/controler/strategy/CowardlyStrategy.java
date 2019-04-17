package ru.spb.hse.roguelike.controler.strategy;

import ru.spb.hse.roguelike.Point;
import ru.spb.hse.roguelike.model.GameModel;
import ru.spb.hse.roguelike.model.UnknownObjectException;
import ru.spb.hse.roguelike.model.map.Direction;
import ru.spb.hse.roguelike.model.map.GameMapCellType;
import ru.spb.hse.roguelike.model.object.alive.GameCharacter;

import javax.annotation.Nonnull;

public class CowardlyStrategy extends NonPlayerCharacterStrategy {
    @Override
    public Point move(@Nonnull GameModel gameModel,
                      @Nonnull Point nonPlayerCharacterPoint) throws UnknownObjectException {
        if (isInvalid(gameModel, nonPlayerCharacterPoint)) {
            throw new SecurityException("unable to get mob from cell " + nonPlayerCharacterPoint);
        }
        GameCharacter gameCharacter = gameModel.getCharacter();
        Point gameCharacterPoint = gameModel.getAliveObjectPoint(gameCharacter);
        try {
            return getNewPosition(gameModel, nonPlayerCharacterPoint, gameCharacterPoint);
        } catch (StrategyException e) {
            return nonPlayerCharacterPoint;
        }
    }

    private Point getNewPosition(@Nonnull GameModel gameModel,
                                 @Nonnull Point mobPoint,
                                 @Nonnull Point gameCharacter) throws StrategyException {
        Graph graph = Graph.of(gameModel);
        int maxDistance;
        Point pointWithMaxDistance;
        maxDistance = -1;
        pointWithMaxDistance = mobPoint;
        for (Direction d : Direction.values()) {
            Point curPoint = new Point(mobPoint.getRow() + d.dRow, mobPoint.getCol() + d.dCol);
            int curDistance = 0;
            try {
                curDistance = graph.bfs(curPoint, gameCharacter);
                if (!existsNonPlayerCharacter(curPoint, gameModel) && curDistance >= maxDistance) {
                    maxDistance = curDistance;
                    pointWithMaxDistance = curPoint;
                }
            } catch (StrategyException ignored) {
                //path not exist
            }
        }
        if (gameModel.getCell(pointWithMaxDistance).getGameMapCellType() == GameMapCellType.EMPTY) {
            throw new StrategyException("Failed  to move cowardly mob");
        }
        return pointWithMaxDistance;
    }


}
