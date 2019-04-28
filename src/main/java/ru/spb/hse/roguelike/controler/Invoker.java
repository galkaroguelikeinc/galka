package ru.spb.hse.roguelike.controler;

import ru.spb.hse.roguelike.model.UnknownObjectException;
import ru.spb.hse.roguelike.view.CommandName;
import ru.spb.hse.roguelike.view.ViewException;

import java.util.HashMap;
import java.util.Map;

/**
 * Class to run commands by they name.
 */
class Invoker {
    private Map<CommandName, Command> commands = new HashMap<>();

    /**
     * Execute a command with a given name
     *
     * @param name name of a command
     * @return true is the game is not ended
     */
    boolean executeCommand(CommandName name) throws UnknownObjectException, ViewException {
        return commands.get(name).execute();
    }

    /**
     * Sets up a command for the given name.
     *
     * @param commandName name of a command
     * @param command     command to execute when needed
     */
    void setCommand(CommandName commandName, Command command) {
        commands.put(commandName, command);
    }
}
