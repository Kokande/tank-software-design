package ru.mipt.bit.platformer.controls.keyboard;

import com.badlogic.gdx.Gdx;

public class GdxKeyboardState implements KeyboardState {

    @Override
    public boolean isKeyPressed(int key) {
        return Gdx.input.isKeyPressed(key);
    }
}
