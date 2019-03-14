package ru.spb.hse.roguelike.model.object.items;

import ru.spb.hse.roguelike.model.object.alive.GameCharacter;

/**
 * Unmovable object, that can be used by game character to increase characteristics.
 */
public abstract class Item {
    private GameCharacter gameCharacter;
    private int itemNumber;
    Item(GameCharacter gameCharacter) {
        this.gameCharacter = gameCharacter;
        itemNumber = 1;
    }
    public abstract void use();

    GameCharacter getGameCharacter() {
        return gameCharacter;
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
}
