package ru.mipt.bit.platformer.util;

import ru.mipt.bit.platformer.objects.CollisionChecker;
import ru.mipt.bit.platformer.objects.Movable;

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
