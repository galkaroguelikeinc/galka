package ru.spb.hse.roguelike.model.object.alive;

import ru.spb.hse.roguelike.model.object.MeasurableCharacteristic;

public class ConfusedNonPlayerCharacter extends NonPlayerCharacter {

    public ConfusedNonPlayerCharacter(NonPlayerCharacter nonPlayerCharacter) {
        super(new MeasurableCharacteristic(nonPlayerCharacter.getMaxHealth()),
                new MeasurableCharacteristic(nonPlayerCharacter.getMaxPower()),
                NonPlayerCharacterStrategyType.RANDOM);
    }
}
