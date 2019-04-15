package ru.spb.hse.roguelike.model.object.items;

import ru.spb.hse.roguelike.model.object.alive.GameCharacter;

/**
 * A very unfulfilling type of food with food value of 1. At least, it's healthy!
 */
public class Water extends Food {
    public Water(GameCharacter gameCharacter) {
        super(gameCharacter, 1);
    }
}
