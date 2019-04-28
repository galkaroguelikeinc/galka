package ru.spb.hse.roguelike.controler;

import ru.spb.hse.roguelike.model.UnknownObjectException;
import ru.spb.hse.roguelike.view.ViewException;

/**
 * Class representing user commands.
 */
abstract class Command {
    Controller controller;

    Command(Controller controller) {
        this.controller = controller;
    }

    /**
     * Executes the command
     *
     * @return true is the game continues
     */
    abstract boolean execute() throws UnknownObjectException, ViewException;
}
