package ru.spb.hse.roguelike.model.object.items;

import ru.spb.hse.roguelike.model.object.alive.GameCharacter;
import ru.spb.hse.roguelike.model.object.GameObject;

public abstract class Item extends GameObject {
    public abstract void use(GameCharacter character);
}
