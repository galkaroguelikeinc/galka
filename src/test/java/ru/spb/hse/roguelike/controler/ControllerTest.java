package ru.spb.hse.roguelike.controler;

import org.junit.Test;
import ru.spb.hse.roguelike.Point;
import ru.spb.hse.roguelike.model.GameModel;
import ru.spb.hse.roguelike.model.Generator;
import ru.spb.hse.roguelike.model.MapGeneratorException;
import ru.spb.hse.roguelike.model.UnknownObjectException;
import ru.spb.hse.roguelike.model.map.GameMapCellType;
import ru.spb.hse.roguelike.view.Command;
import ru.spb.hse.roguelike.view.View;
import ru.spb.hse.roguelike.view.ViewException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests changing game character positions when command from view received.
 */
public class ControllerTest {

    @Test
    public void moveLeftTest() throws MapGeneratorException, ViewException, UnknownObjectException {
        for (int k = 0; k < 1000; k++) {
            GameModel gameModel = new Generator().generateModel(3, 20, 25);
            gameModel.getCell(new Point(0, 3)).setGameMapCellType(GameMapCellType.ROOM);
            gameModel.getCell(new Point(0, 2)).setGameMapCellType(GameMapCellType.ROOM);
            gameModel.moveAliveObject(gameModel.getCharacter(), new Point(0, 3));

            Controller controller = new Controller(new View() {
                @Override
                public void showChanges(Point point) {
                }

                @Override
                public Command readCommand() {
                    return Command.LEFT;
                }

                @Override
                public void end() {
                }
            }, gameModel);

            controller.executeCommand();
            assertEquals(0, gameModel.getAliveObjectPoint(gameModel.getCharacter()).getRow());
            assertEquals(2, gameModel.getAliveObjectPoint(gameModel.getCharacter()).getCol());
        }
    }
}