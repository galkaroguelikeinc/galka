import java.util.List;

/**
 * Клетка на игровой карте -- это либо стена, либо клетка внутри комнаты, либо клетка тоннеля,
 * либо ничего (клетка ни в какой комнате), а также список из мобов, айтемов, персонажа и т.д.
 */
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
