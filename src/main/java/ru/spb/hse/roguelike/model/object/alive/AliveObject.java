package ru.spb.hse.roguelike.model.object.alive;

import ru.spb.hse.roguelike.model.object.GameObject;

/**
 * And object^ that can be moved.
 */
public abstract class AliveObject extends GameObject {
    private int xPos;
    private int yPos;

    AliveObject(int x, int y) {
        xPos = x;
        yPos = y;
    }

    public void move(int x, int y) {
        xPos = x;
        yPos = y;
    }

    public int getxPos() {
        return xPos;
    }

    public int getyPos() {
        return yPos;
    }
}
