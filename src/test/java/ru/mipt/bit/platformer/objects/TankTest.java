package ru.mipt.bit.platformer.objects;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import ru.mipt.bit.platformer.util.Direction;
import ru.mipt.bit.platformer.util.GameMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TankTest {
    private static final float MOVEMENT_DURATION = 0.4f;
    private static final GridPoint2 START = new GridPoint2(1, 1);

    private GameMap emptyMap;
    private Tank tank;

    @BeforeEach
    void setUp() {
        emptyMap = new GameMap();
        tank = new Tank(new GridPoint2(START));
    }

    private void move(Direction direction, GameMap map) {
        tank.tryMovement(direction.dx(), direction.dy(), direction.rotation(), map);
    }

    private void finishMovement() {
        tank.update(MOVEMENT_DURATION);
    }

    @Test
    void startsIdleAtItsInitialTile() {
        assertEquals(START, tank.getCoordinates());
        assertEquals(START, tank.getDestinationCoordinates());
        assertEquals(1f, tank.getMovementProgress());
        assertFalse(tank.isMoving());
    }

    @Test
    void doesNotShareStateWithTheCoordinatesItWasCreatedWith() {
        GridPoint2 initial = new GridPoint2(2, 2);
        Tank tank = new Tank(initial);

        initial.set(7, 7);

        assertEquals(new GridPoint2(2, 2), tank.getCoordinates());
    }

    @Test
    void picksTheNeighbouringTileAsDestinationButStaysUntilItArrives() {
        move(Direction.UP, emptyMap);

        assertEquals(new GridPoint2(1, 2), tank.getDestinationCoordinates());
        assertEquals(START, tank.getCoordinates(), "tank must not teleport to the destination");
        assertEquals(0f, tank.getMovementProgress());
        assertTrue(tank.isMoving());
    }

    @Test
    void arrivesAtTheDestinationWhenTheMovementIsOver() {
        move(Direction.UP, emptyMap);

        finishMovement();

        assertEquals(new GridPoint2(1, 2), tank.getCoordinates());
        assertEquals(1f, tank.getMovementProgress());
        assertFalse(tank.isMoving());
    }

    @Test
    void movesGraduallyOverSeveralFrames() {
        move(Direction.UP, emptyMap);

        tank.update(MOVEMENT_DURATION / 4f);
        assertEquals(0.25f, tank.getMovementProgress(), 1e-5f);
        assertTrue(tank.isMoving());

        tank.update(MOVEMENT_DURATION / 4f);
        assertEquals(0.5f, tank.getMovementProgress(), 1e-5f);
        assertEquals(START, tank.getCoordinates());
    }

    @Test
    void movementProgressNeverExceedsOne() {
        move(Direction.UP, emptyMap);

        tank.update(100 * MOVEMENT_DURATION);

        assertEquals(1f, tank.getMovementProgress());
    }

    @Test
    void ignoresNewDestinationWhileStillMoving() {
        move(Direction.UP, emptyMap);

        tank.update(MOVEMENT_DURATION / 2f);
        move(Direction.RIGHT, emptyMap);

        assertEquals(new GridPoint2(1, 2), tank.getDestinationCoordinates(), "destination must not change mid-move");
    }

    @Test
    void rotatesTowardsTheRequestedDirectionEvenWhileMoving() {
        move(Direction.UP, emptyMap);
        tank.update(MOVEMENT_DURATION / 2f);

        move(Direction.RIGHT, emptyMap);

        assertEquals(Direction.RIGHT.rotation(), tank.getRotation());
    }

    @Test
    void doesNotMoveIntoATreeButStillTurnsToIt() {
        GameMap map = new GameMap(new Tree(1, 2));

        move(Direction.UP, map);

        assertEquals(START, tank.getCoordinates());
        assertEquals(START, tank.getDestinationCoordinates());
        assertFalse(tank.isMoving());
        assertEquals(Direction.UP.rotation(), tank.getRotation());
    }

    @Test
    void movesAroundAnObstacle() {
        GameMap map = new GameMap(new Tree(1, 2));

        move(Direction.RIGHT, map);
        finishMovement();
        move(Direction.UP, map);
        finishMovement();

        assertEquals(new GridPoint2(2, 2), tank.getCoordinates());
    }

    @ParameterizedTest
    @EnumSource(Direction.class)
    void movesOneTileInEveryDirection(Direction direction) {
        move(direction, emptyMap);
        finishMovement();

        assertEquals(new GridPoint2(START.x + direction.dx(), START.y + direction.dy()), tank.getCoordinates());
        assertEquals(direction.rotation(), tank.getRotation());
    }

    @Test
    void returnsToTheStartAfterAFullLoop() {
        Direction[] loop = {Direction.UP, Direction.RIGHT, Direction.DOWN, Direction.LEFT};
        for (Direction direction : loop) {
            move(direction, emptyMap);
            finishMovement();
        }

        assertEquals(START, tank.getCoordinates());
    }
}
