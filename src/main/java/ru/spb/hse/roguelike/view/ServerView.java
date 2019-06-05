package ru.spb.hse.roguelike.view;

import ru.spb.hse.roguelike.Point;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

/**
 * Class for woring with controller requests on server.
 */
public class ServerView extends View {
    @Override
    public void showChanges(Point point) {
        changedPoints.add(point);
    }

    @Override
    public CommandNameId readCommand() {
        return commandNameIds.poll();
    }

    @Override
    public void end() {
        isEnd = true;
    }

    private Queue<CommandNameId> commandNameIds = new ArrayDeque<>();
    private List<Point> changedPoints = new ArrayList<>();
    private boolean isEnd = false;

    /**
     * Add new command to execute
     * @param commandName name of a command
     * @param playerId id of a player
     */
    public void addCommand(CommandName commandName, int playerId) {
        commandNameIds.offer(new CommandNameId(commandName, playerId));
    }

    /**
     * et changes made since last check.
     * @return list of changes points
     */
    public List<Point> getChanges() {
        List<Point> answer = changedPoints;
        changedPoints.clear();
        return answer;
    }

    /**
     * @return number of waiting commands
     */
    public int getcommandNameIdsSize() {
        return commandNameIds.size();
    }

    /**
     * @return true if the game ends
     */
    public boolean isEnd() {
        return isEnd;
    }
}
