package ru.mipt.bit.platformer.objects;

import com.badlogic.gdx.math.GridPoint2;

public class Tree {
    private final GridPoint2 treeObstacleCoordinates;

    public Tree(int x, int y) {
        this.treeObstacleCoordinates = new GridPoint2(x, y);
    }

    public GridPoint2 getCoordinates() {
        return treeObstacleCoordinates;
    }
}
