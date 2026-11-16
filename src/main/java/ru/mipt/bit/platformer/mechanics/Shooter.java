package ru.mipt.bit.platformer.mechanics;

/**
 * Domain incoming capability port: accepts shooting requests from application commands
 */
public interface Shooter {
    void shoot();
}
