package ru.mipt.bit.platformer.controls;

import ru.mipt.bit.platformer.mechanics.CollisionChecker;
import ru.mipt.bit.platformer.mechanics.Direction;
import ru.mipt.bit.platformer.mechanics.Movable;

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
        this.keyBindings = keyBindings;
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
