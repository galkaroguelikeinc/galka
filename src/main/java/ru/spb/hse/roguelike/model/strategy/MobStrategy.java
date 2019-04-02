package ru.spb.hse.roguelike.model.strategy;

import ru.spb.hse.roguelike.model.GameModel;
import ru.spb.hse.roguelike.model.object.alive.Mob;

import javax.annotation.Nonnull;

public interface MobStrategy {
    void move(@Nonnull GameModel gameModel,
              @Nonnull Mob mob);
}
