package ru.spb.hse.roguelike.model.map;

/**
 * Type of the game cell: wall, room or nothing. The type defines what the cell can collect. EMPTY cells can not have
 * mobs, items or game character, tunnels can not have items.
 */
public enum GameMapCellType {
    EMPTY, ROOM, TUNNEL
}
