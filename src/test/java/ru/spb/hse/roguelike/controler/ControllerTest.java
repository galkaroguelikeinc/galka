package ru.spb.hse.roguelike.controler;

import org.junit.Test;
import ru.spb.hse.roguelike.Point;
import ru.spb.hse.roguelike.model.GameModel;
import ru.spb.hse.roguelike.model.Generator;
import ru.spb.hse.roguelike.model.MapGeneratorException;
import ru.spb.hse.roguelike.model.UnknownObjectException;
import ru.spb.hse.roguelike.model.map.GameCellException;
import ru.spb.hse.roguelike.model.map.GameMapCellType;
import ru.spb.hse.roguelike.view.CommandName;
import ru.spb.hse.roguelike.view.CommandNameId;
import ru.spb.hse.roguelike.view.View;
import ru.spb.hse.roguelike.view.ViewException;

import static org.junit.Assert.assertEquals;

/**
 * Tests changing game character positions when command from view received.
 */
public class ControllerTest {

    @Test
    public void moveLeftTest() throws MapGeneratorException, ViewException, UnknownObjectException, GameCellException {
        for (int k = 0; k < 1000; k++) {
            GameModel gameModel = new Generator().generateModel(3, 20, 25);
            gameModel.getCell(new Point(0, 3)).setGameMapCellType(GameMapCellType.ROOM);
            gameModel.getCell(new Point(0, 2)).setGameMapCellType(GameMapCellType.ROOM);
            gameModel.moveAliveObject(gameModel.getCharacters().get(0), new Point(0, 3));

            Controller controller = new Controller(new View() {
                @Override
                public void showChanges(Point point) {
                }

                @Override
                public CommandNameId readCommand() {
                    return new CommandNameId(CommandName.LEFT, 0);
                }

                @Override
                public void end() {
                }
            }, gameModel);

            controller.executeCommand();
            assertEquals(0, gameModel.getAliveObjectPoint(gameModel.getCharacters().get(0)).getRow());
            assertEquals(2, gameModel.getAliveObjectPoint(gameModel.getCharacters().get(0)).getCol());
        }
    }
}