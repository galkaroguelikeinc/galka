package ru.spb.hse.roguelike.model.object.items;

import ru.spb.hse.roguelike.model.object.alive.GameCharacter;
import ru.spb.hse.roguelike.model.object.GameObject;

/**
 * Unmovable object, that can be used by game character to increase characteristics.
 */
public abstract class Item extends GameObject {
    public abstract void use(GameCharacter character);
}
