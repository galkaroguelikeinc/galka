package ru.spb.hse.roguelike.model;

import ru.spb.hse.roguelike.model.map.GameCell;
import ru.spb.hse.roguelike.model.object.alive.AliveObject;
import ru.spb.hse.roguelike.model.object.alive.GameCharacter;
import ru.spb.hse.roguelike.model.object.items.Item;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ru.spb.hse.roguelike.model.map.GameMapCellType.EMPTY;

/**
 * Model: class to remember the game map and inventory. Can be modified by Controller and used by View.
 */
public class GameModel {
    private final GameCell[][] gameMap;
    private final List<Item> inventory;
    private final GameCharacter gameCharacter;
    private static final int MAX_INVENTORY_SIZE = 10;
    private boolean isEnd = false;

    private Map<AliveObject, Integer> aliveObjectToRow = new HashMap<>();
    private Map<AliveObject, Integer> aliveObjectToCol = new HashMap<>();

    private void saveAliveObjectCoordinates() {
        for (int row = 0; row < gameMap.length; row++) {
            for (int col = 0; col < gameMap[row].length; col++) {
                if (gameMap[row][col].hasAliveObject()) {
                    AliveObject aliveObject = gameMap[row][col].getAliveObject();
                    aliveObjectToRow.put(aliveObject, row);
                    aliveObjectToCol.put(aliveObject, col);
                }
            }
        }
    }

    GameModel(@Nonnull final GameCell[][] gameMap,
              @Nonnull final List<Item> inventory, GameCharacter character) {
        this.gameMap = gameMap;
        this.inventory = inventory;
        this.gameCharacter = character;
        saveAliveObjectCoordinates();
    }

    public GameCharacter getCharacter() {
        return gameCharacter;
    }

    public GameCell getCell(int row, int col) {
        if (row < 0 || row >= gameMap.length || col < 0 || col >= gameMap[0].length) {
            return null;
        }
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

    public void addItem(Item item) {
        inventory.add(item);
    }

    public Item takeCellItem(int row, int col) {
        return gameMap[row][col].takeCellItem();
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

    public Integer getAliveObjectRow(AliveObject aliveObject) {
        if (!aliveObjectToRow.containsKey(aliveObject)) {
            return null;
        }
        return aliveObjectToRow.get(aliveObject);
    }

    public Integer getAliveObjectCol(AliveObject aliveObject) {
        if (!aliveObjectToCol.containsKey(aliveObject)) {
            return null;
        }
        return aliveObjectToCol.get(aliveObject);
    }

    public boolean moveAliveObjectDiff(AliveObject aliveObject, int rowDiff, int colDiff) {
        int row = aliveObjectToRow.get(aliveObject);
        int col = aliveObjectToCol.get(aliveObject);
        return moveAliveObject(aliveObject, row + rowDiff, col + colDiff);
    }

    public boolean moveAliveObject(AliveObject aliveObject, int newRow, int newCol) {
        if (!aliveObjectToRow.containsKey(aliveObject) ||
                newRow < 0 || newRow >= gameMap.length ||
                newCol < 0 || newCol >= gameMap[newRow].length ||
                gameMap[newRow][newCol].getGameMapCellType() == EMPTY) {
            return false;
        }


        int row = aliveObjectToRow.get(aliveObject);
        int col = aliveObjectToCol.get(aliveObject);
        gameMap[row][col].removeAliveObject();
        gameMap[newRow][newCol].addAliveObject(aliveObject);
        aliveObjectToRow.put(aliveObject, newRow);
        aliveObjectToCol.put(aliveObject, newCol);
        if (gameMap[newRow][newCol].hasItem() &&
                getInventory().size() != getMaxInventorySize()) {
            addItem(takeCellItem(newRow, newCol));
        }
        return true;
    }
}
