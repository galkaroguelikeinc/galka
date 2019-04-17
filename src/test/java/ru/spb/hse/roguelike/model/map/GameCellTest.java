package ru.spb.hse.roguelike.model.map;

import org.junit.Test;
import ru.spb.hse.roguelike.model.object.MeasurableCharacteristic;
import ru.spb.hse.roguelike.model.object.alive.NonPlayerCharacter;
import ru.spb.hse.roguelike.model.object.alive.NonPlayerCharacterStrategyType;
import ru.spb.hse.roguelike.model.object.items.Water;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests adding and removing objects in cells.
 */
public class GameCellTest {

    @Test
    public void itemTest() {
        GameCell gameCell = new GameCell(GameMapCellType.ROOM, null, null);
        assertFalse(gameCell.hasItem());
        gameCell.addItem(new Water());
        assertTrue(gameCell.hasItem());
        gameCell.takeCellItem();
        assertFalse(gameCell.hasItem());
    }

    @Test
    public void aliveObjectTest() {
        GameCell gameCell = new GameCell(GameMapCellType.TUNNEL, new NonPlayerCharacter(
                new MeasurableCharacteristic(1),
                new MeasurableCharacteristic(1),
                NonPlayerCharacterStrategyType.AGGRESSIVE),
                null);
        assertTrue(gameCell.hasAliveObject());
        gameCell.removeAliveObject();
        assertFalse(gameCell.hasAliveObject());
    }
}