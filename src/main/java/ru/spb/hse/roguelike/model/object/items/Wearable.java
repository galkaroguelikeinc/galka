package ru.spb.hse.roguelike.model.object.items;

import ru.spb.hse.roguelike.model.object.alive.GameCharacter;

public class Wearable extends Item {
    // TODO: extend from an Item's superclass bc there can't be e.g. 2 dresses

    private final int powerDiff;

    public Wearable(int powerDiff) {
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
