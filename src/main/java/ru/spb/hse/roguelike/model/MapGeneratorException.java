package ru.spb.hse.roguelike.model;

/**
 * Thrown if problems with map generator: wrong sizes etc.
 */
public class MapGeneratorException extends Exception {

    MapGeneratorException(String message) {
        super(message);
    }
}
