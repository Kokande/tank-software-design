package ru.mipt.bit.platformer.controls.support;

import ru.mipt.bit.platformer.controls.keyboard.KeyboardState;

import java.util.HashSet;
import java.util.Set;

// keyboard which reports exactly the keys a test has pressed
public class FakeKeyboard implements KeyboardState {
    private final Set<Integer> pressedKeys = new HashSet<>();

    public void press(int... keys) {
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
