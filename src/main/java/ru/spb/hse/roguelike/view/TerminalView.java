package ru.spb.hse.roguelike.view;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import ru.spb.hse.roguelike.Point;
import ru.spb.hse.roguelike.model.GameModel;
import ru.spb.hse.roguelike.model.map.GameCell;
import ru.spb.hse.roguelike.model.map.GameMapCellType;
import ru.spb.hse.roguelike.model.object.alive.GameCharacter;
import ru.spb.hse.roguelike.model.object.alive.NonPlayerCharacter;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

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
    private static final char AGGRESSIVE_MOB_SYMBOL = 'A';
    private static final char PASSIVE_MOB_SYMBOL = 'P';
    private static final char COWARD_MOB_SYMBOL = 'C';
    private static final char RANDOM_MOB_SYMBOL = 'R';
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
                            new TextCharacter(cellToSymbol(gameModel.getCell(new Point(row, col)))));
                    terminalScreen.refresh();
                }
            }
        } catch (IOException e) {
            throw new ViewException();
        }
    }

    public static boolean getAnswer(String question,
                                    Function<Character, Boolean> converterCharacterToAnswer) throws IOException {
        DefaultTerminalFactory defaultTerminalFactory = new DefaultTerminalFactory();
        TerminalScreen terminalScreen = new TerminalScreen(defaultTerminalFactory.createTerminal());
        terminalScreen.startScreen();
        terminalScreen.setCursorPosition(null);
        String[] questionLines = question.split(System.lineSeparator());
        for (int j = 0; j < questionLines.length; j++)
            for (int i = 0; i < questionLines[j].length(); i++) {
                terminalScreen.setCharacter(new TerminalPosition(i, j),
                        new TextCharacter(questionLines[j].charAt(i)));
                terminalScreen.refresh();
            }
        KeyStroke keyStroke = terminalScreen.readInput();
        boolean result = (keyStroke.getKeyType() == KeyType.Character
                && converterCharacterToAnswer.apply(keyStroke.getCharacter()));
        terminalScreen.clear();
        terminalScreen.stopScreen();
        return result;
    }

    private char cellToSymbol(@Nonnull GameCell gameCell) {
        if (gameCell.hasAliveObject()) {
            if (gameCell.getAliveObject().getClass().equals(GameCharacter.class)) {
                return GAME_CHARACTER_SYMBOL;
            } else {
                switch (((NonPlayerCharacter) gameCell.getAliveObject()).getNonPlayerCharacterStrategyType()) {
                    case PASSIVE:
                        return PASSIVE_MOB_SYMBOL;
                    case AGGRESSIVE:
                        return AGGRESSIVE_MOB_SYMBOL;
                    case COWARDLY:
                        return COWARD_MOB_SYMBOL;
                    case RANDOM:
                        return RANDOM_MOB_SYMBOL;
                }
                return ' ';
            }
        }
        return CELL_TYPE_TO_SYMBOL.get(gameCell.getGameMapCellType());
    }

    @Override
    public void showChanges(Point point) throws ViewException {
        try {
            terminalScreen.setCharacter(new TerminalPosition(point.getCol(), point.getRow()),
                    new TextCharacter(cellToSymbol(gameModel.getCell(point))));
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
                case Tab:
                    return Command.CONFUSE_MOBS;
            }
        } catch (IOException e) {
            System.out.println("Problems with parsing command");
        }
        return null;
    }

    @Override
    public void end() throws IOException, InterruptedException {
        System.out.println("end");
        terminalScreen.clear();
        terminalScreen.setCharacter(0, 0, new TextCharacter('e'));
        terminalScreen.setCharacter(1, 0, new TextCharacter('n'));
        terminalScreen.setCharacter(2, 0, new TextCharacter('d'));
        terminalScreen.refresh();
        TimeUnit.SECONDS.sleep(3);
        terminalScreen.close();
    }
}
