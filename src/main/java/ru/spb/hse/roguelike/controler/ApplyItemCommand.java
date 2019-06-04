package ru.spb.hse.roguelike.controler;

import ru.spb.hse.roguelike.model.UnknownObjectException;
import ru.spb.hse.roguelike.view.ViewException;

/**
 * A command which makes the character pick up and apply an item in current cell.
 */
public class ApplyItemCommand extends Command {
    ApplyItemCommand(Controller controller) {
        super(controller);
    }

    @Override
    boolean execute(int playerId) throws UnknownObjectException, ViewException {
        return controller.applyItem(playerId);
    }
}
