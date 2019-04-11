package ru.spb.hse.roguelike.model.map;

public enum Direction {
    LEFT(-1, 0),
    UP(0, 1),
    RIGHT(1, 0),
    DOWN(0, -1);

    public final int dx;
    public final int dy;

    Direction(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }
}