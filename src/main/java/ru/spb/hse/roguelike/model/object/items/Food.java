package ru.spb.hse.roguelike.model.object.items;

import ru.spb.hse.roguelike.model.object.alive.GameCharacter;

/**
 * Increases food fullness.
 */
public abstract class Food extends Item {
    private int foodFullnessImprover;
    private boolean used = false;

    Food(int foodAmount) {
        foodFullnessImprover = foodAmount;
    }

    @Override
    public void apply(GameCharacter gameCharacter) throws CannotApplyFoodMultipleTimesException {
        if (used) {
            throw new CannotApplyFoodMultipleTimesException();
        }
        gameCharacter.setFoodFullness(gameCharacter.getFoodFullness() + foodFullnessImprover);
        used = true;
    }

    public int getPower() {
        return foodFullnessImprover;
    }
}
