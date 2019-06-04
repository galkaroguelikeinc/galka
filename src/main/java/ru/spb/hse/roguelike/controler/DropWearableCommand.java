package ru.spb.hse.roguelike.controler;

import ru.spb.hse.roguelike.model.UnknownObjectException;
import ru.spb.hse.roguelike.view.ViewException;

/**
 * A command which makes the character drop its top wearable item.
 */
public class DropWearableCommand extends Command {
    DropWearableCommand(Controller controller) {
        super(controller);
    }

    @Override
    boolean execute(int playerId) throws UnknownObjectException, ViewException {
        return controller.dropWearable(playerId);
    }
}
