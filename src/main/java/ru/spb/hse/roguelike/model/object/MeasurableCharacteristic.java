package ru.spb.hse.roguelike.model.object;

/**
 * A characteristic of an alive object expressed as an integer.
 * <p>
 * A characteristic has the max capacity (the upper bound of the integer) expressing the most amount of the
 * characteristic an alive object can have and the current amount.
 * <p>
 * E.g. health, power, food-fullness.
 */
public class MeasurableCharacteristic {
    private int max;
    private int current;

    public MeasurableCharacteristic(int capacity) {
        max = capacity;
        current = max;
    }

    public void change(int x) {
        current = x;
        if (current > max) {
            current = max;
        }
        if (current < 0) {
            current = 0;
        }
    }

    public void changeMax(int x) {
        max = x;
    }

    public int getCurentValue() {
        return current;
    }
}
