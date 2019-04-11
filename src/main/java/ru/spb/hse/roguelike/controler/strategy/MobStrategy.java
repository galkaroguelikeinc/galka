package ru.spb.hse.roguelike.controler.strategy;

import ru.spb.hse.roguelike.model.GameModel;
import ru.spb.hse.roguelike.model.map.Point;

import javax.annotation.Nonnull;

public interface MobStrategy {
    Point move(@Nonnull GameModel gameModel,
               @Nonnull Point mobPoint) throws StrategyException;
}
