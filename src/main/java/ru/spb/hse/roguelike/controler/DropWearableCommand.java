package ru.spb.hse.roguelike.controler;

/**
 * A command which makes the character drop its top wearable item.
 */
class DropWearableCommand extends Command {
    DropWearableCommand(Controller controller) {
        super(controller);
    }

    @Override
    boolean execute(int playerId){
        return controller.dropWearable(playerId);
    }
}
