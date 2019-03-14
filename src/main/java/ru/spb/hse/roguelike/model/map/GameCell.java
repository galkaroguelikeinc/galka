package ru.spb.hse.roguelike.model.map;

import ru.spb.hse.roguelike.model.object.alive.AliveObject;
import ru.spb.hse.roguelike.model.object.items.Item;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Game cell: wall, place inside the room, place inside the tunnel and place outside of the room or tunnel.
 * Also collects Item or/and AliveObject, which is currently placed in it.
 */
public class GameCell {
    @Nonnull
    private GameMapCellType gameMapCellType;
    private AliveObject aliveObject;
    private Item cellItem;

    public GameCell(@Nonnull final GameMapCellType gameMapCellType,
                    @Nullable final AliveObject aliveObject,
                    @Nullable final Item cellItem) {
        this.gameMapCellType = gameMapCellType;
        this.aliveObject = aliveObject;
        this.cellItem = cellItem;
    }

    public boolean addItem(Item item) {
        if (hasItem()) {
            return false;
        }
        cellItem = item;
        return true;
    }

    public boolean addAliveObject(AliveObject object) {
        if (hasAliveObject()) {
            return false;
        }
        aliveObject = object;
        return true;
    }

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

    @Nonnull
    public GameMapCellType getGameMapCellType() {
        return gameMapCellType;
    }

    public void setGameMapCellType(@Nonnull GameMapCellType gameMapCellType) {
        this.gameMapCellType = gameMapCellType;
    }
}
