package ru.spb.hse.roguelike.model;

/**
 * thrown if working with not existing object
 */
public class UnknownObjectException extends Exception {

    public UnknownObjectException(String message) {
        super(message);
    }
}
