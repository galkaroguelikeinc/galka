package ru.spb.hse.roguelike.model.object.items;

import ru.spb.hse.roguelike.model.object.alive.GameCharacter;

/**
 * Increases food fullness.
 */
public abstract class Food extends Item {
    private int foodFullnessImprover;

    Food(GameCharacter gameCharacter, int foodAmount) {
        super(gameCharacter);
        foodFullnessImprover = foodAmount;
    }

    @Override
    public void use() {
        getGameCharacter().changeFoodFullness(foodFullnessImprover);
    }
}
