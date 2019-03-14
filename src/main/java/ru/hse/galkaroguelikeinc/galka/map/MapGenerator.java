package ru.hse.galkaroguelikeinc.galka.map;

import ru.hse.galkaroguelikeinc.galka.map.exceptions.MapGeneratorException;
import ru.hse.galkaroguelikeinc.galka.map.model.Map;
import ru.hse.galkaroguelikeinc.galka.map.model.Point;
import ru.hse.galkaroguelikeinc.galka.map.model.Room;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

public class MapGenerator {
    private static int MIN_ROOM_HEIGHT = 10;
    private static int MIN_ROOM_WIDTH = 10;
    private static int MAX_ROOM_HEIGHT = 40;
    private static int MAX_ROOM_WIDTH = 40;
    private static int INDENT = 3;
    private static int MAX_REGENERATION_COUNT = 1000;

    public Map generate(int roomCount,
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
            final boolean ok = rooms.parallelStream().noneMatch(room -> room.intersect(curRoom));
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


        final Boolean[][] data = new Boolean[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                data[x][y] = false;
            }
        }
        markRooms(width, height, rooms, data);
        for (int i = 0; i < rooms.size() - 1; i++) {
            createPath(rooms.get(i), rooms.get(i + 1), width, height, data);
        }
        return new Map(rooms, width, height, data);

    }


    private void markRooms(int width,
                           int height,
                           @Nonnull final List<Room> rooms,
                           @Nonnull Boolean[][] data) {
        rooms.parallelStream().forEach(room -> {
            for (int x = 0; x < room.getW(); x++) {
                for (int y = 0; y < room.getH(); y++) {
                    if (room.getX() + x < width && room.getY() + y < height) {
                        data[room.getX() + x][room.getY() + y] = true;
                    }
                }
            }
        });
    }


    private void createPath(@Nonnull final Room startRoom,
                            @Nonnull final Room finishRoom,
                            final int width,
                            final int height,
                            @Nonnull Boolean[][] data) throws MapGeneratorException {

        final Point startPoint = getMiddleOfRoom(startRoom);
        Point curPoint = new Point(startPoint.getX(), startPoint.getY());
        final Set<Point> visit = new HashSet<>();
        final TreeSet<Point> active = new TreeSet<>((o1, o2) -> {
            int cost1 = cost(o1, finishRoom);
            int cost2 = cost(o2, finishRoom);
            if (cost1 != cost2) {
                return cost1 - cost2;
            }
            if (o1.getX() != o2.getX()) {
                return o1.getX() - o2.getX();
            }
            return o1.getY() - o2.getY();
        });
        final int[][] directions = {{1, 0}, {0, 1}, {-1, 0}, {0, -1}};
        final HashMap<Point, Point> parent = new HashMap<>();
        while (!isPointInsideRoom(curPoint, finishRoom)) {
            visit.add(curPoint);

            for (int i = 0; i < 4; i++) {
                final Point p = new Point(curPoint.getX() + directions[i][0],
                        curPoint.getY() + directions[i][1]);
                if (!visit.contains(p)
                        && p.getX() > 0
                        && p.getX() < width
                        && p.getY() > 0
                        && p.getY() < height) {
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


    private void markPath(@Nonnull Boolean[][] data,
                          @Nonnull final Point start,
                          @Nonnull final Point finish,
                          @Nonnull final HashMap<Point, Point> parent) {
        Point curPoint = new Point(finish.getX(), finish.getY());
        while (!curPoint.equals(start)) {
            data[curPoint.getX()][curPoint.getY()] = true;
            curPoint = parent.get(curPoint);
        }
    }

    private boolean isPointInsideRoom(@Nonnull final Point point,
                                      @Nonnull final Room room) {
        if (point.getX() < room.getX()) {
            return false;
        }

        if (point.getY() < room.getY()) {
            return false;
        }

        if (point.getX() > room.getX() + room.getW()) {
            return false;
        }

        if (point.getY() > room.getY() + room.getH()) {
            return false;
        }
        return true;
    }

    private int cost(@Nonnull final Point point,
                     @Nonnull final Room room) {
        final Point middle = getMiddleOfRoom(room);
        return (point.getX() - middle.getX()) * (point.getX() - middle.getX()) +
                (point.getY() - middle.getX()) * (point.getY() - middle.getY());
    }

    private Point getMiddleOfRoom(@Nonnull final Room room) {
        return new Point(room.getX() + room.getW() / 2,
                room.getY() + room.getW() / 2);
    }
}
