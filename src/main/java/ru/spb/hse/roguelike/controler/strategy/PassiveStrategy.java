package ru.spb.hse.roguelike.controler.strategy;

import ru.spb.hse.roguelike.model.GameModel;
import ru.spb.hse.roguelike.Point;

import javax.annotation.Nonnull;

public class PassiveStrategy implements MobStrategy {
    @Override
    public Point move(@Nonnull GameModel gameModel,
                      @Nonnull Point mobPoint) {
        return mobPoint;
    }
}
