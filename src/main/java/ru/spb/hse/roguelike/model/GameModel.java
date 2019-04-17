package ru.spb.hse.roguelike.model;

import ru.spb.hse.roguelike.Point;
import ru.spb.hse.roguelike.model.map.GameCell;
import ru.spb.hse.roguelike.model.object.alive.AliveObject;
import ru.spb.hse.roguelike.model.object.alive.ConfusedNonPlayerCharacter;
import ru.spb.hse.roguelike.model.object.alive.GameCharacter;
import ru.spb.hse.roguelike.model.object.alive.NonPlayerCharacter;
import ru.spb.hse.roguelike.model.object.items.Item;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.spb.hse.roguelike.model.map.GameMapCellType.EMPTY;

/**
 * Model: class to remember the game map and inventory. Can be modified by Controller and used by View.
 */
public class GameModel {
    private final GameCell[][] gameMap;
    private final List<Item> inventory;
    private final GameCharacter gameCharacter;
    private final int maxInventorySize;
    // TODO: separate all alive objects into the game character and non-player characters
    private final Map<AliveObject, Point> aliveObjectToPoint = new HashMap<>();
    private boolean isEnd = false;

    public GameModel(@Nonnull final GameCell[][] gameMap,
                     @Nonnull final List<Item> inventory, GameCharacter character, int maxInventorySize) {
        this.gameMap = gameMap;
        this.inventory = inventory;
        this.gameCharacter = character;
        this.maxInventorySize = maxInventorySize;
        saveAliveObjectCoordinates();
    }

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

    public void confuseMobs() {
        Map<AliveObject, Point> newAliveObjectToPoint = new HashMap<>();

        Point gameCharacterPoint = aliveObjectToPoint.get(gameCharacter);

        for (Map.Entry<AliveObject, Point> entry : aliveObjectToPoint.entrySet()) {
            if (entry.getKey() == gameCharacter) {
                continue;
            }
            newAliveObjectToPoint.put(new ConfusedNonPlayerCharacter((NonPlayerCharacter) entry.getKey()), entry.getValue());
        }

        aliveObjectToPoint.clear();
        aliveObjectToPoint.put(gameCharacter, gameCharacterPoint);
        aliveObjectToPoint.putAll(newAliveObjectToPoint);
    }

    public Set<NonPlayerCharacter> getNonGameCharacters() {
        return aliveObjectToPoint.
                keySet().
                stream().
                filter(x -> !x.equals(gameCharacter)).
                map(x -> (NonPlayerCharacter) x).
                collect(Collectors.toSet());
    }

    public GameCharacter getCharacter() {
        return gameCharacter;
    }

    public boolean hasCell(Point point) {
        return (point.getRow() >= 0 && point.getRow() < gameMap.length &&
                point.getCol() >= 0 && point.getCol() < gameMap[0].length);
    }

    public GameCell getCell(Point point) {
        // TODO: throw an error
        return hasCell(point) ? gameMap[point.getRow()][point.getCol()] : null;
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

    public int getMaxInventorySize() {
        return maxInventorySize;
    }

    public Point getAliveObjectPoint(AliveObject aliveObject) throws UnknownObjectException {
        if (!aliveObjectToPoint.containsKey(aliveObject)) {
            throw new UnknownObjectException("This game model doesn't contain any information on the alive object.");
        }
        return aliveObjectToPoint.get(aliveObject);
    }

    public boolean moveAliveObjectDiff(AliveObject aliveObject, Point diff) {
        Point point = aliveObjectToPoint.get(aliveObject);
        return moveAliveObject(aliveObject, point.add(diff));
    }

    public boolean moveAliveObject(AliveObject aliveObject, Point newPoint) {
        if (!aliveObjectToPoint.containsKey(aliveObject) ||
                getCell(newPoint) == null ||
                getCell(newPoint).getGameMapCellType() == EMPTY ||
                getCell(newPoint).hasAliveObject()) {
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

    public void removeAliveObject(Point point) {
        aliveObjectToPoint.remove(gameMap[point.getRow()][point.getCol()].getAliveObject());
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
