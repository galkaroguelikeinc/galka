package ru.spb.hse.roguelike.model.object.items;

import org.junit.Test;
import ru.spb.hse.roguelike.model.object.alive.GameCharacter;

import static org.junit.Assert.*;

/**
 * Tests if Water item changes game character characteristic.
 */
public class WaterTest {

    @Test
    public void useTest() {
        GameCharacter gameCharacter = new GameCharacter();
        gameCharacter.changeFoodFullness(0);
        Water water = new Water(gameCharacter);
        water.increaseNumber();
        water.use();
        assertEquals(2, gameCharacter.getFoodFullness());
    }
}