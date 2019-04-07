package ru.spb.hse.roguelike.model;

import ru.spb.hse.roguelike.model.map.GameCell;
import ru.spb.hse.roguelike.model.map.GameMapCellType;
import ru.spb.hse.roguelike.model.object.alive.GameCharacter;
import ru.spb.hse.roguelike.model.object.items.Item;

import javax.annotation.Nonnull;
import java.util.*;

/**
 * Class to generate game model pieces: rooms, inventory, character and mobs.
 */
public class Generator {
    private static final int MIN_ROOM_HEIGHT = 1;
    private static final int MIN_ROOM_WIDTH = 1;
    private static final int MAX_ROOM_HEIGHT = 5;
    private static final int MAX_ROOM_WIDTH = 5;
    private static final int INDENT = 0;
    private static final int MAX_FAILED_CREATING_ROOM_ATTEMPT_COUNT = 10;
    private static final int MAX_REGENERATION_COUNT = 1000;
    private static final Random RANDOM = new Random();

    public static GameModel generateModel(int roomCount,
                                          int width,
                                          int height) throws MapGeneratorException {
        List<Room> rooms = generateRooms(roomCount, width, height);
        GameCell[][] map = generateMap(rooms, width, height);
        Room characterRoom = rooms.get(RANDOM.nextInt(roomCount));
        GameCharacter character = generateCharacter();
        int row = characterRoom.x + RANDOM.nextInt(characterRoom.w);
        int col = characterRoom.y + RANDOM.nextInt(characterRoom.h);
        map[row][col].addAliveObject(character);
        List<Item> inventories = generateInventories();
        return new GameModel(map, inventories, character);
    }

    private static List<Room> generateRooms(int roomCount,
                                            int width,
                                            int height) throws MapGeneratorException {

        List<Room> rooms = new ArrayList<>();
        int failedCreatingRoomAttemptCount = 0;
        int regenerationCount = 0;
        while (rooms.size() < roomCount) {
            int roomWidth = MIN_ROOM_WIDTH + RANDOM.nextInt(MAX_ROOM_WIDTH - MIN_ROOM_WIDTH + 1);
            int roomHeight = MIN_ROOM_HEIGHT + RANDOM.nextInt(MAX_ROOM_HEIGHT - MIN_ROOM_HEIGHT + 1);
            int roomX = INDENT + RANDOM.nextInt(width - roomWidth - 2 * INDENT + 1);
            int roomY = INDENT + RANDOM.nextInt(height - roomHeight - 2 * INDENT + 1);
            Room curRoom = new Room(roomX, roomY, roomWidth, roomHeight);
            boolean ok = rooms.parallelStream().noneMatch(room -> room.intersect(curRoom, 2));
            if (ok) {
                rooms.add(curRoom);
                failedCreatingRoomAttemptCount = 0;
            } else {
                failedCreatingRoomAttemptCount++;
            }
            if (failedCreatingRoomAttemptCount > MAX_FAILED_CREATING_ROOM_ATTEMPT_COUNT) {
                failedCreatingRoomAttemptCount = 0;
                regenerationCount++;
                rooms.clear();
            }
            if (regenerationCount >= MAX_REGENERATION_COUNT) {
                throw new MapGeneratorException("Failed to generate map");
            }
        }
        return rooms;
    }

    private static GameCell[][] generateMap(List<Room> rooms,
                                            int width,
                                            int height) throws MapGeneratorException {
        GameCell[][] data = new GameCell[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                data[x][y] = new GameCell(GameMapCellType.EMPTY, null, null);
            }
        }
        markRooms(width, height, rooms, data);
        for (int i = 0; i < rooms.size() - 1; i++) {
            createPath(rooms.get(i), rooms.get(i + 1), width, height, data);
        }
        //markWall(data);
        return data;
    }

    private static GameCharacter generateCharacter() {
        return new GameCharacter();
    }

    public static void generateMobsIfNeeded() {

    }

    private static List<Item> generateInventories() {
        return new ArrayList<>();
    }


    private static void markRooms(int width,
                                  int height,
                                  @Nonnull List<Room> rooms,
                                  @Nonnull GameCell[][] data) {
        rooms.parallelStream().forEach(room -> {
            for (int x = 0; x < room.w; x++) {
                for (int y = 0; y < room.h; y++) {
                    if (room.x + x < width
                            && room.y + y < height
                            && data[room.x + x][room.y + y].getGameMapCellType() == GameMapCellType.EMPTY) {
                        data[room.x + x][room.y + y].setGameMapCellType(GameMapCellType.ROOM);
                    }
                }
            }
        });
    }


    private static void createPath(@Nonnull Room startRoom,
                                   @Nonnull Room finishRoom,
                                   int width,
                                   int height,
                                   @Nonnull GameCell[][] data) throws MapGeneratorException {
        Point startPoint = startRoom.getMiddle();
        Point curPoint = new Point(startPoint.x, startPoint.y);
        Set<Point> visit = new HashSet<>();
        final TreeSet<Point> active = new TreeSet<>(
                Comparator.<Point>comparingInt(p -> cost(p, finishRoom))
                        .thenComparingInt(p -> p.x)
                        .thenComparingInt(p -> p.y));
        int[][] directions = {{1, 0}, {0, 1}, {-1, 0}, {0, -1}};
        HashMap<Point, Point> parent = new HashMap<>();
        while (!finishRoom.isPointInside(curPoint)) {
            visit.add(curPoint);
            for (int i = 0; i < 4; i++) {
                Point p = new Point(curPoint.x + directions[i][0],
                        curPoint.y + directions[i][1]);
                if (!visit.contains(p)
                        && p.x > 0
                        && p.x < width
                        && p.y > 0
                        && p.y < height) {
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

    private static void markPath(@Nonnull GameCell[][] data,
                                 @Nonnull Point start,
                                 @Nonnull Point finish,
                                 @Nonnull HashMap<Point, Point> parent) {
        Point curPoint = new Point(finish.x, finish.y);
        while (!curPoint.equals(start)) {
            if (data[curPoint.x][curPoint.y].getGameMapCellType() == GameMapCellType.EMPTY) {
                data[curPoint.x][curPoint.y].setGameMapCellType(GameMapCellType.TUNNEL);
            }
            curPoint = parent.get(curPoint);
        }
    }

    private static int cost(@Nonnull Point point,
                            @Nonnull Room room) {
        Point middle = room.getMiddle();
        return (point.x - middle.x) * (point.x - middle.x) +
                (point.y - middle.x) * (point.y - middle.y);
    }

    private static class Point {
        private int x;
        private int y;

        private Point(int x, int y) {
            this.x = x;
            this.y = y;

        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Point that = (Point) o;
            return x == that.x && y == that.y;
        }
    }

    private static class Room {
        private int x;
        private int y;
        private int w;
        private int h;


        private Room(int x,
                     int y,
                     int w,
                     int h) {
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
        }

        private boolean isPointInside(@Nonnull Point point) {
            if (point.x < x) {
                return false;
            }

            if (point.y < y) {
                return false;
            }

            if (point.x > x + w) {
                return false;
            }

            return point.y <= y + h;
        }

        private Point getMiddle() {
            return new Point(x + w / 2, y + w / 2);
        }

        private boolean intersect(Room r,
                                  int indent) {
            return !(r.x >= (x + w) + indent ||
                    x >= (r.x + r.w) + indent ||
                    r.y >= (y + h) + indent
                    || y >= (r.y + r.h) + indent);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Room that = (Room) o;
            return x == that.x
                    && y == that.y
                    && w == that.w
                    && h == that.h;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y, h, w);
        }
    }

}
