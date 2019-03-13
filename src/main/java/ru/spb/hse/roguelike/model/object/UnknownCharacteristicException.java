package ru.spb.hse.roguelike.model.object;

public class UnknownCharacteristicException extends Exception {
    public UnknownCharacteristicException(String name) {
        super(name);
    }
}
