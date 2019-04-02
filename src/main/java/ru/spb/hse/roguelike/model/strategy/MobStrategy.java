package ru.spb.hse.roguelike.model.strategy;

import ru.spb.hse.roguelike.model.map.GameCell;
import ru.spb.hse.roguelike.model.object.alive.GameCharacter;
import ru.spb.hse.roguelike.model.object.alive.Mob;

import javax.annotation.Nonnull;

public interface MobStrategy {
    void move(@Nonnull final GameCell[][] map,
              @Nonnull final Mob mob,
              @Nonnull final GameCharacter gameCharacter);
}
