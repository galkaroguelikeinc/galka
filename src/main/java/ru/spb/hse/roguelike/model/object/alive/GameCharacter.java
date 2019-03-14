package ru.spb.hse.roguelike.model.object.alive;

import ru.spb.hse.roguelike.model.object.MeasurableCharacteristic;

/**
 * Represents the game character (a character, that can be moved by player and use items)
 */
public class GameCharacter extends AliveObject {
    private MeasurableCharacteristic hunger;

    public GameCharacter(int x, int y)  {
        super(x, y);
        hunger = new MeasurableCharacteristic(10);
        changeMaxHealth(10);
    }

    public void changeHunger(int x) {
        hunger.change(x);
    }

    public void changeMaxHunger(int x) {
        hunger.changeMax(x);
    }
}
