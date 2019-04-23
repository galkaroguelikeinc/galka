package ru.spb.hse.roguelike.model;

public class CannotDropWearableException extends Exception {
    public CannotDropWearableException(String message) {
        super(message);
    }
}
