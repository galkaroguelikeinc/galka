package ru.spb.hse.roguelike.model.object.alive;

import ru.spb.hse.roguelike.model.object.MeasurableCharacteristic;

/**
 * An object that can move on its own. It can act and has measurable characteristics.
 */
public abstract class AliveObject {
    private final MeasurableCharacteristic health;
    private final MeasurableCharacteristic power;

    AliveObject(MeasurableCharacteristic health, MeasurableCharacteristic power) {
        this.health = health;
        this.power = power;
    }

    public void setCurrentHealth(int x) {
        health.setCurrentValue(x);
    }

    public void setMaxHealth(int x) {
        health.setMaxValue(x);
    }

    public void setCurrentPower(int x) {
        power.setCurrentValue(x);
    }

    public void setMaxPower(int x) {
        power.setMaxValue(x);
    }
}
