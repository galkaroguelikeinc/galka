package ru.spb.hse.roguelike.model.object.alive;

import ru.spb.hse.roguelike.model.object.MeasurableCharacteristic;

/**
 * Mob -- alive object, that can move and have fights with the game character.
 */
public abstract class Mob extends AliveObject {
    public Mob() {
        super(new MeasurableCharacteristic(1), new MeasurableCharacteristic(1));
    }
}
