package ru.spb.hse.roguelike.model.object.alive;

import ru.spb.hse.roguelike.model.object.MeasurableCharacteristic;

/**
 * Represents the game character (a character, that can be moved by player and use items)
 */
public class GameCharacter extends AliveObject {
    private MeasurableCharacteristic food_fullness;

    public GameCharacter() {
        food_fullness = new MeasurableCharacteristic(10);
        changeMaxHealth(10);
    }

    public void changeFoodFullness(int x) {
        food_fullness.change(x);
    }

    public void changeMaxFoodFullness(int x) {
        food_fullness.changeMax(x);
    }

    public int getFoodFullness() {
        return food_fullness.getCurentValue();
    }
}
