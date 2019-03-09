public class GameModel {
    private GameCell[][] gameMap;

    public static GameModel generateMap() {
        //TODO (Оля сделает здесь генерацию карты)
        return new GameModel();
    }

    public void generateMobsIfNeeded() {

    }

    public GameCell getCell(int x, int y) {
        return gameMap[x][y];
    }
}
