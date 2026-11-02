package ru.mipt.bit.platformer.util;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.jupiter.api.Test;
import ru.mipt.bit.platformer.mechanics.GameMap;
import ru.mipt.bit.platformer.mechanics.Obstacle;
import ru.mipt.bit.platformer.objects.Tank;
import ru.mipt.bit.platformer.objects.Tree;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameMapTest {
    @Test
    void copiesTheArrayButKeepsLiveObstaclePositions() {
        Tank tank = new Tank(new GridPoint2(1, 1), 0.4f);
        Obstacle[] obstacles = {tank};
        GameMap map = new GameMap(obstacles);
        obstacles[0] = new Tree(5, 5);

        assertTrue(map.collides(new GridPoint2(1, 1)));
        assertFalse(map.collides(new GridPoint2(5, 5)));
        tank.tryMovement(1, 0, 0, point -> false);
        tank.update(0.4f);
        assertTrue(map.collides(new GridPoint2(2, 1)));
        assertFalse(map.collides(new GridPoint2(1, 1)));
    }


    @Test
    void reportsCollisionOnATileOccupiedByATree() {
        GameMap map = new GameMap(new Tree(1, 3));

        assertTrue(map.collides(new GridPoint2(1, 3)));
    }

    @Test
    void reportsNoCollisionOnAFreeTile() {
        GameMap map = new GameMap(new Tree(1, 3));

        assertFalse(map.collides(new GridPoint2(1, 2)));
        assertFalse(map.collides(new GridPoint2(3, 1)));
    }

    @Test
    void findsCollisionWithAnyOfTheTrees() {
        GameMap map = new GameMap(new Tree(0, 0), new Tree(4, 7), new Tree(2, 2));

        assertTrue(map.collides(new GridPoint2(0, 0)));
        assertTrue(map.collides(new GridPoint2(4, 7)));
        assertTrue(map.collides(new GridPoint2(2, 2)));
        assertFalse(map.collides(new GridPoint2(7, 4)));
    }

    @Test
    void collidesWithAnyKindOfObstacle() {
        // obstacle covering the whole bottom row
        GameMap map = new GameMap(point -> point.y == 0, new Tree(2, 2));

        assertTrue(map.collides(new GridPoint2(5, 0)));
        assertTrue(map.collides(new GridPoint2(2, 2)));
        assertFalse(map.collides(new GridPoint2(5, 1)));
    }

    @Test
    void anEmptyMapCollidesWithNothing() {
        GameMap map = new GameMap();

        assertFalse(map.collides(new GridPoint2(0, 0)));
    }
}
