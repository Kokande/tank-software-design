package ru.mipt.bit.platformer.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.mipt.bit.platformer.util.Direction.DOWN;
import static ru.mipt.bit.platformer.util.Direction.LEFT;
import static ru.mipt.bit.platformer.util.Direction.RIGHT;
import static ru.mipt.bit.platformer.util.Direction.UP;

class DirectionTest {

    @Test
    void shiftsOneTileAlongTheExpectedAxis() {
        assertEquals(0, UP.dx());
        assertEquals(1, UP.dy());

        assertEquals(0, DOWN.dx());
        assertEquals(-1, DOWN.dy());

        assertEquals(1, RIGHT.dx());
        assertEquals(0, RIGHT.dy());

        assertEquals(-1, LEFT.dx());
        assertEquals(0, LEFT.dy());
    }

    @Test
    void oppositeDirectionsCancelEachOther() {
        assertEquals(0, UP.dx() + DOWN.dx());
        assertEquals(0, UP.dy() + DOWN.dy());
        assertEquals(0, LEFT.dx() + RIGHT.dx());
        assertEquals(0, LEFT.dy() + RIGHT.dy());
    }

    @Test
    void rotationMatchesTheDirectionOfMovement() {
        assertEquals(0f, RIGHT.rotation());
        assertEquals(90f, UP.rotation());
        assertEquals(-90f, DOWN.rotation());
        assertEquals(-180f, LEFT.rotation());
    }

    @ParameterizedTest
    @EnumSource(Direction.class)
    void everyDirectionMovesExactlyOneTile(Direction direction) {
        assertEquals(1, Math.abs(direction.dx()) + Math.abs(direction.dy()),
                direction + " must move the tank by a single tile");
    }

    @ParameterizedTest
    @EnumSource(Direction.class)
    void rotationStaysWithinFullCircle(Direction direction) {
        assertTrue(Math.abs(direction.rotation()) <= 180f, direction + " has an out of range rotation");
    }
}
