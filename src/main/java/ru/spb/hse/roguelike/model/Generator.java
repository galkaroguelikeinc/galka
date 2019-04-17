package ru.spb.hse.roguelike.model;

import ru.spb.hse.roguelike.Point;
import ru.spb.hse.roguelike.model.map.Direction;
import ru.spb.hse.roguelike.model.map.GameCell;
import ru.spb.hse.roguelike.model.map.GameMapCellType;
import ru.spb.hse.roguelike.model.object.MeasurableCharacteristic;
import ru.spb.hse.roguelike.model.object.alive.GameCharacter;
import ru.spb.hse.roguelike.model.object.alive.NonPlayerCharacter;
import ru.spb.hse.roguelike.model.object.alive.NonPlayerCharacterStrategyType;
import ru.spb.hse.roguelike.model.object.items.Item;

import javax.annotation.Nonnull;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.function.Function;

/**
 * Class to generate game model pieces: rooms, inventory, character and mobs.
 */
public class Generator {
    private static final Random RANDOM = new Random();
    private final int minRoomHeight = 3;
    private final int minRoomWidth = 3;
    private final int maxRoomHeight = 7;
    private final int maxRoomWidth = 7;
    private final int indent = 1;
    private final int maxFailedCreatingRoomAttemptCount = 10;
    private final int maxRegenerationCount = 1000;
    private final int maxCountMobsInRoom = 2;
    public GameModel generateModel(int roomCount,
                                   int width,
                                   int height) throws MapGeneratorException {
        List<Room> rooms = generateRooms(roomCount, width, height);
        GameCell[][] map = generateMap(rooms, width, height);
        Room characterRoom = rooms.get(RANDOM.nextInt(roomCount));
        GameCharacter gameCharacter = generateCharacter(map,
                characterRoom.row + RANDOM.nextInt(characterRoom.height),
                characterRoom.col + RANDOM.nextInt(characterRoom.width));
        List<Item> inventories = generateInventories();
        generateMobs(rooms, map);
        return new GameModel(map, inventories, gameCharacter, 10);
    }

