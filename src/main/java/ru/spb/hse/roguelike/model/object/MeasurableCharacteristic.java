package ru.spb.hse.roguelike.model.object;

public class MeasurableCharacteristic{
    private int max;
    private int current;

    public MeasurableCharacteristic(int capacity){
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
}
