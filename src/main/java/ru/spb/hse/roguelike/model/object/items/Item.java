package ru.spb.hse.roguelike.model.object.items;

import ru.spb.hse.roguelike.model.object.alive.GameCharacter;

import java.io.Serializable;

/**
 * An object that can't move on its own. The game character can use it to increase their characteristics.
 */
public abstract class Item implements Serializable {
    private int itemNumber;

    Item() {
        itemNumber = 1;
    }

    void increaseNumber() {
        itemNumber++;
    }

    void decreaseNumber() {
        itemNumber--;
    }

    boolean isEmpty() {
        return itemNumber <= 0;
    }

    public int getItemNumber() {
        return itemNumber;
    }

    public abstract void apply(GameCharacter gameCharacter);
}
