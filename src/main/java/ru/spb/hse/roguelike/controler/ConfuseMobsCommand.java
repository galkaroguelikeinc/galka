package ru.spb.hse.roguelike.controler;

/**
 * Command to confuse mobs.
 */
class ConfuseMobsCommand extends Command {
    ConfuseMobsCommand(Controller controller) {
        super(controller);
    }

    @Override
    boolean execute() {
        return controller.confuseMobs();
    }
}
