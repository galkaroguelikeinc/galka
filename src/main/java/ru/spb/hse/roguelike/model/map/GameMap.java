package ru.spb.hse.roguelike.model.map;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;

public class GameMap {
    @Nonnull
    private final List<Room> rooms;
    @Nonnull
    protected final GameCell[][] data;


    public GameMap(@Nonnull final List<Room> rooms,
                   @Nonnull final GameCell[][] data) {
        this.rooms = rooms;
        this.data = data;
    }

    @Nonnull
    public GameCell[][] getData() {
        return data;
    }

    @Nonnull
    public List<Room> getRooms() {
        return rooms;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameMap that = (GameMap) o;
        return Objects.equals(rooms, that.rooms)
                && Arrays.deepEquals(data, that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rooms, data);
    }

    @Override
    public String toString() {
        return "GameMap {" +
                "rooms = " + rooms + ", " +
                "}";
    }
}
