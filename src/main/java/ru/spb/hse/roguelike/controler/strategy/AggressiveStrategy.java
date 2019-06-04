package ru.spb.hse.roguelike.controler.strategy;

import ru.spb.hse.roguelike.Point;
import ru.spb.hse.roguelike.model.GameModel;
import ru.spb.hse.roguelike.model.UnknownObjectException;
import ru.spb.hse.roguelike.model.map.Direction;
import ru.spb.hse.roguelike.model.map.GameMapCellType;
import ru.spb.hse.roguelike.model.object.alive.GameCharacter;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class AggressiveStrategy extends NonPlayerCharacterStrategy {
    @Override
    public Point move(@Nonnull GameModel gameModel,
                      @Nonnull Point nonPlayerCharacterPoint) throws UnknownObjectException, StrategyException {
        List<GameCharacter> gameCharacters = gameModel.getCharacters();
        List<Point> gameCharacterPoints = new ArrayList<>();
        for (GameCharacter gameCharacter : gameCharacters) {
            Point aliveObjectPoint = gameModel.getAliveObjectPoint(gameCharacter);
            gameCharacterPoints.add(aliveObjectPoint);
        }
        if (isInvalid(gameModel, nonPlayerCharacterPoint)) {
            throw new StrategyException("Unable to get NPC from cell " + nonPlayerCharacterPoint);
        }
        try {
            return getNewPosition(gameModel, nonPlayerCharacterPoint, gameCharacterPoints);
        } catch (StrategyException e) {
            return nonPlayerCharacterPoint;
        }
    }


    private Point getNewPosition(@Nonnull GameModel gameModel,
                                 @Nonnull Point mobPoint,
                                 @Nonnull List<Point> gameCharacters) throws StrategyException {
        Graph graph = Graph.of(gameModel);
        Point pointWithMinDistance = mobPoint;
        int minDistance = 0;
        try {
            minDistance = graph.bfs(mobPoint, gameCharacters);
        } catch (StrategyException e) {
            return mobPoint;
        } catch (UnknownObjectException ignored) {
        }
        for (Direction d : Direction.values()) {
            Point curPoint = new Point(mobPoint.getRow() + d.dRow, mobPoint.getCol() + d.dCol);
            int curDistance = 0;
            try {
                curDistance = graph.bfs(curPoint,gameCharacters);
                if (hasNoNonPlayerCharacter(curPoint, gameModel) && curDistance < minDistance) {
                    minDistance = curDistance;
                    pointWithMinDistance = curPoint;
                }
            } catch (StrategyException ignored) {
                //path not exist
            } catch (UnknownObjectException ignored) {
            }
        }
        if (gameModel.getCell(pointWithMinDistance).getGameMapCellType() == GameMapCellType.EMPTY) {
            throw new StrategyException("Failed to move an aggressive NPC");
        }
        return pointWithMinDistance;
    }


}
