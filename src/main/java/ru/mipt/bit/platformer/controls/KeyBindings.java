package ru.mipt.bit.platformer.controls;

import ru.mipt.bit.platformer.mechanics.Direction;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.badlogic.gdx.Input.Keys.*;

public final class KeyBindings {

    private KeyBindings() {
    }

    // keys are checked in the order they are bound
    public static Map<Integer, Direction> wasdAndArrows() {
        Map<Integer, Direction> bindings = new LinkedHashMap<>();
        bindings.put(W, Direction.UP);
        bindings.put(UP, Direction.UP);
        bindings.put(DOWN, Direction.DOWN);
        bindings.put(S, Direction.DOWN);
        bindings.put(LEFT, Direction.LEFT);
        bindings.put(A, Direction.LEFT);
        bindings.put(RIGHT, Direction.RIGHT);
        bindings.put(D, Direction.RIGHT);
        return bindings;
    }
}
