package ru.spb.hse.roguelike.view;

import org.junit.Test;
import ru.spb.hse.roguelike.model.GameModel;
import ru.spb.hse.roguelike.model.map.GameCell;
import ru.spb.hse.roguelike.model.map.GameMapCellType;
import ru.spb.hse.roguelike.model.object.alive.GameCharacter;

import java.util.ArrayList;

public class TerminalViewTest {
    @Test
    public void simpleUsage() throws ViewException, InterruptedException {
        GameCell[][] gameMap = new GameCell[2][3];
        gameMap[0][0] = new GameCell(GameMapCellType.ROOM, null, null);
        gameMap[0][1] = new GameCell(GameMapCellType.ROOM, null, null);
        gameMap[0][2] = new GameCell(GameMapCellType.TUNNEL, null, null);
        gameMap[1][0] = new GameCell(GameMapCellType.EMPTY, null, null);
        gameMap[1][1] = new GameCell(GameMapCellType.ROOM, null, null);
        gameMap[1][2] = new GameCell(GameMapCellType.ROOM, new GameCharacter(), null);
        GameModel gameModel = new GameModel(gameMap, new ArrayList<>(), null, 10);
        // should print in the upper-left corner:
        // ..#
        //  .&
        TerminalView terminalView = new TerminalView(gameModel);
        Thread.sleep(1000);
    }
}