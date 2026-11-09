package ru.mipt.bit.platformer.mechanics;

import ru.mipt.bit.platformer.mechanics.collision.Obstacle;

public interface GameObject extends Obstacle {
    default void live(float tick) { }

    default void addedTo(GameWorld world) { }

    default void removedFrom(GameWorld world) { }
}
