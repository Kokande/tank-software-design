package ru.mipt.bit.platformer.controls;

import java.util.ArrayList;
import java.util.List;

public class ControlsProcessor {
    private final List<ControlStrategy> strategies;

    public ControlsProcessor(List<ControlStrategy> strategies) {
        this.strategies = new ArrayList<>(strategies);
    }

    public void processInput() {
        for (ControlStrategy strategy : strategies) {
            strategy.nextCommand().ifPresent(Command::execute);
        }
    }
}
