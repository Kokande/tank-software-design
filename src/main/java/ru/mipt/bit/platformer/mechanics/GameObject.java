package ru.mipt.bit.platformer.mechanics;

public interface GameObject extends Obstacle {
    default void live(float tick) { }

    default void addedTo(GameWorld world) { }

    default void removedFrom(GameWorld world) { }
}
