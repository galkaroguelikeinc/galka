import java.util.List;

public class GameCell {
    private GameMapCellType gameMapCellType;
    private List<GameObject> gameObjectList;

    public GameCell(GameMapCellType gameMapCellType, List<GameObject> gameObjectList) {
        this.gameMapCellType = gameMapCellType;
        this.gameObjectList = gameObjectList;
    }

    public List<GameObject> getGameObjectList() {
        return gameObjectList;
    }

    public void setGameObjectList(List<GameObject> gameObjectList) {
        this.gameObjectList = gameObjectList;
    }

    public GameMapCellType getGameMapCellType() {
        return gameMapCellType;
    }
}
