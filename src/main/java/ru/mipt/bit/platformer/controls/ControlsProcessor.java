package ru.mipt.bit.platformer.controls;

import java.util.ArrayList;
import java.util.List;

public class ControlsProcessor {
    private final List<ControlStrategy> strategies;

    public ControlsProcessor(List<ControlStrategy> strategies) {
        this.strategies = new ArrayList<>(strategies);
    }

    public void processInput() {
        executeCommands(createCommands());
    }

    public List<Command> createCommands() {
        List<Command> commands = new ArrayList<>();
        for (ControlStrategy strategy : strategies) {
            strategy.nextCommand().ifPresent(commands::add);
        }
        return commands;
    }

    public void executeCommands(List<Command> commands) {
        for (Command command : commands) command.execute();
    }
}
