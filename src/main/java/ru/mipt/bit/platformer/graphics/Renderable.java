package ru.mipt.bit.platformer.graphics;

import com.badlogic.gdx.graphics.g2d.Batch;

/**
 * Infrastructure: internal rendering interface implemented by graphics adapter components
 */
public interface Renderable {
    void render(Batch batch);

    void dispose();
}
