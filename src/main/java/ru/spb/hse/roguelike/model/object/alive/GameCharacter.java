package ru.spb.hse.roguelike.model.object.alive;

import ru.spb.hse.roguelike.model.object.MeasurableCharacteristic;

/**
 * Represents the game character (a character, that can be moved by player and use items)
 */
public class GameCharacter extends AliveObject {
    private MeasurableCharacteristic foodFullness;

    public GameCharacter() {
        super(new MeasurableCharacteristic(10), new MeasurableCharacteristic(1));
        foodFullness = new MeasurableCharacteristic(10);
    }

    public void setFoodFullness(int x) {
        foodFullness.setCurrentValue(x);
    }

    public void setMaxFoodFullness(int x) {
        foodFullness.setMaxValue(x);
    }

    public int getFoodFullness() {
        return foodFullness.getCurrentValue();
    }
}
