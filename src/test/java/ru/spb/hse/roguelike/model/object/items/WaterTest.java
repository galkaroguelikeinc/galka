package ru.spb.hse.roguelike.model.object.items;

import org.junit.Test;
import ru.spb.hse.roguelike.model.object.alive.GameCharacter;

import static org.junit.Assert.assertEquals;

/**
 * Tests if WaterFood item changes game character characteristic.
 */
public class WaterTest {

    @Test
    public void useTest() {
        GameCharacter gameCharacter = new GameCharacter();
        gameCharacter.setFoodFullness(0);
        WaterFood water = new WaterFood();
        water.increaseNumber();
        water.apply(gameCharacter);
        assertEquals(1, gameCharacter.getFoodFullness());
        water.apply(gameCharacter);
        assertEquals(2, gameCharacter.getFoodFullness());
    }
}