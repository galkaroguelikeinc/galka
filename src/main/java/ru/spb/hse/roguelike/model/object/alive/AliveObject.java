package ru.spb.hse.roguelike.model.object.alive;

import ru.spb.hse.roguelike.model.object.MeasurableCharacteristic;

/**
 * And object^ that can be moved.
 */
public abstract class AliveObject{
    private static final int DEFAULT_HEALTH = 1;
    private static final int DEFAULT_POWER = 1;
    private int xPos;
    private int yPos;
    private MeasurableCharacteristic health;
    private MeasurableCharacteristic power;

    public void changeHealth(int x) {
        health.change(x);
    }

    public  void changeMaxHealth(int x) {
        health.changeMax(x);
    }
    public void changePower(int x) {
        power.change(x);
    }

    public  void changeMaxPower(int x) {
        power.changeMax(x);
    }

    AliveObject(int x, int y) {
        xPos = x;
        yPos = y;
        health = new MeasurableCharacteristic(DEFAULT_HEALTH);
        power = new MeasurableCharacteristic(DEFAULT_POWER);
    }

    public void move(int newXPos, int newYPos) {
        xPos = newXPos;
        yPos = newYPos;
    }

    public int getXPos() {
        return xPos;
    }

    public int getYPos() {
        return yPos;
    }
}
