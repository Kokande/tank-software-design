package ru.mipt.bit.platformer.util;

import com.badlogic.gdx.math.GridPoint2;
import ru.mipt.bit.platformer.objects.CollisionChecker;
import ru.mipt.bit.platformer.objects.Obstacle;

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
