package ru.spb.hse.roguelike.view;

import ru.spb.hse.roguelike.Point;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class ServerView extends View {
    @Override
    public void showChanges(Point point) throws ViewException {
        changedPoints.add(point);
    }

    @Override
    public CommandNameId readCommand() {
        return commandNameIds.poll();
    }

    @Override
    public void end() throws IOException, InterruptedException {
        isEnd = true;
    }

    private Queue<CommandNameId> commandNameIds = new ArrayDeque<>();
    private List<Point> changedPoints = new ArrayList<>();
    private boolean isEnd = false;

    public void addCommand(CommandName commandName, int playerId) {
        commandNameIds.offer(new CommandNameId(commandName, playerId));
    }

    public List<Point> getChanges() {
        List<Point> answer = changedPoints;
        changedPoints.clear();
        return answer;
    }

    public boolean isEnd() {
        return isEnd;
    }
}
