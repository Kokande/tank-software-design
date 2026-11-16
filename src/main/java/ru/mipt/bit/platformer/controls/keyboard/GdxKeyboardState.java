package ru.mipt.bit.platformer.controls.keyboard;

import com.badlogic.gdx.Gdx;

/**
 * Infrastructure: driven adapter implementing KeyboardState using input
 */
public class GdxKeyboardState implements KeyboardState {

    @Override
    public boolean isKeyPressed(int key) {
        return Gdx.input.isKeyPressed(key);
    }
}
