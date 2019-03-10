public class GameModel {
    private GameCell[][] gameMap;

    public static GameModel generateMap() {
        //TODO (Оля сделает здесь генерацию карты)
        return new GameModel();
    }

    public GameModel() {

    }

    public GameModel(GameCell[][] gameMap) {
        this.gameMap = gameMap;
    }

    public void generateMobsIfNeeded() {

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
}
