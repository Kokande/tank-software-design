package ru.mipt.bit.platformer.controls.keyboard;

/**
 * Input-adapter port: abstracts keyboard polling from and test keyboards
 */
public interface KeyboardState {
    boolean isKeyPressed(int key);
}
