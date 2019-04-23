package ru.spb.hse.roguelike.model;

import org.junit.Test;
import ru.spb.hse.roguelike.Point;
import ru.spb.hse.roguelike.model.map.GameMapCellType;
import ru.spb.hse.roguelike.model.object.alive.NonPlayerCharacter;

import java.io.FileNotFoundException;
import java.util.function.Function;

import static org.junit.Assert.assertEquals;

/**
 * Tests generating the map from file and randomly.
 */
public class GeneratorTest {
    private final Generator generator = new Generator();

    @Test
    public void testGenerateModelFromFile() throws FileNotFoundException, MapGeneratorException {
        Function<Character, GameMapCellType> decoder = character -> {
            if (character == '.') {
                return GameMapCellType.ROOM;
            }
            if (character == '#') {
                return GameMapCellType.TUNNEL;
            }
            return GameMapCellType.EMPTY;
        };
        String value = "_..._____" +
                "_..._____" +
                "_...#____" +
                "_...#____" +
                "_...##___" +
                "_..._..._" +
                "_..._..._" +
                "_____..._";
        GameModel gameModel = generator.generateModel("src/test/resources/map.txt", decoder);
        assertEquals(9, gameModel.getCols());
        assertEquals(8, gameModel.getRows());
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 9; j++) {
                assertEquals(decoder.apply(value.charAt(i * 9 + j)), gameModel.getCell(new Point(i, j)).getGameMapCellType());
            }
        }
    }

    @Test
    public void testCountRooms() throws MapGeneratorException {
        for (int x = 0; x < 1000; x++) {
            GameModel gameModel = generator.generateModel(3, 20, 25);
            int actualRoomCount = 0;
            for (int i = 0; i < 25; i++) {
                for (int j = 0; j < 20; j++) {
                    if (gameModel.getCell(new Point(i, j)).getGameMapCellType() == GameMapCellType.ROOM) {
                        if ((i == 0 || gameModel.getCell(new Point(i - 1, j)).getGameMapCellType() != GameMapCellType.ROOM) &&
                                (j == 0 || gameModel.getCell(new Point(i, j - 1)).getGameMapCellType() != GameMapCellType.ROOM)) {
                            actualRoomCount++;
                        }
                    }
                }
            }
            assertEquals(3, actualRoomCount);
        }
    }

    @Test
    public void testGenerateNPC() throws MapGeneratorException {
        GameModel gameModel = generator.generateModel(3, 20, 25);
        int actualNPCCount = 0;
        for (int i = 0; i < 25; i++) {
            for (int j = 0; j < 20; j++) {
                if (gameModel.getCell(new Point(i, j)).hasAliveObject()
                        && gameModel.getCell(new Point(i, j)).getAliveObject() instanceof NonPlayerCharacter) {
                    actualNPCCount++;
                }
            }
        }
        assertEquals(4, actualNPCCount);
    }


}