public class Main {
    public static void main(String[] args) {
        GameModel model = GameModel.generateMap();
        View view = null;//TODO
        Controller controller = new Controller(view, model);
        controller.runGame();
    }
}
