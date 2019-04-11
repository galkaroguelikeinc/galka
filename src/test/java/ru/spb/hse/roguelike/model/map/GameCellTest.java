package ru.spb.hse.roguelike.model.map;

import org.junit.Test;
import ru.spb.hse.roguelike.model.object.alive.GameCharacter;
import ru.spb.hse.roguelike.model.object.alive.Mob;
import ru.spb.hse.roguelike.model.object.alive.MobStrategyType;
import ru.spb.hse.roguelike.model.object.items.Water;

import static org.junit.Assert.*;

/**
 * Tests adding and removing objects in cells.
 */
public class GameCellTest {

    @Test
    public void itemTest() {
        GameCell gameCell = new GameCell(GameMapCellType.ROOM, null, null);
        assertFalse(gameCell.hasItem());
        gameCell.addItem(new Water(new GameCharacter()));
        assertTrue(gameCell.hasItem());
        gameCell.takeCellItem();
        assertFalse(gameCell.hasItem());
    }

    @Test
    public void aliveObjectTest() {
        GameCell gameCell = new GameCell(GameMapCellType.TUNNEL, new Mob(MobStrategyType.AGGRESSIVE), null);
        assertTrue(gameCell.hasAliveObject());
        gameCell.removeAliveObject();
        assertFalse(gameCell.hasAliveObject());
    }
}