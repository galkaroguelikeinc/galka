package ru.hse.galkaroguelikeinc.galka.map.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import javax.annotation.Nonnull;

public class Map {
    private final List<Room> rooms;
    private final int width;
    private final int height;
    private final Boolean[][] data;


    public Map(@Nonnull final List<Room> rooms,
               int width,
               int height,
               @Nonnull final Boolean[][] data) {
        this.rooms = rooms;
        this.width = width;
        this.height = height;
        this.data = data;
    }

    public Boolean[][] getData() {
        return data;
    }

    public List<Room> getRooms() {
        return rooms;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Map that = (Map) o;
        return width == that.width
                && height == that.height
                && Objects.equals(rooms, that.rooms)
                && Arrays.deepEquals(data, that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(width, height, rooms, data);
    }

    @Override
    public String toString() {
        return "Map {" +
                "width = " + width + "," +
                "height = " + height + ", " +
                "rooms = " + rooms + ", " +
                "}";
    }
}
