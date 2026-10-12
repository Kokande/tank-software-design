package ru.mipt.bit.platformer.objects;

import com.badlogic.gdx.math.GridPoint2;
import ru.mipt.bit.platformer.mechanics.Obstacle;

public class Tree implements Obstacle {
    private final GridPoint2 treeObstacleCoordinates;

    public Tree(int x, int y) {
        this.treeObstacleCoordinates = new GridPoint2(x, y);
    }

    public GridPoint2 getCoordinates() {
        return treeObstacleCoordinates;
    }

    @Override
    public boolean occupies(GridPoint2 point) {
        return treeObstacleCoordinates.equals(point);
    }
}
