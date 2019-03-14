package ru.spb.hse.roguelike.model;

import ru.spb.hse.roguelike.model.map.GameCell;
import ru.spb.hse.roguelike.model.map.GameMap;
import ru.spb.hse.roguelike.model.map.Room;
import ru.spb.hse.roguelike.model.object.alive.GameCharacter;
import ru.spb.hse.roguelike.model.object.items.Item;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * Model: class to remember the game map and inventory. Can be modified by Controller and used by View.
 */
public class GameModel extends GameMap {
    private List<Item> inventory;

    public GameModel(@Nonnull final List<Room> rooms,
                     @Nonnull final GameCell[][] data,
                     @Nonnull final List<Item> inventory) {
        super(rooms, data);
        this.inventory = inventory;
    }

    public void addItem(Item item) {
        inventory.add(item);
    }

    public GameCharacter generateCharacter() {
        //create character with position, if no character exists
        return null;
    }

    public GameCell getCell(int row, int col) {
        return data[row][col];
    }

    public int getRows() {
        return data.length;
    }

    public int getCols() {
        return getRows() == 0 ? 0 : data[0].length;
    }

    public List<Item> getInventory() {
        return inventory;
    }

    public void addInventory(Item item) {
        inventory.add(item);
    }

    public Item takeCellItem(int x, int y) {
        return data[x][y].takeCellItem();
    }

    public Item getItem(int index) {
        return inventory.get(index);
    }
}
