package ru.spb.hse.roguelike.model.object.alive;

import ru.spb.hse.roguelike.model.object.MeasurableCharacteristic;

import java.io.Serializable;

/**
 * An object that can move on its own. It can act and has measurable characteristics.
 */
public abstract class AliveObject implements Serializable {
    private MeasurableCharacteristic health;
    private MeasurableCharacteristic power;

    public AliveObject(MeasurableCharacteristic health, MeasurableCharacteristic power) {
        this.health = health;
        this.power = power;
    }

    public int getCurrentHealth() {
        return health.getCurrentValue();
    }

    public void setCurrentHealth(int x) {
        health.setCurrentValue(x);
    }

    public int getMaxHealth() {
        return health.getMaxValue();
    }

    public void setMaxHealth(int x) {
        health.setMaxValue(x);
    }

    public void increaseMaxHealth(int diff) {
        health.increaseCurrentValue(diff);
    }

    public int getCurrentPower() {
        return power.getCurrentValue();
    }

    public void setCurrentPower(int x) {
        power.setCurrentValue(x);
    }

    public void increaseCurrentPower(int diff) {
        power.increaseCurrentValue(diff);
    }

    public int getMaxPower() {
        return power.getMaxValue();
    }

    public void setMaxPower(int x) {
        power.setMaxValue(x);
    }
}
