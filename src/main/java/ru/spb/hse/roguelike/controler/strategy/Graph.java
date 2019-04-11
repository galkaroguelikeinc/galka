package ru.spb.hse.roguelike.controler.strategy;

import ru.spb.hse.roguelike.model.GameModel;

import ru.spb.hse.roguelike.model.map.Direction;
import ru.spb.hse.roguelike.model.map.Point;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static ru.spb.hse.roguelike.model.map.GameMapCellType.EMPTY;

public class Graph {
    private final Map<Point, List<Point>> adjacencyList;

    private Graph(@Nonnull Map<Point, List<Point>> adjacencyList) {
        this.adjacencyList = adjacencyList;
    }

    @Nonnull
    public static Graph of(GameModel gameModel) {
        final Map<Point, List<Point>> adjacencyList = new HashMap<>();
        for (int row = 0; row < gameModel.getRows(); row++) {
            for (int col = 0; col < gameModel.getCols(); col++) {
                Point curPoint = new Point(row, col);
                adjacencyList.put(curPoint, new ArrayList<>());
                for (Direction direction : Direction.values()) {
                    if (row + direction.dx >= 0 &&
                            row + direction.dx < gameModel.getRows() &&
                            col + direction.dy >= 0 &&
                            col + direction.dy < gameModel.getCols() &&
                            gameModel.getCell(row + direction.dx, col + direction.dy)
                                    .getGameMapCellType() != EMPTY) {
                        adjacencyList.get(curPoint).add(new Point(row + direction.dx,
                                col + direction.dy));
                    }
                }
            }
        }
        return new Graph(adjacencyList);
    }

    public int bfs(@Nonnull Point start,
                   @Nonnull Point finish) throws StrategyException {
        Map<Point, Boolean> visit = new HashMap<>();
        LinkedList<Point> queue = new LinkedList<>();
        Map<Point, Point> prev = new HashMap<>();
        queue.add(start);
        while (!queue.isEmpty()) {
            Point curPoint = queue.getFirst();
            visit.put(curPoint, true);
            if (curPoint.equals(finish)) {
                return getPath(prev, finish, start);
            }
            for (Point child : adjacencyList.get(curPoint)) {
                if (visit.containsKey(child) && visit.get(child)) {
                    continue;
                }
                prev.put(child, curPoint);
                queue.add(child);
            }
            queue.remove();
        }
        throw new StrategyException("failed to create bfs path");
    }

    private int getPath(@Nonnull Map<Point, Point> prev,
                        @Nonnull Point start,
                        @Nonnull Point finish) throws StrategyException {
        if (start.equals(finish)) {
            return 0;
        }
        if (!prev.containsKey(start)) {
            throw new StrategyException("Failed to get path for bfs");
        }
        return getPath(prev, prev.get(start), finish) + 1;
    }
}
