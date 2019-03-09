import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public abstract class GameObject {
    private Map<String, Characteristic> characteristics = new HashMap<>();

    public void addCharacteristic(String name, int value) throws UnknownCharacteristicException {
        this.characteristics.put(name, CharacteristicCreator.create(name, value));
    }

    public Characteristic getCharacteristic(String name) {
        if (characteristics.containsKey(name)) {
            return characteristics.get(name);
        }
        throw new NoSuchElementException(name);
    }
}
