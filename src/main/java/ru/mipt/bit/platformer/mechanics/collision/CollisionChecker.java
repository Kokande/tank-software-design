package ru.mipt.bit.platformer.mechanics.collision;

import com.badlogic.gdx.math.GridPoint2;

public interface CollisionChecker {
    boolean collides(GridPoint2 point);
}
