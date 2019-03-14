package ru.spb.hse.roguelike.model.object.alive;

/**
 * Represents the game character (a character, that can be moved bu player and use items)
 */
public class GameCharacter extends AliveObject {
    private int maxInventorySize;

    GameCharacter(int x, int y, int maxInventorySize) {
        super(x, y);
        this.maxInventorySize = maxInventorySize;
    }

    public int getMaxInventorySize() {
        return maxInventorySize;
    }
}
