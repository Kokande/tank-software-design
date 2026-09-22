package ru.mipt.bit.platformer.util;

public enum Direction {
    UP(0, 1, 90f), DOWN(0, -1, -90f),
    RIGHT(1, 0, 0f), LEFT(-1, 0, -180f);

    final int x;
    final int y;
    final float r;

    Direction(int delta_x, int delta_y, float direction) {
        x = delta_x;
        y = delta_y;
        r = direction;
    }

    public int dx() {
        return x;
    }

    public int dy() {
        return y;
    }

    public float rotation() {
        return r;
    }
}
