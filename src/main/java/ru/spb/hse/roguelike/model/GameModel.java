package ru.spb.hse.roguelike.model;

import ru.spb.hse.roguelike.model.map.GameCell;
import ru.spb.hse.roguelike.model.object.alive.GameCharacter;
import ru.spb.hse.roguelike.model.object.items.Item;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Model: class to remember the game map and inventory. Can be modified by Controller and used by View.
 */
public class GameModel {
    private final GameCell[][] gameMap;
    private final List<Item> inventory;
    private GameCharacter gameCharacter;
    private static final int MAX_INVENTORY_SIZE = 10;
    private boolean isEnd = false;

    public GameModel(@Nonnull final GameCell[][] gameMap,
                     @Nonnull final List<Item> inventory) {
        this.gameMap = gameMap;
        this.inventory = inventory;
    }

    public GameCharacter getCharacter() {
        return gameCharacter;
    }

    public GameCell getCell(int row, int col) {
        return gameMap[row][col];
    }

    public int getRows() {
        return gameMap.length;
    }

    public int getCols() {
        return getRows() == 0 ? 0 : gameMap[0].length;
    }

    public List<Item> getInventory() {
        return inventory;
    }

    public void addInventory(Item item) {
        inventory.add(item);
    }

    public Item takeCellItem(int x, int y) {
        return gameMap[x][y].takeCellItem();
    }

    public Item getItemFromInventory(int index) {
        return inventory.get(index);
    }

    public void end() {
        isEnd = true;
    }

    public boolean isEnd() {
        return isEnd;
    }

    public static int getMaxInventorySize() {
        return MAX_INVENTORY_SIZE;
    }
}