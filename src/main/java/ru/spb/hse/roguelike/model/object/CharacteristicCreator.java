package ru.spb.hse.roguelike.model.object;

public class CharacteristicCreator {
    public static Characteristic create(String name, int value) throws UnknownCharacteristicException {
        switch (name) {
            case "health":
                return new MeasurableCharacteristic(value);
            case "attack power":
                return new MeasurableCharacteristic(value);
            default:
                throw new UnknownCharacteristicException(name);
        }
    }
}
