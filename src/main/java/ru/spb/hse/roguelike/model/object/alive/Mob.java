package ru.spb.hse.roguelike.model.object.alive;

/**
 * Mob% alive object, that can move and have fights with the game character.
 */
public abstract class Mob extends AliveObject {
    Mob(int x, int y) {
        super(x, y);
    }
}
