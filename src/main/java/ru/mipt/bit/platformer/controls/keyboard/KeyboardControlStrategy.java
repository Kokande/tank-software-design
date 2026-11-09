package ru.mipt.bit.platformer.controls.keyboard;

import ru.mipt.bit.platformer.controls.commands.Command;
import ru.mipt.bit.platformer.controls.commands.MoveCommand;
import ru.mipt.bit.platformer.controls.ControlStrategy;
import ru.mipt.bit.platformer.mechanics.collision.CollisionChecker;
import ru.mipt.bit.platformer.mechanics.movement.Direction;
import ru.mipt.bit.platformer.mechanics.movement.Movable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class KeyboardControlStrategy implements ControlStrategy {
    private final Movable movable;
    private final CollisionChecker environment;
    private final KeyboardState keyboard;
    private final Map<Integer, Direction> keyBindings;

    public KeyboardControlStrategy(Movable movable, CollisionChecker environment, KeyboardState keyboard, Map<Integer, Direction> keyBindings) {
        this.movable = movable;
        this.environment = environment;
        this.keyboard = keyboard;
        this.keyBindings = new LinkedHashMap<>(keyBindings);
    }

    @Override
    public Optional<Command> nextCommand() {
        // the first pressed key wins, keys are checked in the order they are bound
        for (Map.Entry<Integer, Direction> binding : keyBindings.entrySet()) {
            if (keyboard.isKeyPressed(binding.getKey())) {
                return Optional.of(new MoveCommand(movable, binding.getValue(), environment));
            }
        }
        return Optional.empty();
    }
}
