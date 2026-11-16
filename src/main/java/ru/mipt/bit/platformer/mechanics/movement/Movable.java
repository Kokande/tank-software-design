package ru.mipt.bit.platformer.mechanics.movement;

import ru.mipt.bit.platformer.mechanics.collision.CollisionChecker;

/**
 * Domain incoming capability port: accepts movement requests from application commands
 */
public interface Movable {
    void tryMovement(int dx, int dy, float newRotation, CollisionChecker obstacles);

    boolean isMoving();
}
