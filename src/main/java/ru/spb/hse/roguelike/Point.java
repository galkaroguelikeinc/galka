package ru.spb.hse.roguelike;

import java.util.Objects;

public class Point {
    private final int row;
    private final int col;

    public Point(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int getCol() {
        return col;
    }

    public int getRow() {
        return row;
    }

    public Point add(Point point) {
        return new Point(this.row + point.row, this.col + point.col);
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

    @Override
    public String toString() {
        return "Point{" + "\n" +
                "row=" + row + "\n" +
                ",col=" + col + "\n" +
                "}";
    }
}