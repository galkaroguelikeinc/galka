package ru.spb.hse.roguelike.controler.strategy;

import ru.spb.hse.roguelike.model.GameModel;
import ru.spb.hse.roguelike.model.map.Direction;
import ru.spb.hse.roguelike.model.map.GameCell;
import ru.spb.hse.roguelike.model.map.Point;
import ru.spb.hse.roguelike.model.object.alive.AliveObject;
import ru.spb.hse.roguelike.model.object.alive.GameCharacter;
import ru.spb.hse.roguelike.model.object.alive.Mob;
import ru.spb.hse.roguelike.model.object.alive.MobStrategyType;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class CowardlyStrategy implements MobStrategy {
    @Override
    public void move(@Nonnull GameModel gameModel,
                     @Nonnull Point mobPoint) {
        if (!valid(gameModel, mobPoint)) {
            throw new SecurityException("unable to get mob from cell " + mobPoint);
        }
        List<Point> gameCharacterPoints = findGameCharacter(gameModel);
        if (gameCharacterPoints.size() == 0) {
            throw new SecurityException("unable to find gameCharacter");
        }
    }

    private boolean valid(@Nonnull GameModel gameModel,
                          @Nonnull Point mobPoint) {
        if (mobPoint.getRow() < 0 || mobPoint.getRow() >= gameModel.getRows()
                || mobPoint.getCol() < 0 || mobPoint.getCol() >= gameModel.getCols()) {
            return false;
        }
        if (!gameModel.getCell(mobPoint.getRow(), mobPoint.getCol()).hasAliveObject()) {
            return false;
        }
        AliveObject aliveObject = gameModel.getCell(mobPoint.getRow(), mobPoint.getCol()).getAliveObject();
        if (!(aliveObject instanceof Mob)) {
            return false;
        }
        Mob mob = (Mob) aliveObject;
        return mob.getMobStrategyType() == MobStrategyType.COWARDLY;
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

    @Nonnull
    private List<Point> findGameCharacter(GameModel gameModel) {
        List<Point> result = new ArrayList<>();
        for (int row = 0; row < gameModel.getRows(); row++) {
            for (int col = 0; col < gameModel.getRows(); col++) {
                GameCell cell = gameModel.getCell(row, col);
                if (cell.hasAliveObject()
                        && cell.getAliveObject() instanceof GameCharacter) {
                    result.add(new Point(row, col));
                }
            }
        }
        return result;
    }
}
