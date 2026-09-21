package ru.mipt.bit.platformer.util;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.jupiter.api.Test;
import ru.mipt.bit.platformer.objects.Tree;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameMapTest {

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
    void keepsEveryTreeItWasBuiltFrom() {
        Tree[] trees = {new Tree(1, 3), new Tree(2, 2)};

        GameMap map = new GameMap(trees);

        assertEquals(2, map.getTrees().length);
        assertEquals(new GridPoint2(1, 3), map.getTrees()[0].getCoordinates());
        assertEquals(new GridPoint2(2, 2), map.getTrees()[1].getCoordinates());
    }

    @Test
    void anEmptyMapCollidesWithNothing() {
        GameMap map = new GameMap();

        assertFalse(map.collides(new GridPoint2(0, 0)));
        assertEquals(0, map.getTrees().length);
    }
}
