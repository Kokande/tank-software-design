package ru.mipt.bit.platformer.level;

/**
 * Application outgoing port: obtain level content independently of its source
 */
public interface LevelGenerator {
    Level generate();
}
