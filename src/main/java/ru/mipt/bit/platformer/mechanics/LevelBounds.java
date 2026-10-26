package ru.mipt.bit.platformer.mechanics;

import com.badlogic.gdx.math.GridPoint2;

public class LevelBounds implements Obstacle {
    private final int width;
    private final int height;

    public LevelBounds(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public boolean occupies(GridPoint2 point) {
        return point.x < 0 || point.y < 0 || point.x >= width || point.y >= height;
    }
}
