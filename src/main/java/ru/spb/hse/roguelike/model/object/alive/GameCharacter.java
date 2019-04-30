package ru.spb.hse.roguelike.model.object.alive;

import ru.spb.hse.roguelike.model.object.MeasurableCharacteristic;
import ru.spb.hse.roguelike.model.object.items.Wearable;

import java.util.Stack;

/**
 * Represents the game character (a character, that can be moved by player and use items)
 */
public class GameCharacter extends AliveObject {
    private MeasurableCharacteristic foodFullness;
    private Stack<Wearable> wearableStack = new Stack<>();
    private int experiencePoints = 10;

    public GameCharacter() {
        super(new MeasurableCharacteristic(10), new MeasurableCharacteristic(1));
        foodFullness = new MeasurableCharacteristic(10);
    }

    public void setMaxFoodFullness(int x) {
        foodFullness.setMaxValue(x);
    }

    public int getMaxFoodFullness() {
        return foodFullness.getMaxValue();
    }

    public int getFoodFullness() {
        return foodFullness.getCurrentValue();
    }

    public void setFoodFullness(int x) {
        foodFullness.setCurrentValue(x);
    }

    public void pushWearable(Wearable wearable) {
        wearableStack.push(wearable);
    }

    public boolean hasWearable() {
        return !wearableStack.empty();
    }

    public Wearable peekWearable() {
        return wearableStack.peek();
    }

    public void popWearable() {
        wearableStack.pop();
    }

    public int getExperiencePoints() {
        return experiencePoints;
    }

    private boolean isLevelUp(int oldXP, int newXP) {
        return getLevel(oldXP) > getLevel(newXP);
    }

    private int calculateNewMaxValue(int currentMaxValue, int oldXP, int newXP) {
        return currentMaxValue / getLevel(oldXP) * getLevel(newXP);
    }

    public void addExperiencePoints(int points) {
        int oldXP = experiencePoints;
        int newXP = experiencePoints + points;
        experiencePoints += points;
        if (isLevelUp(oldXP, newXP)) {
            setMaxHealth(calculateNewMaxValue(getMaxHealth(), oldXP, newXP));
            setMaxPower(calculateNewMaxValue(getMaxPower(), oldXP, newXP));
            setMaxFoodFullness(calculateNewMaxValue(getMaxFoodFullness(), oldXP, newXP));
        }
    }

    private int getLevel(int experiencePoints) {
        return experiencePoints / 10;
    }

    public int getLevel() {
        return getLevel(experiencePoints);
    }
}
