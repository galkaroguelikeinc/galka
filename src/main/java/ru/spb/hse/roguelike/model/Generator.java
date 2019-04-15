package ru.spb.hse.roguelike.model;

import ru.spb.hse.roguelike.model.map.GameCell;
import ru.spb.hse.roguelike.model.map.GameMapCellType;
import ru.spb.hse.roguelike.model.object.alive.GameCharacter;
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
                pointForGameCharacter.row,
                pointForGameCharacter.col);
        List<Item> inventories = generateInventories();
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
        Point curPoint = new Point(startPoint.row, startPoint.col);
        Set<Point> visit = new HashSet<>();
        final TreeSet<Point> active = new TreeSet<>(
                Comparator.<Point>comparingInt(p -> cost(p, finishRoom))
                        .thenComparingInt(p -> p.row)
                        .thenComparingInt(p -> p.col));
        HashMap<Point, Point> parent = new HashMap<>();
        while (!finishRoom.isPointInside(curPoint)) {
            visit.add(curPoint);
            for (Direction d : Direction.values()) {
                Point p = new Point(curPoint.row + d.dx, curPoint.col + d.dy);
                if (!visit.contains(p)
                        && p.row > 0
                        && p.row < height
                        && p.col > 0
                        && p.col < width) {
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
        Point curPoint = new Point(finish.row, finish.col);
        while (!curPoint.equals(start)) {
            if (data[curPoint.row][curPoint.col].getGameMapCellType() == GameMapCellType.EMPTY) {
                data[curPoint.row][curPoint.col].setGameMapCellType(GameMapCellType.TUNNEL);
            }
            curPoint = parent.get(curPoint);
        }
    }

    private int cost(@Nonnull Point point,
                     @Nonnull Room room) {
        Point middle = room.getMiddle();
        return (point.row - middle.row) * (point.row - middle.row) +
                (point.col - middle.col) * (point.col - middle.col);
    }

    enum Direction {
        LEFT(-1, 0),
        UP(0, 1),
        RIGHT(1, 0),
        DOWN(0, -1);

        public final int dx;
        public final int dy;

        Direction(int dx, int dy) {
            this.dx = dx;
            this.dy = dy;
        }
    }

    private class Point {
        private int row;
        private int col;

        private Point(int row, int col) {
            this.row = row;
            this.col = col;

        }

        @Override
        public int hashCode() {
            return Objects.hash(row, col);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Point that = (Point) o;
            return row == that.row && col == that.col;
        }
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
            if (point.row < row) {
                return false;
            }

            if (point.col < col) {
                return false;
            }

            if (point.row >= row + height) {
                return false;
            }

            return point.col < col + width;
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
