package ru.mipt.bit.platformer.graphics;

import com.badlogic.gdx.graphics.g2d.Batch;

public interface Renderable {
    void render(Batch batch);

    void dispose();
}
