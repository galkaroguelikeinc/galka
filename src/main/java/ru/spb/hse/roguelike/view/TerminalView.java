package ru.spb.hse.roguelike.view;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import ru.spb.hse.roguelike.Point;
import ru.spb.hse.roguelike.controler.GameStateSaver;
import ru.spb.hse.roguelike.model.GameModel;
import ru.spb.hse.roguelike.model.Generator;
import ru.spb.hse.roguelike.model.MapGeneratorException;
import ru.spb.hse.roguelike.model.map.GameCell;
import ru.spb.hse.roguelike.model.map.GameCellException;
import ru.spb.hse.roguelike.model.map.GameMapCellType;
import ru.spb.hse.roguelike.model.object.alive.GameCharacter;
import ru.spb.hse.roguelike.model.object.alive.NonPlayerCharacter;
import ru.spb.hse.roguelike.ws.client.RoguelikeClient;

import javax.annotation.Nonnull;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
    private static final char ITEM_SYMBOL = '*';
    private static final int SINGLE_MODE = 0;
    private static final int MULTI_MODE = 0;

    private static Map<GameMapCellType, Character> CELL_TYPE_TO_SYMBOL = new HashMap<GameMapCellType, Character>() {{
        put(GameMapCellType.ROOM, ROOM_SYMBOL);
        put(GameMapCellType.EMPTY, EMPTY_SYMBOL);
        put(GameMapCellType.TUNNEL, TUNNEL_SYMBOL);
    }};
    private TerminalScreen terminalScreen;
    private GameModel gameModel;
    private int numInfoRows = 0;
    private int numCols = 0;
    private int id;
    private int mode;

    /**
     * Create a terminal view.
     */
    public TerminalView(GameModel gameModel) throws IOException {
        DefaultTerminalFactory defaultTerminalFactory = new DefaultTerminalFactory();
        try {
            terminalScreen = new TerminalScreen(defaultTerminalFactory.createTerminal());
            terminalScreen.startScreen();
            terminalScreen.setCursorPosition(null);
        } catch (IOException e) {
            throw new ViewException();
        }
        this.gameModel = gameModel;
        try {
            drawGameMap();
            drawCharacterInfo(id);
            terminalScreen.refresh();
        } catch (IOException e) {
            throw new ViewException();
        }
    }

    private void drawGameMap() {
        for (int row = 0; row < gameModel.getRows(); row++) {
            for (int col = 0; col < gameModel.getCols(); col++) {
                terminalScreen.setCharacter(new TerminalPosition(col, row),
                        new TextCharacter(cellToSymbol(gameModel.getCell(new Point(row, col)))));
            }
        }
        numCols += gameModel.getCols();
    }

    private void drawText(char[][] text, int rowOffset, int colOffset) {
        for (int row = 0; row < text.length; row++) {
            for (int col = 0; col < text[row].length; col++) {
                terminalScreen.setCharacter(new TerminalPosition(col + colOffset, row + rowOffset),
                        new TextCharacter(text[row][col]));
            }
        }
    }

    private void drawNextLine(String text) {
        drawText(new char[][]{text.toCharArray()}, numInfoRows, numCols);
        numInfoRows++;
    }

    private void drawExperienceAndLevelInfo(int id) {
        Map<String, Integer> stats = new HashMap<>();
        GameCharacter gameCharacter = gameModel.getCharacters().get(id);
        stats.put("XP", gameCharacter.getExperiencePoints());
        stats.put("Level", gameCharacter.getLevel());
        for (Map.Entry<String, Integer> labelAndValue : stats.entrySet()) {
            String label = labelAndValue.getKey();
            String value = Integer.toString(labelAndValue.getValue());
            String line = label + ": " + value;
            drawNextLine(line);
        }
    }

    private void drawMeasurableCharacteristicInfo(String label, int currentValue, int maxValue) {
        String line = label + ": " + currentValue + " / " + maxValue;
        drawNextLine(line);
    }

    private void drawCharacterInfo(int id) {
        drawExperienceAndLevelInfo(id);
        GameCharacter gameCharacter = gameModel.getCharacters().get(id);
        drawMeasurableCharacteristicInfo(
                "Health", gameCharacter.getCurrentHealth(), gameCharacter.getMaxHealth());
        drawMeasurableCharacteristicInfo(
                "Power", gameCharacter.getCurrentPower(), gameCharacter.getMaxPower());
        drawMeasurableCharacteristicInfo(
                "Food", gameCharacter.getFoodFullness(), gameCharacter.getMaxFoodFullness());
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
        } else if (gameCell.hasItem()) {
            return ITEM_SYMBOL;
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
    public CommandNameId readCommand() {
        try {
            KeyStroke key = terminalScreen.readInput();
            switch (key.getKeyType()) {
                case ArrowDown:
                    return new CommandNameId(CommandName.DOWN, 0);
                case ArrowUp:
                    return new CommandNameId(CommandName.UP, 0);
                case ArrowLeft:
                    return new CommandNameId(CommandName.LEFT, 0);
                case ArrowRight:
                    return new CommandNameId(CommandName.RIGHT, 0);
                case Tab:
                    return new CommandNameId(CommandName.CONFUSE_MOBS, 0);
                case Enter:
                    return new CommandNameId(CommandName.APPLY_ITEM, 0);
                case Escape:
                    return new CommandNameId(CommandName.DROP_WEARABLE, 0);
            }
        } catch (IOException e) {
            System.out.println("Problems with parsing command");
        }
        return null;
    }

    @Override
    public void end() throws IOException, InterruptedException {
        terminalScreen.clear();
        printString("end");
        terminalScreen.refresh();
        TimeUnit.SECONDS.sleep(3);
        terminalScreen.close();
    }

    private void printString(String string) {
        char[] array = string.toCharArray();
        for (int i = 0; i < array.length; i++) {
            terminalScreen.setCharacter(i, 0, new TextCharacter(array[i]));
        }
    }
}
