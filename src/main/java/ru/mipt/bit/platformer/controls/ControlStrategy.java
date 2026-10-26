package ru.mipt.bit.platformer.controls;

import java.util.Optional;

// decides what the controlled object should do during the current frame
public interface ControlStrategy {
    Optional<Command> nextCommand();
}
