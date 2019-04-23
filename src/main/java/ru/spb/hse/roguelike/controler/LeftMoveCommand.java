package ru.spb.hse.roguelike.controler;

import ru.spb.hse.roguelike.model.UnknownObjectException;
import ru.spb.hse.roguelike.view.ViewException;

/**
 * Command to move the character left.
 */
class LeftMoveCommand extends Command {
    LeftMoveCommand(Controller controller) {
        super(controller);
    }

    @Override
    boolean execute() throws UnknownObjectException, ViewException {
        return controller.handleMove(0, -1);
    }
}
