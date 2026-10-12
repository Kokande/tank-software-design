package ru.mipt.bit.platformer.mechanics;

public interface Movable {
    void tryMovement(int dx, int dy, float newRotation, CollisionChecker obstacles);
}
