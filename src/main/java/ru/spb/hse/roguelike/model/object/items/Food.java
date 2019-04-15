package ru.spb.hse.roguelike.model.object.items;

import ru.spb.hse.roguelike.model.object.alive.GameCharacter;

/**
 * Increases food fullness.
 */
public abstract class Food extends Item {
    private int foodValue;

    Food(GameCharacter gameCharacter, int foodValue) {
        super(gameCharacter);
        this.foodValue = foodValue;
    }

    @Override
    public void use() {
        getGameCharacter().setFoodFullness(foodValue * super.getItemNumber());
    }
}
