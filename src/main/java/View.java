public abstract class View {
    private GameModel gameModel;

    View(GameModel gameModel) {
        this.gameModel = gameModel;
    }

    abstract void showChanges();
}
