package ru.spb.hse.roguelike.model;

import ru.spb.hse.roguelike.Point;
import ru.spb.hse.roguelike.model.map.GameCell;
import ru.spb.hse.roguelike.model.map.GameCellException;
import ru.spb.hse.roguelike.model.object.alive.AliveObject;
import ru.spb.hse.roguelike.model.object.alive.ConfusedNonPlayerCharacter;
import ru.spb.hse.roguelike.model.object.alive.GameCharacter;
import ru.spb.hse.roguelike.model.object.alive.NonPlayerCharacter;
import ru.spb.hse.roguelike.model.object.items.CannotApplyFoodMultipleTimesException;
import ru.spb.hse.roguelike.model.object.items.Food;
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
    private final Map<Integer, GameCharacter> gameCharacters;
    private final int maxInventorySize;
    private final Map<AliveObject, Point> aliveObjectToPoint = new HashMap<>();
    private boolean isEnd = false;

    public GameModel(@Nonnull final GameCell[][] gameMap, GameCharacter character, int maxInventorySize, int playerId) {
        this.gameMap = gameMap;
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

    /**
     * @return game characters with their ids
     */
    public Map<Integer, GameCharacter> getGameCharacters() {
        return gameCharacters;
    }

    /**
     * make mobs confused.
     */
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

    /**
     * @return mobs
     */
    public Set<NonPlayerCharacter> getNonGameCharacters() {
        List<Point> gameCharacterPoints = gameCharacters.values().stream().map(aliveObjectToPoint::get).collect(Collectors.toList());
        return aliveObjectToPoint.
                keySet().
                stream().
                filter(x -> !gameCharacterPoints.contains(aliveObjectToPoint.get(x))).
                map(x -> (NonPlayerCharacter) x).
                collect(Collectors.toSet());
    }

    /**
     * @return game characters
     */
    public List<GameCharacter> getCharacters() {
        return new ArrayList<>(gameCharacters.values());
    }

    /**
     * @param point point ot check
     * @return true if such cell exists
     */
    public boolean hasCell(Point point) {
        return (point.getRow() >= 0 && point.getRow() < gameMap.length &&
                point.getCol() >= 0 && point.getCol() < gameMap[0].length);
    }

    /**
     * @param point to find the cell
     * @return cell of the given point
     */
    public GameCell getCell(Point point) {
        return hasCell(point) ? gameMap[point.getRow()][point.getCol()] : null;
    }

    /**
     * @return number of rows
     */
    public int getRows() {
        return gameMap.length;
    }

    /**
     * @return number of columns
     */
    public int getCols() {
        return getRows() == 0 ? 0 : gameMap[0].length;
    }

    /**
     * find the item from the cell
     * @param point point to check item
     * @return itme in the cell
     */
    public Item takeCellItem(Point point) {
        return gameMap[point.getRow()][point.getCol()].takeCellItem();
    }

    /**
     * end the game
     */
    public void end() {
        isEnd = true;
    }

    /**
     * @return true if the game ended
     */
    public boolean isEnd() {
        return isEnd;
    }

    /**
     * Get position of the alive object
     * @param aliveObject object with unknown position
     * @return point where the object is
     * @throws UnknownObjectException if no such object
     */
    public Point getAliveObjectPoint(AliveObject aliveObject) throws UnknownObjectException {
        if (!aliveObjectToPoint.containsKey(aliveObject)) {
            throw new UnknownObjectException("This game model doesn't contain any information on the alive object.");
        }
        return aliveObjectToPoint.get(aliveObject);
    }

    /**
     * Change alive object position of some difference.
     * @param aliveObject mob or character
     * @param diff difference between new and old points
     * @return true if success
     * @throws GameCellException if problems with game cell objects
     */
    public boolean moveAliveObjectDiff(AliveObject aliveObject, Point diff) throws GameCellException {
        Point point = aliveObjectToPoint.get(aliveObject);
        return moveAliveObject(aliveObject, point.add(diff));
    }

    /**
     * Change alive object position.
     * @param aliveObject mob or character
     * @param newPoint point to move
     * @return true if success
     * @throws GameCellException if problems with game cell objects
     */
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
        if (hasItem(newPoint)) {
            Item item = takeCellItem(newPoint);
            if (item.getClass().equals(Food.class)
                    && ((GameCharacter) aliveObject).getFoodFullness() != ((GameCharacter) aliveObject).getMaxFoodFullness()) {
                ((GameCharacter) aliveObject).setFoodFullness(((GameCharacter) aliveObject).getFoodFullness()
                        + ((Food)takeCellItem(newPoint)).getPower());
            } else if (item.getClass().equals(Wearable.class) && !((GameCharacter)aliveObject).hasWearable()) {
                ((GameCharacter) aliveObject).pushWearable((Wearable) item);
            }
        }
        return true;
    }

    /**
     * Remove mob or game character from the cell.
     * @param point point of the cell to remove alive object.
     */
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

    /**
     * Use the item.
     * @param characterId id of a character to use the item in the cell
     */
    public void makeCharacterApplyItem(int characterId) throws CannotPickItemException, CannotApplyFoodMultipleTimesException {
        GameCell cell = getCell(aliveObjectToPoint.get(gameCharacters.get(characterId)));
        if (!cell.hasItem()) {
            throw new CannotPickItemException();
        }
        Item item = cell.takeCellItem();
        item.apply(gameCharacters.get(characterId));
    }

    /**
     * Do not use item anymore.
     * @param characterId id of a character to drop item
     * @throws CannotDropWearableException if not possible to drop
     */
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

    /**
     * Adds new character to the game.
     * @param id new player id
     * @throws GameCellException if problems with choosing new cell.
     */
    public void addCharacter(int id) throws GameCellException {
        gameCharacters.put(id, new GameCharacter());
        while (true) {
            int row = RANDOM.nextInt(gameMap.length);
            int col = RANDOM.nextInt(gameMap[0].length);
            if (gameMap[row][col].isNonEmptyTypeAndHasNoObjects()) {
                addAliveObject(new Point(row, col),gameCharacters.get(id) );
                return;
            }
        }
    }

    /**
     * Transforms string to the game model
     * @param s string representing game model
     * @return created game model
     * @throws IOException in problems with streams
     */
    public static GameModel fromString(String s) throws IOException,
            ClassNotFoundException {
        byte[] data = Base64.getDecoder().decode(s);
        ObjectInputStream ois = new ObjectInputStream(
                new ByteArrayInputStream(data));
        GameModel o = (GameModel) ois.readObject();
        ois.close();
        return o;
    }

    /**
     * Transforms the mode to string
     * @param model game model
     * @return string representing the model
     * @throws IOException in problems with streams
     */
    public static String toString(GameModel model) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(model);
        oos.close();
        return Base64.getEncoder().encodeToString(baos.toByteArray());
    }
}
