package ru.spb.hse.roguelike.controler.strategy;

import ru.spb.hse.roguelike.Point;
import ru.spb.hse.roguelike.model.GameModel;

import javax.annotation.Nonnull;

/**
 * Implementation of passive mob strategy: just standing and doing nothing.
 */
public class PassiveStrategy extends NonPlayerCharacterStrategy {
    @Override
    public Point move(@Nonnull GameModel gameModel,
                      @Nonnull Point mobPoint) {
        return mobPoint;
    }
}
