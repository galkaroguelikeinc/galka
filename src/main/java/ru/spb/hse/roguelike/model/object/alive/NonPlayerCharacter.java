package ru.spb.hse.roguelike.model.object.alive;

import ru.spb.hse.roguelike.model.object.MeasurableCharacteristic;

import javax.annotation.Nonnull;

public class NonPlayerCharacter extends AliveObject {
    private final NonPlayerCharacterStrategyType nonPlayerCharacterStrategyType;

    public NonPlayerCharacter(@Nonnull MeasurableCharacteristic health,
                              @Nonnull MeasurableCharacteristic power,
                              @Nonnull NonPlayerCharacterStrategyType nonPlayerCharacterStrategyType) {
        super(health, power);
        this.nonPlayerCharacterStrategyType = nonPlayerCharacterStrategyType;
    }

    public NonPlayerCharacterStrategyType getNonPlayerCharacterStrategyType() {
        return nonPlayerCharacterStrategyType;
    }
}
