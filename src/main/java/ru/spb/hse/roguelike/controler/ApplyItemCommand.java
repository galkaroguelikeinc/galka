package ru.spb.hse.roguelike.controler;

/**
 * A command which makes the character pick up and apply an item in current cell.
 */
class ApplyItemCommand extends Command {
    ApplyItemCommand(Controller controller) {
        super(controller);
    }

    @Override
    boolean execute(int playerId) {
        return controller.applyItem(playerId);
    }
}
