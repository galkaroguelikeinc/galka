package ru.spb.hse.roguelike.model.object.items;

import org.junit.Test;
import ru.spb.hse.roguelike.model.object.alive.GameCharacter;

import static org.junit.Assert.assertEquals;

/**
 * Tests if a WaterFood item changes game character's characteristic.
 */
public class WaterTest {

    @Test
    public void useTest() throws CannotApplyFoodMultipleTimesException {
        GameCharacter gameCharacter = new GameCharacter();
        gameCharacter.setFoodFullness(0);
        WaterFood water = new WaterFood();
        water.apply(gameCharacter);
        assertEquals(1, gameCharacter.getFoodFullness());
        WaterFood newWater = new WaterFood();
        newWater.apply(gameCharacter);
        assertEquals(2, gameCharacter.getFoodFullness());
    }
}