package ru.spb.hse.roguelike.model.strategy;

import ru.spb.hse.roguelike.model.map.GameCell;
import ru.spb.hse.roguelike.model.object.alive.Mob;

import javax.annotation.Nonnull;

public class PassiveStrategy implements MobStrategy {
    @Override
    public void move(@Nonnull GameCell[][] map, @Nonnull Mob mob) {

    }
}
