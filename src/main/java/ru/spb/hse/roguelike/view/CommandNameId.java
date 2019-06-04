package ru.spb.hse.roguelike.view;

public class CommandNameId {
    private final CommandName commandName;
    private final int id;

    public CommandNameId(CommandName commandName, int id) {
        this.commandName = commandName;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public CommandName getCommandName() {
        return commandName;
    }
}
