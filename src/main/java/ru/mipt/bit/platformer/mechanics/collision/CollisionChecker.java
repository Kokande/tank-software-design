package ru.mipt.bit.platformer.mechanics.collision;

import com.badlogic.gdx.math.GridPoint2;

/**
 * Domain query port: asks whether the movement environment blocks a tile
 */
public interface CollisionChecker {
    boolean collides(GridPoint2 point);
}
