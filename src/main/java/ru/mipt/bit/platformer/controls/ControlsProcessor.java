package ru.mipt.bit.platformer.controls;

import ru.mipt.bit.platformer.mechanics.CollisionChecker;
import ru.mipt.bit.platformer.mechanics.Movable;
import ru.mipt.bit.platformer.mechanics.Direction;

import java.util.Map;

public class ControlsProcessor {
    private final Movable playerMovement;
    private final CollisionChecker environment;
    private final KeyboardState keyboard;
    private final Map<Integer, Direction> keyBindings;

    public ControlsProcessor(Movable player, CollisionChecker map, KeyboardState keyboardState, Map<Integer, Direction> keyBindings) {
        playerMovement = player;
        environment = map;
        keyboard = keyboardState;
        this.keyBindings = keyBindings;
    }

    public void processMovement() {
        for (Map.Entry<Integer, Direction> binding : keyBindings.entrySet()) {
            if (keyboard.isKeyPressed(binding.getKey())) {
                Direction move = binding.getValue();
                playerMovement.tryMovement(move.dx(), move.dy(), move.rotation(), environment);
            }
        }
    }

    public void processInput() {
        processMovement();
    }
}
