package ru.spb.hse.roguelike.controler.strategy;

import ru.spb.hse.roguelike.Point;
import ru.spb.hse.roguelike.model.GameModel;
import ru.spb.hse.roguelike.model.UnknownObjectException;
import ru.spb.hse.roguelike.model.map.Direction;
import ru.spb.hse.roguelike.model.map.GameMapCellType;
import ru.spb.hse.roguelike.model.object.alive.GameCharacter;

import javax.annotation.Nonnull;

public class AggressiveStrategy extends NonPlayerCharacterStrategy {
    @Override
    public Point move(@Nonnull GameModel gameModel,
                      @Nonnull Point nonPlayerCharacterPoint) throws UnknownObjectException {
        GameCharacter gameCharacter = gameModel.getCharacter();
        Point gameCharacterPoint = gameModel.getAliveObjectPoint(gameCharacter);
        if (isInvalid(gameModel, nonPlayerCharacterPoint)) {
            throw new SecurityException("Unable to get NPC from cell " + nonPlayerCharacterPoint);
        }
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
        Point pointWithMinDistance = mobPoint;
        int minDistance;
        try {
            minDistance = graph.bfs(mobPoint, gameCharacter);
        } catch (StrategyException e) {
            return mobPoint;
        }
        for (Direction d : Direction.values()) {
            Point curPoint = new Point(mobPoint.getRow() + d.dRow, mobPoint.getCol() + d.dCol);
            int curDistance = 0;
            try {
                curDistance = graph.bfs(curPoint, gameCharacter);
                if (!existsNonPlayerCharacter(curPoint, gameModel) && curDistance < minDistance) {
                    minDistance = curDistance;
                    pointWithMinDistance = curPoint;
                }
            } catch (StrategyException ignored) {
                //path not exist
            }
        }
        if (gameModel.getCell(pointWithMinDistance).getGameMapCellType() == GameMapCellType.EMPTY) {
            throw new StrategyException("Failed to move an aggressive NPC");
        }
        return pointWithMinDistance;
    }


}
