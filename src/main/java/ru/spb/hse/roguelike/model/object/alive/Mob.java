package ru.spb.hse.roguelike.model.object.alive;

import ru.spb.hse.roguelike.model.object.MeasurableCharacteristic;

import javax.annotation.Nonnull;

/**
 * Mob -- alive object, that can move and have fights with the game character.
 */

public class Mob extends AliveObject {
    private final MobStrategyType mobStrategyType;

    public Mob(@Nonnull MobStrategyType mobStrategyType) {
        super(new MeasurableCharacteristic(1), new MeasurableCharacteristic(1));
        this.mobStrategyType = mobStrategyType;
    }

    public MobStrategyType getMobStrategyType() {
        return mobStrategyType;
    }
}
