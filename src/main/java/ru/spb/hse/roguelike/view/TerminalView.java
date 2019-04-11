package ru.spb.hse.roguelike.view;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import ru.spb.hse.roguelike.model.GameModel;
import ru.spb.hse.roguelike.model.map.GameCell;
import ru.spb.hse.roguelike.model.map.GameMapCellType;
import ru.spb.hse.roguelike.model.object.alive.GameCharacter;

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
    private static final char ROOM_SYMBOL = '.';
    private static final char EMPTY_SYMBOL = ' ';
    private static final char TUNNEL_SYMBOL = '#';
    private static final char GAME_CHARACTER_SYMBOL = '&';
    private static final char MOB_SYMBOL = 'M';
    private static Map<GameMapCellType, Character> CELL_TYPE_TO_SYMBOL = new HashMap<GameMapCellType, Character>() {{
        put(GameMapCellType.ROOM, ROOM_SYMBOL);
        put(GameMapCellType.EMPTY, EMPTY_SYMBOL);
        put(GameMapCellType.TUNNEL, TUNNEL_SYMBOL);
    }};
    private TerminalScreen terminalScreen;
    private GameModel gameModel;

    /**
     * Create a terminal view.
     *
     * @param gameModel stores game data which needs to be shown to user
     */
    public TerminalView(GameModel gameModel) throws ViewException {
        this.gameModel = gameModel;
        DefaultTerminalFactory defaultTerminalFactory = new DefaultTerminalFactory();
        try {
            terminalScreen = new TerminalScreen(defaultTerminalFactory.createTerminal());
            terminalScreen.startScreen();
            terminalScreen.setCursorPosition(null);
        } catch (IOException e) {
            throw new ViewException();
        }
        try {
            for (int row = 0; row < gameModel.getRows(); row++) {
                for (int col = 0; col < gameModel.getCols(); col++) {
                    terminalScreen.setCharacter(new TerminalPosition(col, row),
                            new TextCharacter(cellToSymbol(gameModel.getCell(row, col))));
                    terminalScreen.refresh();
                }
            }
        } catch (IOException e) {
            throw new ViewException();
        }
    }

    private char cellToSymbol(GameCell gameCell) {
        if (gameCell.hasAliveObject()) {
            if (gameCell.getAliveObject().getClass().equals(GameCharacter.class)) {
                return GAME_CHARACTER_SYMBOL;
            } else {
                return MOB_SYMBOL;
            }
        }
        return CELL_TYPE_TO_SYMBOL.get(gameCell.getGameMapCellType());
    }

    @Override
    public void showChanges(int row, int col) throws ViewException {
        try {
            terminalScreen.setCharacter(new TerminalPosition(col, row),
                    new TextCharacter(cellToSymbol(gameModel.getCell(row, col))));
            terminalScreen.refresh();
        } catch (IOException e) {
            throw new ViewException();
        }
    }

    @Override
    public Command readCommand() {
        try {
            KeyStroke key = terminalScreen.readInput();
            switch (key.getKeyType()) {
                case ArrowDown:
                    return Command.DOWN;
                case ArrowUp:
                    return Command.UP;
                case ArrowLeft:
                    return Command.LEFT;
                case ArrowRight:
                    return Command.RIGHT;
            }
        } catch (IOException e) {
            System.out.println("Problems with parsing command");
        }
        return null;
    }
}
