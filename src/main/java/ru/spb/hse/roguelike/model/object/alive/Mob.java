package ru.spb.hse.roguelike.model.object.alive;

import javax.annotation.Nonnull;

/**
 * Mob -- alive object, that can move and have fights with the game character.
 */
public abstract class Mob extends AliveObject {
    private final MobStrategyType mobStrategyType;

    protected Mob(@Nonnull MobStrategyType mobStrategyType) {
        this.mobStrategyType = mobStrategyType;
    }

    public MobStrategyType getMobStrategyType() {
        return mobStrategyType;
    }
}