    public GameModel generateModel(String fileName, Function<Character, GameMapCellType> decoder)
            throws FileNotFoundException, MapGeneratorException {
        Scanner scanner = new Scanner(new FileInputStream(fileName));
        List<String> lines = new ArrayList<>();
        while (scanner.hasNextLine()) {
            lines.add(scanner.nextLine());
        }
        if (!isValidModel(lines)) {
            throw new MapGeneratorException("Invalid model from file " + fileName);
        }
        int width = lines.get(0).length();
        int height = lines.size();
        GameCell[][] map = new GameCell[height][width];
        List<Point> roomsPoint = new ArrayList<>();
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                map[i][j] = new GameCell(decoder.apply(lines.get(i).charAt(j)), null, null);
                if (decoder.apply(lines.get(i).charAt(j)) == GameMapCellType.ROOM) {
                    roomsPoint.add(new Point(i, j));
                }
            }
        }
        Point pointForGameCharacter = roomsPoint.get(RANDOM.nextInt(roomsPoint.size()));
        GameCharacter gameCharacter = generateCharacter(map,
                pointForGameCharacter.getRow(),
                pointForGameCharacter.getCol());
        List<Item> inventories = generateInventories();
        generateMobs(findRooms(map), map);
        return new GameModel(map, inventories, gameCharacter, 10);

    }

    private boolean isValidModel(List<String> lines) {
        if (lines == null || lines.size() == 0) {
            return false;
        }
        int width = lines.get(0).length();
        for (String line : lines) {
            if (line.length() != width) {
                return false;
            }
        }

        return true;
    }

    private List<Room> generateRooms(int roomCount,
                                     int width,
                                     int height) throws MapGeneratorException {

        List<Room> rooms = new ArrayList<>();
        int failedCreatingRoomAttemptCount = 0;
        int regenerationCount = 0;
        while (rooms.size() < roomCount) {
            int roomWidth = minRoomWidth + RANDOM.nextInt(maxRoomWidth - minRoomWidth + 1);
            int roomHeight = minRoomHeight + RANDOM.nextInt(maxRoomHeight - minRoomHeight + 1);
            int roomCol = indent + RANDOM.nextInt(width - roomWidth - 2 * indent + 1);
            int roomRow = indent + RANDOM.nextInt(height - roomHeight - 2 * indent + 1);
            Room curRoom = new Room(roomRow, roomCol, roomWidth, roomHeight);
            boolean ok = rooms.parallelStream().noneMatch(room -> room.intersect(curRoom, 2));
            if (ok) {
                rooms.add(curRoom);
                failedCreatingRoomAttemptCount = 0;
            } else {
                failedCreatingRoomAttemptCount++;
            }
            if (failedCreatingRoomAttemptCount > maxFailedCreatingRoomAttemptCount) {
                failedCreatingRoomAttemptCount = 0;
                regenerationCount++;
                rooms.clear();
            }
            if (regenerationCount >= maxRegenerationCount) {
                throw new MapGeneratorException("Failed to generate map");
            }
        }
        return rooms;
    }

    private List<Room> findRooms(GameCell[][] map) {
        List<Room> rooms = new ArrayList<>();
        for (int row = 0; row < map.length; row++) {
            for (int col = 0; col < map[row].length; col++) {
                if (map[row][col].getGameMapCellType() == GameMapCellType.ROOM) {
                    if ((row == 0 || map[row - 1][col].getGameMapCellType() != GameMapCellType.ROOM) &&
                            (col == 0 || map[row][col - 1].getGameMapCellType() != GameMapCellType.ROOM)) {
                        int width = findEndOfRoom(map, row, col, Direction.RIGHT);
                        int height = findEndOfRoom(map, row, col, Direction.UP);
                        rooms.add(new Room(row, col, width, height));
                    }
                }
            }
        }
        return rooms;
    }

    private int findEndOfRoom(GameCell[][] map, int beginRow, int beginCol, Direction direction) {
        int size = 1;
        int curRow = beginRow;
        int curCol = beginCol;
        while (curRow + direction.dCol < map.length
                && curCol + direction.dRow < map[curRow + direction.dCol].length
                && map[curRow + direction.dCol][curCol + direction.dRow].getGameMapCellType() == GameMapCellType.ROOM) {
            size++;
            curRow += direction.dCol;
            curCol += direction.dRow;
        }
        return size;
    }

    private GameCharacter generateCharacter(GameCell[][] map,
                                            int row,
                                            int col) {
        GameCharacter character = generateCharacter();
        map[row][col].addAliveObject(character);
        return character;
    }

    private GameCell[][] generateMap(List<Room> rooms,
                                     int width,
                                     int height) throws MapGeneratorException {
        GameCell[][] data = new GameCell[height][width];
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                data[row][col] = new GameCell(GameMapCellType.EMPTY, null, null);
            }
        }
        markRooms(width, height, rooms, data);
        for (int i = 0; i < rooms.size() - 1; i++) {
            createPath(rooms.get(i), rooms.get(i + 1), width, height, data);
        }
        return data;
    }

    private GameCharacter generateCharacter() {
        return new GameCharacter();
    }

    private void generateMobs(@Nonnull List<Room> rooms,
                              @Nonnull GameCell[][] map) {
        rooms.forEach(room -> generateMobsInRoom(room, map));
    }

    private void generateMobsInRoom(@Nonnull Room room,
                                    @Nonnull GameCell[][] map) {
        int fullMobsCount = RANDOM.nextInt(maxCountMobsInRoom - 1) + 1;
        NonPlayerCharacterStrategyType[] allTypes = NonPlayerCharacterStrategyType.values();
        int curMobsCount = 0;
        while (curMobsCount < fullMobsCount) {
            int row = room.row + RANDOM.nextInt(room.height);
            int col = room.col + RANDOM.nextInt(room.width);
            NonPlayerCharacterStrategyType strategyType = allTypes[RANDOM.nextInt(allTypes.length)];
            if (!map[row][col].hasAliveObject()) {
                map[row][col].addAliveObject(new NonPlayerCharacter(
                        new MeasurableCharacteristic(1),
                        new MeasurableCharacteristic(1),
                        strategyType));
                curMobsCount++;
            }
        }
    }

    private List<Item> generateInventories() {
        return new ArrayList<>();
    }

    private void markRooms(int width,
                           int height,
                           @Nonnull List<Room> rooms,
                           @Nonnull GameCell[][] data) {
        rooms.parallelStream().forEach(room -> {
            for (int row = 0; row < room.height; row++) {
                for (int col = 0; col < room.width; col++) {
                    if (room.row + row < height
                            && room.col + col < width
                            && data[room.row + row][room.col + col].getGameMapCellType() == GameMapCellType.EMPTY) {
                        data[room.row + row][room.col + col].setGameMapCellType(GameMapCellType.ROOM);
                    }
                }
            }
        });
    }

    private void createPath(@Nonnull Room startRoom,
                            @Nonnull Room finishRoom,
                            int width,
                            int height,
                            @Nonnull GameCell[][] data) throws MapGeneratorException {
        Point startPoint = startRoom.getMiddle();
        Point curPoint = startPoint;
        Set<Point> visit = new HashSet<>();
        final TreeSet<Point> active = new TreeSet<>(
                Comparator.<Point>comparingInt(p -> cost(p, finishRoom))
                        .thenComparingInt(Point::getRow)
                        .thenComparingInt(Point::getCol));
        HashMap<Point, Point> parent = new HashMap<>();
        while (!finishRoom.isPointInside(curPoint)) {
            visit.add(curPoint);
            for (Direction d : Direction.values()) {
                Point p = new Point(curPoint.getRow() + d.dRow, curPoint.getCol() + d.dCol);
                if (!visit.contains(p)
                        && p.getRow() > 0
                        && p.getRow() < height
                        && p.getCol() > 0
                        && p.getCol() < width) {
                    active.add(p);
                    parent.put(p, curPoint);
                }
            }
            if (active.isEmpty()) {
                throw new MapGeneratorException("Failed to create path");
            }
            curPoint = active.first();
            active.remove(curPoint);
        }
        markPath(data, startPoint, curPoint, parent);
    }

    private void markPath(@Nonnull GameCell[][] data,
                          @Nonnull Point start,
                          @Nonnull Point finish,
                          @Nonnull HashMap<Point, Point> parent) {
        Point curPoint = finish;
        while (!curPoint.equals(start)) {
            if (data[curPoint.getRow()][curPoint.getCol()].getGameMapCellType() == GameMapCellType.EMPTY) {
                data[curPoint.getRow()][curPoint.getCol()].setGameMapCellType(GameMapCellType.TUNNEL);
            }
            curPoint = parent.get(curPoint);
        }
    }

    private int cost(@Nonnull Point point,
                     @Nonnull Room room) {
        Point middle = room.getMiddle();
        return (point.getRow() - middle.getRow()) * (point.getRow() - middle.getRow()) +
                (point.getCol() - middle.getCol()) * (point.getCol() - middle.getCol());
    }


    private class Room {
        private int row;
        private int col;
        private int width;
        private int height;


        private Room(int row,
                     int col,
                     int width,
                     int height) {
            this.row = row;
            this.col = col;
            this.width = width;
            this.height = height;
        }

        private boolean isPointInside(@Nonnull Point point) {
            if (point.getRow() < row) {
                return false;
            }

            if (point.getCol() < col) {
                return false;
            }

            if (point.getRow() >= row + height) {
                return false;
            }

            return point.getCol() < col + width;
        }

        private Point getMiddle() {
            return new Point(row + height / 2, col + width / 2);
        }

        private boolean intersect(Room r,
                                  int indent) {
            return !(r.row > (row + height) + indent ||
                    row > (r.row + r.height) + indent ||
                    r.col > (col + width) + indent
                    || col > (r.col + r.width) + indent);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Room that = (Room) o;
            return row == that.row
                    && col == that.col
                    && width == that.width
                    && height == that.height;
        }

        @Override
        public int hashCode() {
            return Objects.hash(row, col, height, width);
        }
    }

}
