package ru.mipt.bit.platformer.controls.commands;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import ru.mipt.bit.platformer.mechanics.collision.CollisionChecker;
import ru.mipt.bit.platformer.mechanics.collision.GameMap;
import ru.mipt.bit.platformer.mechanics.movement.Direction;
import ru.mipt.bit.platformer.mechanics.movement.Movable;
import ru.mipt.bit.platformer.objects.Tank;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;

class MoveCommandTest {

    // remembers the last movement it was asked for
    private static class RecordingMovable implements Movable {
        int dx;
        int dy;
        float rotation;
        CollisionChecker obstacles;
        int calls;

        @Override
        public void tryMovement(int dx, int dy, float newRotation, CollisionChecker obstacles) {
            this.dx = dx;
            this.dy = dy;
            this.rotation = newRotation;
            this.obstacles = obstacles;
            calls++;
        }

        @Override
        public boolean isMoving() {
            return false;
        }
    }

    @ParameterizedTest
    @EnumSource(Direction.class)
    void passesTheDirectionToTheMovable(Direction direction) {
        RecordingMovable movable = new RecordingMovable();
        CollisionChecker map = new GameMap();

        new MoveCommand(movable, direction, map).execute();

        assertEquals(1, movable.calls);
        assertEquals(direction.dx(), movable.dx);
        assertEquals(direction.dy(), movable.dy);
        assertEquals(direction.rotation(), movable.rotation);
        assertSame(map, movable.obstacles);
    }

    @Test
    void doesNothingUntilExecuted() {
        RecordingMovable movable = new RecordingMovable();

        MoveCommand command = new MoveCommand(movable, Direction.UP, new GameMap());

        assertEquals(0, movable.calls);
        assertEquals(Direction.UP, command.getDirection());
    }

    @Test
    void canBeExecutedSeveralTimes() {
        Tank tank = new Tank(new GridPoint2(0, 0), 1f);
        MoveCommand command = new MoveCommand(tank, Direction.RIGHT, new GameMap());

        command.execute();
        tank.update(1f);
        command.execute();
        tank.update(1f);

        assertEquals(new GridPoint2(2, 0), tank.getCoordinates());
    }

    @Test
    void movesTheTankOnlyWhereTheMapAllows() {
        Tank tank = new Tank(new GridPoint2(0, 0), 1f);

        new MoveCommand(tank, Direction.UP, point -> true).execute();

        assertFalse(tank.isMoving());
        assertEquals(Direction.UP.rotation(), tank.getRotation());
    }
}
