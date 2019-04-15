package ru.spb.hse.roguelike.model.object.alive;

import ru.spb.hse.roguelike.model.object.MeasurableCharacteristic;

/**
 * Represents the game character (a character, that can be moved by player and use items)
 */
public class GameCharacter extends AliveObject {
    private MeasurableCharacteristic foodFullness;

    public GameCharacter() {
        super(new MeasurableCharacteristic(1), new MeasurableCharacteristic(1));
        foodFullness = new MeasurableCharacteristic(10);
        setMaxHealth(10);
    }

    public void changeFoodFullness(int x) {
        foodFullness.setCurrentValue(x);
    }

    public void changeMaxFoodFullness(int x) {
        foodFullness.setMaxValue(x);
    }

    public int getFoodFullness() {
        return foodFullness.getCurrentValue();
    }
}
