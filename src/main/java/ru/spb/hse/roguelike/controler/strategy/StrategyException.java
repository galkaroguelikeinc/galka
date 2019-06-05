package ru.spb.hse.roguelike.controler.strategy;

/**
 * Exception which is thrown when problems with strategy: it is not correct, not found, or not working.
 */
public class StrategyException extends Exception {
    StrategyException(String msg) {
        super(msg);
    }
}
