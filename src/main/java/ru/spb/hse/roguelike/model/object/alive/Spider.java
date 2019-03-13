package ru.spb.hse.roguelike.model.object.alive;

import ru.spb.hse.roguelike.model.object.UnknownCharacteristicException;

public class Spider extends Mob {
    Spider(int x, int y) throws GameCharacterException {
        super(x, y);
        try {
            super.addCharacteristic("health", 1);
            super.addCharacteristic("attack power", 1);
        } catch (UnknownCharacteristicException e) {
            throw new GameCharacterException(e.getMessage());
        }
    }
}
