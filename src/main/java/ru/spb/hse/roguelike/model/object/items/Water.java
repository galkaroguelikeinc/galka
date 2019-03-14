package ru.spb.hse.roguelike.model.object.items;

import ru.spb.hse.roguelike.model.object.alive.GameCharacter;

public class Water extends Food {
    public Water(GameCharacter gameCharacter) {
        super(gameCharacter, 1);
    }
}
