package ru.spb.hse.roguelike.model.object;

public class MeasurableCharacteristic implements Characteristic {
    private int max;
    private int current;

    MeasurableCharacteristic(int capacity){
        max = capacity;
        current = max;
    }
    
    public boolean decrease(int x) {
        current -= x;
        return current > 0;
    }

    public void increase(int x) {
        current += x;
        if (current > max) {
            current = max;
        }
    }

    public void increaseMax(int x) {
        max = x;
    }
}
