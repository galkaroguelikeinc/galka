package ru.spb.hse.roguelike.model.map;

/**
 * Direction to move in terms of changing row and column.
 */
public enum Direction {
    LEFT(-1, 0),
    UP(0, 1),
    RIGHT(1, 0),
    DOWN(0, -1);

    public final int dRow;
    public final int dCol;

    Direction(int dRow, int dCol) {
        this.dRow = dRow;
        this.dCol = dCol;
    }
}