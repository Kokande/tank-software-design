package ru.mipt.bit.platformer.mechanics;

/**
 * Domain outgoing event port: reports entity additions and removals
 */
public interface WorldListener {
    void objectAdded(GameObject object);

    void objectRemoved(GameObject object);
}
