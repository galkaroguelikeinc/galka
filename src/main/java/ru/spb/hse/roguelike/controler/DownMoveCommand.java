package ru.spb.hse.roguelike.controler;

import ru.spb.hse.roguelike.model.UnknownObjectException;
import ru.spb.hse.roguelike.view.ViewException;

/**
 * Command to move the character down.
 */
class DownMoveCommand extends Command {
    DownMoveCommand(Controller controller) {
        super(controller);
    }

    @Override
    boolean execute() throws UnknownObjectException, ViewException {
        return controller.handleMove(1, 0);
    }
}
