package ru.spb.hse.roguelike.model;

/**
 * Problem with objects usage.
 */
public class CannotDropWearableException extends Exception {
    public CannotDropWearableException(String message) {
        super(message);
    }
}
