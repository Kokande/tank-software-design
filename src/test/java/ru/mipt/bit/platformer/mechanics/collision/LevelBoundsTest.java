package ru.mipt.bit.platformer.mechanics.collision;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import ru.mipt.bit.platformer.mechanics.movement.Direction;
import ru.mipt.bit.platformer.objects.Tank;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LevelBoundsTest {
    private final LevelBounds bounds = new LevelBounds(10, 8);

    @ParameterizedTest
    @CsvSource({"0, 0", "9, 0", "0, 7", "9, 7", "5, 4"})
    void tilesInsideTheLevelAreFree(int x, int y) {
        assertFalse(bounds.occupies(new GridPoint2(x, y)));
    }

    @ParameterizedTest
    @CsvSource({"-1, 0", "0, -1", "10, 0", "0, 8", "10, 8", "-1, -1", "100, 3"})
    void tilesOutsideTheLevelAreBlocked(int x, int y) {
        assertTrue(bounds.occupies(new GridPoint2(x, y)));
    }

    @ParameterizedTest
    @EnumSource(Direction.class)
    void tankInACornerOfASingleTileLevelCanNotMoveAnywhere(Direction direction) {
        GameMap map = new GameMap(new LevelBounds(1, 1));
        Tank tank = new Tank(new GridPoint2(0, 0), 1f);

        tank.tryMovement(direction.dx(), direction.dy(), direction.rotation(), map);

        assertFalse(tank.isMoving());
        assertEquals(new GridPoint2(0, 0), tank.getDestinationCoordinates());
        assertEquals(direction.rotation(), tank.getRotation(), "tank still turns towards the border");
    }

    @Test
    void tankStopsAtTheBorderLikeAtAnObstacle() {
        GameMap map = new GameMap(new LevelBounds(3, 1));
        Tank tank = new Tank(new GridPoint2(0, 0), 1f);

        for (int i = 0; i < 5; i++) {
            tank.tryMovement(Direction.RIGHT.dx(), Direction.RIGHT.dy(), Direction.RIGHT.rotation(), map);
            tank.update(1f);
        }

        assertEquals(new GridPoint2(2, 0), tank.getCoordinates());
    }
}
