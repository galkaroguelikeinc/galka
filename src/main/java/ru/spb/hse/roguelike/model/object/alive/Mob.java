package ru.spb.hse.roguelike.model.object.alive;

import ru.spb.hse.roguelike.model.object.MeasurableCharacteristic;

import javax.annotation.Nonnull;

/**
 * Mob -- alive object, that can move and have fights with the game character.
 */

public class Mob extends NonPlayerCharacter {
    public Mob(@Nonnull MeasurableCharacteristic health,
               @Nonnull MeasurableCharacteristic power,
               @Nonnull NonPlayerCharacterStrategyType nonPlayerCharacterStrategyType) {
        super(health, power, nonPlayerCharacterStrategyType);
    }
}
