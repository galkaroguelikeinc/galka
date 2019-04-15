package ru.spb.hse.roguelike.model.object;

/**
 * A characteristic of an alive object expressed as an integer.
 * <p>
 * A characteristic has the maxValue capacity (the upper bound of the integer) expressing the most amount of the
 * characteristic an alive object can have and the currentValue amount.
 * <p>
 * E.g. health, power, food-fullness.
 */
public class MeasurableCharacteristic {
    private int maxValue;
    private int currentValue;

    public MeasurableCharacteristic(int maxValue) {
        this.maxValue = maxValue;
        currentValue = this.maxValue;
    }

    public void setMaxValue(int newMaxValue) {
        maxValue = newMaxValue;
    }

    public int getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(int newValue) {
        currentValue = Math.max(0, Math.min(maxValue, newValue));
    }
}
