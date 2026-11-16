package ru.mipt.bit.platformer.controls;

import ru.mipt.bit.platformer.controls.commands.Command;

import java.util.Optional;

// decides what the controlled object should do during the current frame
/**
 * Application port: supplies commands to the input-processing use case
 */
public interface ControlStrategy {
    Optional<Command> nextCommand();
}
