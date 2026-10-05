package ru.mipt.bit.platformer.objects;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TreeTest {

    @Test
    void keepsTheCoordinatesItWasCreatedWith() {
        Tree tree = new Tree(3, 5);

        assertEquals(new GridPoint2(3, 5), tree.getCoordinates());
    }

    @Test
    void occupiesOnlyItsOwnTile() {
        Tree tree = new Tree(3, 5);

        assertTrue(tree.occupies(new GridPoint2(3, 5)));
        assertFalse(tree.occupies(new GridPoint2(5, 3)));
    }

    @Test
    void treesOnDifferentTilesHaveDifferentCoordinates() {
        Tree tree = new Tree(1, 3);
        Tree anotherTree = new Tree(3, 1);

        assertNotEquals(tree.getCoordinates(), anotherTree.getCoordinates());
    }
}
