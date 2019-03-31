package ru.spb.hse.roguelike.exceptions;

/**
 * Thrown if problems with map generator: wrong sizes etc.
 */
public class MapGeneratorException extends Exception {
    public MapGeneratorException(String msg) {
        super(msg);
    }
}
