package ru.spb.hse.roguelike.model.object.alive;

import ru.spb.hse.roguelike.model.object.MeasurableCharacteristic;

/**
 * An object that can move on its own. It can act and has measurable characteristics.
 */
public abstract class AliveObject {
    private MeasurableCharacteristic health;
    private MeasurableCharacteristic power;

    AliveObject(MeasurableCharacteristic health, MeasurableCharacteristic power) {
        this.health = health;
        this.power = power;
    }

    public int getCurrentHealth() {
        return health.getCurrentValue();
    }

    public void setCurrentHealth(int x) {
        health.setCurrentValue(x);
    }

    public void setMaxHealth(int x) {
        health.setMaxValue(x);
    }

    public int getCurrentPower() {
        return power.getCurrentValue();
    }

    public void setCurrentPower(int x) {
        power.setCurrentValue(x);
    }

    public void setMaxPower(int x) {
        power.setMaxValue(x);
    }
}
