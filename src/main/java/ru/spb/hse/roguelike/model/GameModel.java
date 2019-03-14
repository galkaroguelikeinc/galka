package ru.spb.hse.roguelike.model;

import ru.spb.hse.roguelike.model.object.alive.GameCharacter;
import ru.spb.hse.roguelike.model.object.items.Item;

import java.util.ArrayList;
import java.util.List;

/**
 * Model: class to remember the game map and inventory. Can be modified by Controller and used by View.
 */
public class GameModel {
    private GameCell[][] gameMap;
    private List<Item> inventory = new ArrayList<>();
    private GameCharacter gameCharacter;
    private boolean isEnd = false;

    public GameModel() {
        //TODO (Оля сделает здесь генерацию карты)
    }

    public void generateMobsIfNeeded() {

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
}
