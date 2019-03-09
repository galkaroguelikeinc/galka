public class Controller {
    private View view;
    private GameModel gameModel;
    private GameCharacter character;

    Controller(View view, GameModel gameModel) {
        this.gameModel = gameModel;
        this.view = view;
        character = gameModel.generateCharacter();
        gameModel.generateMobsIfNeeded();
    }

    public void runGame() {
        while(true) {
            switch (readCommand()) {
                case "q":
                    view.showResult(false);
                    return;
                case "i":
                    view.showInventory();
                    gameModel.getItem(Integer.parseInt(readCommand())).use(character);
                    break;
                case "left": {
                    int newX = character.getxPos() - 1;
                    int newY = character.getyPos();
                    move(newX, newY);
                    break;
                }
                case "right": {
                    int newX = character.getxPos() + 1;
                    int newY = character.getyPos();
                    move(newX, newY);break;
                }
                case "up": {
                    int newX = character.getxPos();
                    int newY = character.getyPos() - 1;
                    move(newX, newY);
                    break;
                }
                case "down": {
                    int newX = character.getxPos();
                    int newY = character.getyPos() + 1;
                    move(newX, newY);
                    break;
                }

            }
        }
    }

    private void move(int newX, int newY) {
        if (isFreeCell(newX, newY)) {
            character.move(newX, newY);
            if (gameModel.getCell(newY, newX).hasItem()
                    && gameModel.getInventory().size() != character.getMaxInventorySize()){
                gameModel.addInventory(gameModel.takeCellItem(newX, newY));
            }
        }
    }

    private boolean isFreeCell(int x, int y) {
        return true;
        //GameCell cell = gameModel.getCell(x, y);
        //if ((cell.gameMapCellType.equals(*room*) || cell.gameMapCellType.equals(*tunnel*))
        //  && !cell.hasAliveObject()) {
        //    return true;
        //}
        //return false;
    }

    private String readCommand() {
        //read key from line
        return "";
    }

}
