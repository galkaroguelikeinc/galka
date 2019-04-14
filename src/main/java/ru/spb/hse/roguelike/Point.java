package ru.spb.hse.roguelike;

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

}
