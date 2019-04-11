package ru.spb.hse.roguelike.controler;

import org.junit.Test;
import ru.spb.hse.roguelike.model.MapGeneratorException;
import ru.spb.hse.roguelike.model.GameModel;
import ru.spb.hse.roguelike.model.Generator;
import ru.spb.hse.roguelike.model.map.GameMapCellType;
import ru.spb.hse.roguelike.view.Command;
import ru.spb.hse.roguelike.view.View;
import ru.spb.hse.roguelike.view.ViewException;

import static org.junit.Assert.*;

/**
 * Tests changing game character positions when command from view received.
 */
public class ControllerTest {

    @Test
    public void moveLeftTest() throws MapGeneratorException, ViewException {
        GameModel gameModel = new Generator().generateModel(1, 10, 10);
        gameModel.getCell(0, 3).setGameMapCellType(GameMapCellType.ROOM);
        gameModel.getCell(0, 2).setGameMapCellType(GameMapCellType.ROOM);
        gameModel.moveAliveObject(gameModel.getCharacter(), 0, 3);

        Controller controller = new Controller(new View() {
            @Override
            public void showChanges(int row, int col) {
            }

            @Override
            public Command readCommand() {
                return Command.LEFT;
            }
        }, gameModel);

        controller.executeCommand();
        assertEquals(new Integer(0), gameModel.getAliveObjectRow(gameModel.getCharacter()));
        assertEquals(new Integer(2), gameModel.getAliveObjectCol(gameModel.getCharacter()));
    }
}