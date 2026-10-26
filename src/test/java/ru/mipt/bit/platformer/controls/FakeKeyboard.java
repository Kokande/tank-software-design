package ru.mipt.bit.platformer.controls;

import java.util.HashSet;
import java.util.Set;

// keyboard which reports exactly the keys a test has pressed
class FakeKeyboard implements KeyboardState {
    private final Set<Integer> pressedKeys = new HashSet<>();

    void press(int... keys) {
        pressedKeys.clear();
        for (int key : keys) {
            pressedKeys.add(key);
        }
    }

    @Override
    public boolean isKeyPressed(int key) {
        return pressedKeys.contains(key);
    }
}
