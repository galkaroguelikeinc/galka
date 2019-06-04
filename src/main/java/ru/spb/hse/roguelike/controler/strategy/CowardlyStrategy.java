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

public class CowardlyStrategy extends NonPlayerCharacterStrategy {
    @Override
    public Point move(@Nonnull GameModel gameModel,
                      @Nonnull Point nonPlayerCharacterPoint) throws UnknownObjectException, StrategyException {
        if (isInvalid(gameModel, nonPlayerCharacterPoint)) {
            throw new StrategyException("unable to get mob from cell " + nonPlayerCharacterPoint);
        }
        List<GameCharacter> gameCharacters = gameModel.getCharacters();
        List<Point> gameCharacterPoints = new ArrayList<>();
        for (GameCharacter gameCharacter : gameCharacters) {
            Point aliveObjectPoint = gameModel.getAliveObjectPoint(gameCharacter);
            gameCharacterPoints.add(aliveObjectPoint);
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
        int maxDistance;
        Point pointWithMaxDistance;
        maxDistance = -1;
        pointWithMaxDistance = mobPoint;
        for (Direction d : Direction.values()) {
            Point curPoint = new Point(mobPoint.getRow() + d.dRow, mobPoint.getCol() + d.dCol);
            int curDistance = 0;
            try {
                curDistance = graph.bfs(curPoint, gameCharacters);
                if (hasNoNonPlayerCharacter(curPoint, gameModel) && curDistance >= maxDistance) {
                    maxDistance = curDistance;
                    pointWithMaxDistance = curPoint;
                }
            } catch (Exception ignored) {
                //path not exist
            }
        }
        if (gameModel.getCell(pointWithMaxDistance).getGameMapCellType() == GameMapCellType.EMPTY) {
            throw new StrategyException("Failed  to move cowardly mob");
        }
        return pointWithMaxDistance;
    }


}
