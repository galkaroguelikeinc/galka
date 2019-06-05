package ru.spb.hse.roguelike.model.object.items;

import ru.spb.hse.roguelike.model.object.alive.GameCharacter;

/**
 * Increases power.
 */
public abstract class Wearable extends Item {
    private final int powerDiff;

    Wearable(int powerDiff) {
        this.powerDiff = powerDiff;
    }

    @Override
    public void apply(GameCharacter gameCharacter) {
        gameCharacter.increaseCurrentPower(powerDiff);
        gameCharacter.pushWearable(this);
    }

    public void unapply(GameCharacter gameCharacter) {
        gameCharacter.increaseCurrentPower(-powerDiff);
        gameCharacter.popWearable();
    }
}
