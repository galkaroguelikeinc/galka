package ru.spb.hse.roguelike.model.object.items;

import ru.spb.hse.roguelike.model.object.alive.GameCharacter;

import java.io.Serializable;

/**
 * An object that can't move on its own. The game character can use it to increase their characteristics.
 */
public abstract class Item implements Serializable {
    public abstract void apply(GameCharacter gameCharacter) throws CannotApplyFoodMultipleTimesException;
}
