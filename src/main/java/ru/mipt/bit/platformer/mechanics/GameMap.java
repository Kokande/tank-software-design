package ru.mipt.bit.platformer.mechanics;

import com.badlogic.gdx.math.GridPoint2;

public class GameMap implements CollisionChecker {
    private final Obstacle[] obstacles;

    public GameMap(Obstacle... obstacles) {
        this.obstacles = obstacles;
    }

    @Override
    public boolean collides(GridPoint2 point) {
        for (Obstacle obstacle : obstacles) {
            if (obstacle.occupies(point)) {
                return true;
            }
        }
        return false;
    }
}
