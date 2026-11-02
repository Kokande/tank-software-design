package ru.mipt.bit.platformer.mechanics;

public interface WorldListener {
    void objectAdded(GameObject object);

    void objectRemoved(GameObject object);
}
