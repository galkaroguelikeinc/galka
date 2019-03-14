package ru.spb.hse.roguelike.model;

import ru.spb.hse.roguelike.exceptions.MapGeneratorException;
import ru.spb.hse.roguelike.model.map.GameCell;
import ru.spb.hse.roguelike.model.map.GameMapCellType;
import ru.spb.hse.roguelike.model.object.alive.GameCharacter;
import ru.spb.hse.roguelike.model.object.items.Item;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

public class Generator {
    private static int MIN_ROOM_HEIGHT = 1;
    private static int MIN_ROOM_WIDTH = 1;
    private static int MAX_ROOM_HEIGHT = 3;
    private static int MAX_ROOM_WIDTH = 3;
    private static int INDENT = 0;
    private static int MAX_REGENERATION_COUNT = 1000;


    public static GameModel generateModel(int roomCount,
                                          int width,
                                          int height) throws MapGeneratorException {
        final GameCell[][] map = generateMap(roomCount, width, height);
        final List<Item> inventories = generateInventories();
        return new GameModel(map, inventories);
    }

    private static GameCell[][] generateMap(int roomCount,
                                            int width,
                                            int height) throws MapGeneratorException {
        final List<Room> rooms = new ArrayList<>();
        final Random RANDOM = new Random();
        int failedCreatingRoomAttemptCount = 0;
        int regenerationCount = 0;
        while (rooms.size() < roomCount) {
            final int roomWidth = MIN_ROOM_WIDTH + RANDOM.nextInt(MAX_ROOM_WIDTH - MIN_ROOM_WIDTH + 1);
            final int roomHeight = MIN_ROOM_HEIGHT + RANDOM.nextInt(MAX_ROOM_HEIGHT - MIN_ROOM_HEIGHT + 1);
            final int roomX = INDENT + RANDOM.nextInt(width - roomWidth - 2 * INDENT + 1);
            final int roomY = INDENT + RANDOM.nextInt(height - roomHeight - 2 * INDENT + 1);
            final Room curRoom = new Room(roomX, roomY, roomWidth, roomHeight);
            final boolean ok = rooms.parallelStream().noneMatch(room -> room.intersect(curRoom, 2));
            if (ok) {
                rooms.add(curRoom);
                failedCreatingRoomAttemptCount = 0;
            } else {
                failedCreatingRoomAttemptCount++;
            }
            if (failedCreatingRoomAttemptCount > 10 && regenerationCount < MAX_REGENERATION_COUNT) {
                failedCreatingRoomAttemptCount = 0;
                regenerationCount++;
                rooms.clear();
            }
            if (regenerationCount >= MAX_REGENERATION_COUNT) {
                throw new MapGeneratorException("Failed to generate map");
            }
        }


        final GameCell[][] data = new GameCell[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                data[x][y] = new GameCell(GameMapCellType.empty, null, null);
            }
        }
        markRooms(width, height, rooms, data);
        for (int i = 0; i < rooms.size() - 1; i++) {
            createPath(rooms.get(i), rooms.get(i + 1), width, height, data);
        }
        markWall(data);
        return data;
    }

    public static GameCharacter generateCharacter() {
        //create character with position, if no character exists
        return null;
    }

    public static void generateMobsIfNeeded() {

    }

    private static List<Item> generateInventories() {
        return new ArrayList<>();
    }


    private static void markRooms(int width,
                                  int height,
                                  @Nonnull final List<Room> rooms,
                                  @Nonnull GameCell[][] data) {
        rooms.parallelStream().forEach(room -> {
            for (int x = 0; x < room.w; x++) {
                for (int y = 0; y < room.h; y++) {
                    if (room.x + x < width
                            && room.y + y < height
                            && data[room.x + x][room.y + y].getGameMapCellType() == GameMapCellType.empty) {
                        data[room.x + x][room.y + y].setGameMapCellType(GameMapCellType.room);
                    }
                }
            }
        });
    }


    private static void createPath(@Nonnull final Room startRoom,
                                   @Nonnull final Room finishRoom,
                                   final int width,
                                   final int height,
                                   @Nonnull GameCell[][] data) throws MapGeneratorException {

        final Point startPoint = getMiddleOfRoom(startRoom);
        Point curPoint = new Point(startPoint.x, startPoint.y);
        final Set<Point> visit = new HashSet<>();
        final TreeSet<Point> active = new TreeSet<>((o1, o2) -> {
            int cost1 = cost(o1, finishRoom);
            int cost2 = cost(o2, finishRoom);
            if (cost1 != cost2) {
                return cost1 - cost2;
            }
            if (o1.x != o2.x) {
                return o1.x - o2.x;
            }
            return o1.y - o2.y;
        });
        final int[][] directions = {{1, 0}, {0, 1}, {-1, 0}, {0, -1}};
        final HashMap<Point, Point> parent = new HashMap<>();
        while (!isPointInsideRoom(curPoint, finishRoom)) {
            visit.add(curPoint);

            for (int i = 0; i < 4; i++) {
                final Point p = new Point(curPoint.x + directions[i][0],
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

    private static void markWall(@Nonnull GameCell[][] data) {
        final int[][] directions = {{1, 0}, {0, 1}, {-1, 0}, {0, -1}, {1, 1}, {1, -1}, {-1, -1}, {-1, 1}};
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[i].length; j++) {
                if (data[i][j].getGameMapCellType() == GameMapCellType.room
                        || data[i][j].getGameMapCellType() == GameMapCellType.tunnel) {
                    for (int[] direction : directions) {
                        int newI = i + direction[0];
                        int newJ = j + direction[1];
                        if (newI > 0 && newI < data.length
                                && newJ > 0 && newJ < data[i].length
                                && data[newI][newJ].getGameMapCellType() == GameMapCellType.empty) {
                            data[newI][newJ].setGameMapCellType(GameMapCellType.wall);
                        }
                    }
                }
            }
        }
    }

    private static void markPath(@Nonnull GameCell[][] data,
                                 @Nonnull final Point start,
                                 @Nonnull final Point finish,
                                 @Nonnull final HashMap<Point, Point> parent) {
        Point curPoint = new Point(finish.x, finish.y);
        while (!curPoint.equals(start)) {
            if (data[curPoint.x][curPoint.y].getGameMapCellType() == GameMapCellType.empty) {
                data[curPoint.x][curPoint.y].setGameMapCellType(GameMapCellType.tunnel);
            }
            curPoint = parent.get(curPoint);
        }
    }

    private static boolean isPointInsideRoom(@Nonnull final Point point,
                                             @Nonnull final Room room) {
        if (point.x < room.x) {
            return false;
        }

        if (point.y < room.y) {
            return false;
        }

        if (point.x > room.x + room.w) {
            return false;
        }

        return point.y <= room.y + room.h;
    }

    private static int cost(@Nonnull final Point point,
                            @Nonnull final Room room) {
        final Point middle = getMiddleOfRoom(room);
        return (point.x - middle.x) * (point.x - middle.x) +
                (point.y - middle.x) * (point.y - middle.y);
    }

    private static Point getMiddleOfRoom(@Nonnull final Room room) {
        return new Point(room.x + room.w / 2,
                room.y + room.w / 2);
    }

    private static class Point {
        private final int x;
        private final int y;

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
        private final int x;
        private final int y;
        private final int w;
        private final int h;


        private Room(int x,
                     int y,
                     int w,
                     int h) {
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
        }

        private boolean intersect(final Room r,
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
