package ru.spb.hse.roguelike.controler.strategy;

import org.junit.Test;
import ru.spb.hse.roguelike.Point;
import ru.spb.hse.roguelike.model.GameModel;
import ru.spb.hse.roguelike.model.UnknownObjectException;
import ru.spb.hse.roguelike.model.map.GameCell;
import ru.spb.hse.roguelike.model.map.GameMapCellType;
import ru.spb.hse.roguelike.model.object.alive.GameCharacter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;

public class GraphTest {
    @Test
    public void testCreation() throws StrategyException, UnknownObjectException {
        GameCell[][] map = new GameCell[6][5];
        map[0][0] = new GameCell(GameMapCellType.ROOM, new GameCharacter(), null);
        map[0][1] = new GameCell(GameMapCellType.ROOM, null, null);
        map[0][2] = new GameCell(GameMapCellType.EMPTY, null, null);
        map[0][3] = new GameCell(GameMapCellType.EMPTY, null, null);
        map[0][4] = new GameCell(GameMapCellType.EMPTY, null, null);

        map[1][0] = new GameCell(GameMapCellType.ROOM, null, null);
        map[1][1] = new GameCell(GameMapCellType.ROOM, null, null);
        map[1][2] = new GameCell(GameMapCellType.TUNNEL, null, null);
        map[1][3] = new GameCell(GameMapCellType.EMPTY, null, null);
        map[1][4] = new GameCell(GameMapCellType.EMPTY, null, null);

        map[2][0] = new GameCell(GameMapCellType.EMPTY, null, null);
        map[2][1] = new GameCell(GameMapCellType.EMPTY, null, null);
        map[2][2] = new GameCell(GameMapCellType.TUNNEL, null, null);
        map[2][3] = new GameCell(GameMapCellType.EMPTY, null, null);
        map[2][4] = new GameCell(GameMapCellType.EMPTY, null, null);

        map[3][0] = new GameCell(GameMapCellType.EMPTY, null, null);
        map[3][1] = new GameCell(GameMapCellType.EMPTY, null, null);
        map[3][2] = new GameCell(GameMapCellType.TUNNEL, null, null);
        map[3][3] = new GameCell(GameMapCellType.TUNNEL, null, null);
        map[3][4] = new GameCell(GameMapCellType.EMPTY, null, null);

        map[4][0] = new GameCell(GameMapCellType.EMPTY, null, null);
        map[4][1] = new GameCell(GameMapCellType.EMPTY, null, null);
        map[4][2] = new GameCell(GameMapCellType.EMPTY, null, null);
        map[4][3] = new GameCell(GameMapCellType.ROOM, null, null);
        map[4][4] = new GameCell(GameMapCellType.ROOM, null, null);

        map[5][0] = new GameCell(GameMapCellType.EMPTY, null, null);
        map[5][1] = new GameCell(GameMapCellType.EMPTY, null, null);
        map[5][2] = new GameCell(GameMapCellType.EMPTY, null, null);
        map[5][3] = new GameCell(GameMapCellType.ROOM, null, null);
        map[5][4] = new GameCell(GameMapCellType.ROOM, null, null);
        GameModel gameModel = new GameModel(map,
                new ArrayList<>(),
                new GameCharacter(),
                10);
        Graph graph = Graph.of(gameModel);
        assertEquals(5, graph.bfs(new Point(1, 1), Collections.singletonList(new Point(4, 3))));
        assertEquals(2, graph.bfs(new Point(0, 0), Collections.singletonList(new Point(1, 1))));
        assertEquals(9, graph.bfs(new Point(0, 0), Collections.singletonList(new Point(5, 4))));
    }

}