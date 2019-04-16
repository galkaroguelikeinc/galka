package ru.spb.hse.roguelike.model.object.items;

import ru.spb.hse.roguelike.model.object.alive.GameCharacter;

/**
 * Increases food fullness.
 */
public abstract class Food extends Item {
    private int foodFullnessImprover;

    Food(int foodAmount) {
        foodFullnessImprover = foodAmount;
    }

    @Override
    public void apply(GameCharacter gameCharacter) {
        gameCharacter.setFoodFullness(gameCharacter.getFoodFullness() + foodFullnessImprover);
        super.decreaseNumber();
    }
}
