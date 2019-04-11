package ru.spb.hse.roguelike.model.object.alive;

import ru.spb.hse.roguelike.model.object.MeasurableCharacteristic;

/**
 * Represents the game character (a character, that can be moved by player and use items)
 */
public class GameCharacter extends AliveObject {
    private MeasurableCharacteristic foodFullness;

    public GameCharacter() {
        foodFullness = new MeasurableCharacteristic(10);
        changeMaxHealth(10);
        changeHealth(10);
    }

    public void changeFoodFullness(int x) {
        foodFullness.change(x);
    }

    public void changeMaxFoodFullness(int x) {
        foodFullness.changeMax(x);
    }

    public int getFoodFullness() {
        return foodFullness.getCurentValue();
    }
}
