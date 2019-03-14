package ru.spb.hse.roguelike.model.object.items;

import ru.spb.hse.roguelike.model.object.alive.GameCharacter;

public abstract class Food extends Item {
    private int hungerImprover;
    Food(GameCharacter gameCharacter, int x) {
        super(gameCharacter);
        hungerImprover = x;
    }

    @Override
    public void use() {
        getGameCharacter().changeHunger(hungerImprover);
    }
}
