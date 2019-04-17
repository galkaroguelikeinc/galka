package ru.spb.hse.roguelike.controler.strategy;

import ru.spb.hse.roguelike.Point;
import ru.spb.hse.roguelike.model.GameModel;
import ru.spb.hse.roguelike.model.UnknownObjectException;
import ru.spb.hse.roguelike.model.map.Direction;
import ru.spb.hse.roguelike.model.map.GameCell;
import ru.spb.hse.roguelike.model.map.GameMapCellType;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RandomStrategy extends NonPlayerCharacterStrategy {
    private static boolean isNonEmptyCell(@Nonnull GameModel gameModel, @Nonnull Point point) {
        GameCell cell = gameModel.getCell(point);
        if (cell == null) {
            return false;
        }
        return cell.getGameMapCellType() != GameMapCellType.EMPTY;
    }

    @Override
    public Point move(@Nonnull GameModel gameModel, @Nonnull Point nonPlayerCharacterPoint) throws UnknownObjectException {
        if (isInvalid(gameModel, nonPlayerCharacterPoint)) {
            throw new SecurityException("Unable to get NPC from cell " + nonPlayerCharacterPoint);
        }

        List<Direction> directionList = Arrays.asList(Direction.values());
        Collections.shuffle(directionList);

        for (Direction d : directionList) {
            Point newPoint = nonPlayerCharacterPoint.add(d);
            if (newPoint != nonPlayerCharacterPoint &&
                    gameModel.hasCell(newPoint) &&
                    isNonEmptyCell(gameModel, newPoint)) {
                return newPoint;
            }
        }

        return nonPlayerCharacterPoint;
    }
}
