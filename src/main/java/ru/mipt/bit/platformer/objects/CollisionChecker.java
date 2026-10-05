package ru.mipt.bit.platformer.objects;

import com.badlogic.gdx.math.GridPoint2;

public interface CollisionChecker {
    boolean collides(GridPoint2 point);
}
