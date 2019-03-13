package ru.spb.hse.roguelike.model;

import ru.spb.hse.roguelike.model.object.alive.AliveObject;
import ru.spb.hse.roguelike.model.object.items.Item;

/**
 * Game cell: wall, place inside the room, place inside the tunnel and place outside of the room or tunnel.
 * Also collects Item or/and AliveObject, which is currently placed in it.
 */
public class GameCell {
    private GameMapCellType gameMapCellType;
    private AliveObject aliveObject;
    private Item cellItem;

    public boolean hasAliveObject() {
        return aliveObject != null;
    }

    public boolean hasItem() {
        return cellItem != null;
    }

    public Item takeCellItem() {
        Item cellItem = this.cellItem;
        this.cellItem = null;
        return cellItem;
    }

    public GameMapCellType getGameMapCellType() {
        return gameMapCellType;
    }
}
