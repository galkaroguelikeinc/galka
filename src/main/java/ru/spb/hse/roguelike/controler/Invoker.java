package ru.spb.hse.roguelike.controler;

import ru.spb.hse.roguelike.model.UnknownObjectException;
import ru.spb.hse.roguelike.view.CommandName;
import ru.spb.hse.roguelike.view.ViewException;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class Invoker {
    private Map<CommandName, Command> commands = new HashMap<>();
    private Stack<Command> commandsHistory = new Stack<>();

    public boolean executeCommand(CommandName name) throws UnknownObjectException, ViewException {
        boolean result = commands.get(name).execute();
        commandsHistory.push(commands.get(name));
        return result;
    }

    public void setCommand(CommandName commandName, Command command) {
        commands.put(commandName, command);
    }

}
