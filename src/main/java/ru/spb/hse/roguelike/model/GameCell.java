package ru.spb.hse.roguelike.model;

import ru.spb.hse.roguelike.model.object.alive.AliveObject;
import ru.spb.hse.roguelike.model.object.items.Item;

/**
 * Клетка на игровой карте -- это либо стена, либо клетка внутри комнаты, либо клетка тоннеля,
 * либо ничего (клетка ни в какой комнате), а также список из мобов, айтемов, персонажа и т.д.
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
