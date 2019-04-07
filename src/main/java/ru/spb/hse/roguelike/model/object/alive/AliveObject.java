package ru.spb.hse.roguelike.model.object.alive;

import ru.spb.hse.roguelike.model.object.MeasurableCharacteristic;

/**
 * An object that can move on its own. It can act and has measurable characteristics.
 */
public abstract class AliveObject {
    private static final int DEFAULT_HEALTH = 1;
    private static final int DEFAULT_POWER = 1;
    private final MeasurableCharacteristic health;
    private final MeasurableCharacteristic power;

    public void changeHealth(int x) {
        health.change(x);
    }

    public void changeMaxHealth(int x) {
        health.changeMax(x);
    }

    public void changePower(int x) {
        power.change(x);
    }

    public int getPower() {
        //return power.;
    }

    public void changeMaxPower(int x) {
        power.changeMax(x);
    }

    AliveObject() {
        health = new MeasurableCharacteristic(DEFAULT_HEALTH);
        power = new MeasurableCharacteristic(DEFAULT_POWER);
    }
}
