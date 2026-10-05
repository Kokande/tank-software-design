package ru.mipt.bit.platformer.objects;

public interface Movable {
    void tryMovement(int dx, int dy, float newRotation, CollisionChecker obstacles);
}
