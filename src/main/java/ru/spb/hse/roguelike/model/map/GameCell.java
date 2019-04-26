package ru.spb.hse.roguelike.model.map;

import ru.spb.hse.roguelike.model.object.alive.AliveObject;
import ru.spb.hse.roguelike.model.object.items.Item;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;

/**
 * Game cell: wall, place inside the room, place inside the tunnel and place outside of the room or tunnel.
 * Also collects Item or/and AliveObject, which is currently placed in it.
 */
public class GameCell implements Serializable {
    @Nonnull
    private GameMapCellType gameMapCellType;
    private AliveObject aliveObject;
    private Item cellItem;

    public GameCell(@Nonnull GameMapCellType gameMapCellType,
                    @Nullable AliveObject aliveObject,
                    @Nullable Item cellItem) {
        this.gameMapCellType = gameMapCellType;
        this.aliveObject = aliveObject;
        this.cellItem = cellItem;
    }

    public void addItem(Item item) throws GameCellException {
        if (hasItem()) {
            throw new GameCellException("This cell already has an item");
        }
        cellItem = item;
    }

    public void addAliveObject(AliveObject object) throws GameCellException {
        if (hasAliveObject()) {
            throw new GameCellException("This cell already has an alive object");
        }
        aliveObject = object;
    }

    public void removeAliveObject() {
        aliveObject = null;
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

    public AliveObject getAliveObject() {
        return aliveObject;
    }

    @Nonnull
    public GameMapCellType getGameMapCellType() {
        return gameMapCellType;
    }

    public void setGameMapCellType(@Nonnull GameMapCellType gameMapCellType) {
        this.gameMapCellType = gameMapCellType;
    }

    public boolean isNonEmptyTypeAndHasNoObjects() {
        return gameMapCellType != GameMapCellType.EMPTY && !hasAliveObject() && !hasItem();
    }
}
