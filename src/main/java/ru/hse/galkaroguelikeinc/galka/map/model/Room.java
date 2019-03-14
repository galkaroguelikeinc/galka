package ru.hse.galkaroguelikeinc.galka.map.model;

import java.util.Objects;

public class Room {
    private final int x;
    private final int y;
    private final int w;
    private final int h;


    public Room(int x,
                int y,
                int w,
                int h) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }

    public int getH() {
        return h;
    }

    public int getW() {
        return w;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean intersect(final Room r) {
        return !(r.x >= (x + w) + 1 || x >= (r.x + r.w) + 1 || r.y >= (y + h) + 1 || y >= (r.y + r.h) + 1);
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

    @Override
    public String toString() {
        return "Room {" +
                "x = " + x + "," +
                "y = " + y + ", " +
                "h = " + h + ", " +
                "w = " + w + ", " +
                "}";
    }
}
