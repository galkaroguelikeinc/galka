package ru.spb.hse.roguelike.view;

import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import ru.spb.hse.roguelike.model.GameCell;
import ru.spb.hse.roguelike.model.GameMapCellType;
import ru.spb.hse.roguelike.model.GameModel;
import ru.spb.hse.roguelike.view.View;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * TerminalView displays the game to the user via terminal pseudo graphics.
 * <p>
 * If the system is headless (e.g. a server without the graphics environment), then it runs inside of the terminal,
 * otherwise it runs a Swing-emulated terminal.
 * <p>
 * It uses Lanterna, the terminal type guess is also done by Lanterna.
 */
public class TerminalView extends View {
    private Terminal terminal = null;

    public TerminalView(GameModel gameModel) {
        super(gameModel);
        DefaultTerminalFactory defaultTerminalFactory = new DefaultTerminalFactory();
        try {
            terminal = defaultTerminalFactory.createTerminal();
        } catch (IOException ignored) {
        }
        showChanges();
    }

    private char cellToSymbol(GameCell gameCell) {
        Map<GameMapCellType, Character> cellToSymbol = new HashMap<GameMapCellType, Character>() {{
            put(GameMapCellType.wall, '#');
            put(GameMapCellType.room, '+');
            put(GameMapCellType.empty, ' ');
            put(GameMapCellType.tunnel, '-');
        }};
        if (gameCell.hasItem() || gameCell.hasAliveObject()) {
            return '*';
        }
        return cellToSymbol.get(gameCell.getGameMapCellType());
    }

    @Override
    public void showChanges() {
        if (terminal == null) {
            System.err.println("Unexpected exception, couldn't create terminal." +
                    "We are sorry! Please, restart the game.");
            System.exit(1);
            return;
        }

        // the terminal window is col, row from top-left corner
        // the map is row, col from top-left corner
        try {
            for (int row = 0; row < gameModel.getRows(); row++) {
                for (int col = 0; col < gameModel.getCols(); col++) {
                    terminal.setCursorPosition(col, row);
                    terminal.putCharacter(cellToSymbol(gameModel.getCell(row, col)));
                    terminal.flush();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Unexpected exception while redrawing terminal." +
                    "We are sorry! Please, restart the game.");
        }
    }

    @Override
    public String readCommand() {
        return null;
    }
}
