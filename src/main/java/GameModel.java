import java.util.ArrayList;
import java.util.List;

public class GameModel {
    private GameCell[][] gameMap;
    private List<Item> inventory = new ArrayList<>();

    public static GameModel generateMap() {
        //TODO (Оля сделает здесь генерацию карты)
        return new GameModel();
    }

    public void generateMobsIfNeeded() {

    }

    public void addItem(Item item) {
        inventory.add(item);
    }

    public GameCharacter generateCharacter() {
        //create character with position, if no character exists
    }

    public GameCell getCell(int x, int y) {
        return gameMap[x][y];
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

    public Item getItem(int index) {
        return inventory.get(index);
    }
}
