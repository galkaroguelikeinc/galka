package ru.spb.hse.roguelike.controler;

import ru.spb.hse.roguelike.model.UnknownObjectException;
import ru.spb.hse.roguelike.view.ViewException;

abstract class Command {
    Controller controller;

    abstract boolean execute() throws UnknownObjectException, ViewException;

    Command(Controller controller) {
        this.controller = controller;
    }
}
