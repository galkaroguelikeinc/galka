package ru.spb.hse.roguelike.model;

import ru.spb.hse.roguelike.Point;
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

    private Map<AliveObject, Point> aliveObjectToPoint = new HashMap<>();

    private void saveAliveObjectCoordinates() {
        for (int row = 0; row < gameMap.length; row++) {
            for (int col = 0; col < gameMap[row].length; col++) {
                if (gameMap[row][col].hasAliveObject()) {
                    AliveObject aliveObject = gameMap[row][col].getAliveObject();
                    aliveObjectToPoint.put(aliveObject, new Point(row, col));
                }
            }
        }
    }

    public GameModel(@Nonnull final GameCell[][] gameMap,
              @Nonnull final List<Item> inventory, GameCharacter character) {
        this.gameMap = gameMap;
        this.inventory = inventory;
        this.gameCharacter = character;
        saveAliveObjectCoordinates();
    }

    public GameCharacter getCharacter() {
        return gameCharacter;
    }

    public GameCell getCell(Point point) {
        if (point.getRow() < 0 || point.getRow() >= gameMap.length ||
                point.getCol() < 0 || point.getCol() >= gameMap[0].length) {
            return null;
        }
        return gameMap[point.getRow()][point.getCol()];
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

    public Item takeCellItem(Point point) {
        return gameMap[point.getRow()][point.getCol()].takeCellItem();
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

    public Point getAliveObjectPoint(AliveObject aliveObject) {
        if (!aliveObjectToPoint.containsKey(aliveObject)) {
            return null;
        }
        return aliveObjectToPoint.get(aliveObject);
    }

    public boolean moveAliveObjectDiff(AliveObject aliveObject, Point diff) {
        Point point  = aliveObjectToPoint.get(aliveObject);
        return moveAliveObject(aliveObject, point.add(diff));
    }

    public boolean moveAliveObject(AliveObject aliveObject, Point newPoint) {
        if (!aliveObjectToPoint.containsKey(aliveObject) ||
                getCell(newPoint) == null ||
                getCell(newPoint).getGameMapCellType() == EMPTY) {
            return false;
        }


        Point point = aliveObjectToPoint.get(aliveObject);
        removeAliveObject(point);
        addAliveObject(newPoint, aliveObject);
        if (hasItem(newPoint) &&
                getInventory().size() != getMaxInventorySize()) {
            addItem(takeCellItem(point));
        }
        return true;
    }

    private void removeAliveObject(Point point) {
        gameMap[point.getRow()][point.getCol()].removeAliveObject();
    }

    private void addAliveObject(Point point, AliveObject aliveObject) {
        gameMap[point.getRow()][point.getCol()].addAliveObject(aliveObject);
        aliveObjectToPoint.put(aliveObject, point);
    }

    private boolean hasItem(Point point) {
        return gameMap[point.getRow()][point.getCol()].hasItem();
    }
}
