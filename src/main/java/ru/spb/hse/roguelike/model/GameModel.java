package ru.spb.hse.roguelike.model;

import ru.spb.hse.roguelike.Point;
import ru.spb.hse.roguelike.model.map.GameCell;
import ru.spb.hse.roguelike.model.map.GameCellException;
import ru.spb.hse.roguelike.model.object.alive.AliveObject;
import ru.spb.hse.roguelike.model.object.alive.ConfusedNonPlayerCharacter;
import ru.spb.hse.roguelike.model.object.alive.GameCharacter;
import ru.spb.hse.roguelike.model.object.alive.NonPlayerCharacter;
import ru.spb.hse.roguelike.model.object.items.CannotApplyFoodMultipleTimesException;
import ru.spb.hse.roguelike.model.object.items.Item;
import ru.spb.hse.roguelike.model.object.items.Wearable;

import javax.annotation.Nonnull;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

import static ru.spb.hse.roguelike.model.map.GameMapCellType.EMPTY;

/**
 * Model: class to remember the game map and characterInventory. Can be modified by Controller and used by View.
 */
public class GameModel implements Serializable {
    private final GameCell[][] gameMap;
    private final List<Item> characterInventory;  // TODO: remove either this or GameCharacter's internal wearableStack
    private final Map<Integer, GameCharacter> gameCharacters;
    private final int maxInventorySize;
    // TODO: separate all alive objects into the game character and non-player characters
    private final Map<AliveObject, Point> aliveObjectToPoint = new HashMap<>();
    private boolean isEnd = false;

    public GameModel(@Nonnull final GameCell[][] gameMap,
                     @Nonnull final List<Item> characterInventory, GameCharacter character, int maxInventorySize, int playerId) {
        this.gameMap = gameMap;
        this.characterInventory = characterInventory;
        this.gameCharacters = new HashMap<>();
        gameCharacters.put(playerId, character);
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

    public Map<Integer, GameCharacter> getGameCharacters() {
        return gameCharacters;
    }

    public void confuseMobs() {
        Map<AliveObject, Point> newAliveObjectToPoint = new HashMap<>();

        List<Point> gameCharacterPoints = gameCharacters.values().stream().map(aliveObjectToPoint::get).collect(Collectors.toList());

        for (Map.Entry<AliveObject, Point> entry : aliveObjectToPoint.entrySet()) {
            if (gameCharacterPoints.contains(entry.getValue())) {
                continue;
            }
            newAliveObjectToPoint.put(new ConfusedNonPlayerCharacter((NonPlayerCharacter) entry.getKey()), entry.getValue());
        }

        aliveObjectToPoint.clear();
        for (int i = 0; i < gameCharacters.size(); i++) {
            aliveObjectToPoint.put(gameCharacters.get(i), gameCharacterPoints.get(i));
        }
        aliveObjectToPoint.putAll(newAliveObjectToPoint);
    }

    public Set<NonPlayerCharacter> getNonGameCharacters() {
        List<Point> gameCharacterPoints = gameCharacters.values().stream().map(aliveObjectToPoint::get).collect(Collectors.toList());
        return aliveObjectToPoint.
                keySet().
                stream().
                filter(x -> !gameCharacterPoints.contains(aliveObjectToPoint.get(x))).
                map(x -> (NonPlayerCharacter) x).
                collect(Collectors.toSet());
    }

    public List<GameCharacter> getCharacters() {
        List<GameCharacter> answer = new ArrayList<>();
        answer.addAll(gameCharacters.values());
        return answer;
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

    public List<Item> getCharacterInventory() {
        return characterInventory;
    }

    public void addItem(Item item) {
        characterInventory.add(item);
    }

    public Item takeCellItem(Point point) {
        return gameMap[point.getRow()][point.getCol()].takeCellItem();
    }

    public Item getItemFromInventory(int index) {
        return characterInventory.get(index);
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

    public boolean moveAliveObjectDiff(AliveObject aliveObject, Point diff) throws GameCellException {
        Point point = aliveObjectToPoint.get(aliveObject);
        return moveAliveObject(aliveObject, point.add(diff));
    }

    public boolean moveAliveObject(AliveObject aliveObject, Point newPoint) throws GameCellException {
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
                getCharacterInventory().size() != getMaxInventorySize()) {
            addItem(takeCellItem(point));
        }
        return true;
    }

    public void removeAliveObject(Point point) {
        aliveObjectToPoint.remove(gameMap[point.getRow()][point.getCol()].getAliveObject());
        gameMap[point.getRow()][point.getCol()].removeAliveObject();
    }

    private void addAliveObject(Point point, AliveObject aliveObject) throws GameCellException {
        gameMap[point.getRow()][point.getCol()].addAliveObject(aliveObject);
        aliveObjectToPoint.put(aliveObject, point);
    }

    private boolean hasItem(Point point) {
        return gameMap[point.getRow()][point.getCol()].hasItem();
    }

    public void makeCharacterApplyItem(int characterId) throws CannotPickItemException, CannotApplyFoodMultipleTimesException {
        GameCell cell = getCell(aliveObjectToPoint.get(gameCharacters.get(characterId)));
        if (!cell.hasItem()) {
            throw new CannotPickItemException();
        }
        Item item = cell.takeCellItem();
        item.apply(gameCharacters.get(characterId));
    }

    public void makeCharacterDropTopWearable(int characterId) throws CannotDropWearableException {
        if (!gameCharacters.get(characterId).hasWearable()) {
            throw new CannotDropWearableException("The character doesn't have any items to drop");
        }
        Wearable wearable = gameCharacters.get(characterId).peekWearable();
        GameCell cell = getCell(aliveObjectToPoint.get(gameCharacters.get(characterId)));
        try {
            cell.addItem(wearable);
        } catch (GameCellException e) {
            throw new CannotDropWearableException("This cell already has an item.");
        }
        wearable.unapply(gameCharacters.get(characterId));
    }

    private static final Random RANDOM = new Random();

    public void addCharacter(int id) throws GameCellException {
        System.out.println("add character");
        gameCharacters.put(id, new GameCharacter());
        while (true) {
            int row = RANDOM.nextInt(gameMap.length);
            int col = RANDOM.nextInt(gameMap[0].length);
            if (gameMap[row][col].isNonEmptyTypeAndHasNoObjects()) {
                //TODO МАША ПОСМОТРИ тут раньше не добавлялись объекты
                addAliveObject(new Point(row, col), gameCharacters.get(id));
                //gameMap[row][col].addAliveObject(gameCharacters.get(id));
                return;
            }
        }
    }

    public static GameModel fromString(String s) throws IOException,
            ClassNotFoundException {
        byte[] data = Base64.getDecoder().decode(s);
        ObjectInputStream ois = new ObjectInputStream(
                new ByteArrayInputStream(data));
        GameModel o = (GameModel) ois.readObject();
        ois.close();
        return o;
    }

    public static String toString(GameModel o) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(o);
        oos.close();
        return Base64.getEncoder().encodeToString(baos.toByteArray());
    }
}
