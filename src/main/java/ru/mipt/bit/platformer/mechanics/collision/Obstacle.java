package ru.mipt.bit.platformer.mechanics.collision;

import com.badlogic.gdx.math.GridPoint2;

public interface Obstacle {
    boolean occupies(GridPoint2 point);
}
