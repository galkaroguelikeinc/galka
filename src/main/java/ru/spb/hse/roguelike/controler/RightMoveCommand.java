package ru.spb.hse.roguelike.controler;

import ru.spb.hse.roguelike.model.UnknownObjectException;
import ru.spb.hse.roguelike.view.ViewException;

/**
 * Command to move the character right.
 */
class RightMoveCommand extends Command {
    RightMoveCommand(Controller controller) {
        super(controller);
    }

    @Override
    boolean execute(int playerId) throws UnknownObjectException, ViewException {
        return controller.handleMove(playerId,0, 1);
    }
}
