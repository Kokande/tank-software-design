package ru.mipt.bit.platformer.controls;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.GridPoint2;
import org.junit.jupiter.api.Test;
import ru.mipt.bit.platformer.controls.ai.RandomControlStrategy;
import ru.mipt.bit.platformer.controls.commands.MoveCommand;
import ru.mipt.bit.platformer.controls.keyboard.KeyBindings;
import ru.mipt.bit.platformer.controls.keyboard.KeyboardControlStrategy;
import ru.mipt.bit.platformer.controls.support.FakeKeyboard;
import ru.mipt.bit.platformer.mechanics.collision.GameMap;
import ru.mipt.bit.platformer.mechanics.collision.LevelBounds;
import ru.mipt.bit.platformer.mechanics.collision.Obstacle;
import ru.mipt.bit.platformer.mechanics.movement.Direction;
import ru.mipt.bit.platformer.objects.Tank;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ControlsProcessorTest {
    private static final float MOVEMENT_DURATION = 0.4f;

    // strategy which always asks to move its tank in the same direction
    private static ControlStrategy always(Tank tank, Direction direction, GameMap map) {
        return () -> Optional.of(new MoveCommand(tank, direction, map));
    }

    @Test
    void executesTheCommandOfEveryStrategyInOrder() {
        List<String> executed = new ArrayList<>();
        ControlsProcessor processor = new ControlsProcessor(List.of(
                () -> Optional.of(() -> executed.add("first")),
                () -> Optional.of(() -> executed.add("second"))
        ));

        processor.processInput();

        assertEquals(List.of("first", "second"), executed);
    }

    @Test
    void skipsStrategiesWithNothingToDo() {
        List<String> executed = new ArrayList<>();
        ControlsProcessor processor = new ControlsProcessor(List.of(
                Optional::empty,
                () -> Optional.of(() -> executed.add("second"))
        ));

        processor.processInput();

        assertEquals(List.of("second"), executed);
    }

    @Test
    void worksWithoutAnyStrategies() {
        new ControlsProcessor(Collections.emptyList()).processInput();
    }

    @Test
    void doesNotDependOnLaterChangesOfTheStrategyList() {
        List<String> executed = new ArrayList<>();
        List<ControlStrategy> strategies = new ArrayList<>();
        ControlsProcessor processor = new ControlsProcessor(strategies);

        strategies.add(() -> Optional.of(() -> executed.add("late")));
        processor.processInput();

        assertTrue(executed.isEmpty());
    }

    @Test
    void twoTanksCanNotDriveIntoTheSameTile() {
        // both tanks aim at (1,0)
        Tank left = new Tank(new GridPoint2(0, 0), MOVEMENT_DURATION);
        Tank right = new Tank(new GridPoint2(2, 0), MOVEMENT_DURATION);
        GameMap map = new GameMap(left, right);
        ControlsProcessor processor = new ControlsProcessor(List.of(
                always(left, Direction.RIGHT, map),
                always(right, Direction.LEFT, map)
        ));

        processor.processInput();

        assertTrue(left.isMoving());
        assertFalse(right.isMoving(), "the tile is already taken by the first tank");
        assertEquals(new GridPoint2(1, 0), left.getDestinationCoordinates());
        assertEquals(new GridPoint2(2, 0), right.getDestinationCoordinates());
    }

    @Test
    void tankCanNotDriveIntoTheTileAnotherTankIsLeaving() {
        Tank leader = new Tank(new GridPoint2(1, 0), MOVEMENT_DURATION);
        Tank follower = new Tank(new GridPoint2(0, 0), MOVEMENT_DURATION);
        GameMap map = new GameMap(leader, follower);
        ControlsProcessor processor = new ControlsProcessor(List.of(
                always(leader, Direction.RIGHT, map),
                always(follower, Direction.RIGHT, map)
        ));

        processor.processInput();
        leader.update(MOVEMENT_DURATION / 2f);
        processor.processInput();

        assertTrue(leader.isMoving());
        assertFalse(follower.isMoving(), "(1,0) is still taken by the leaving tank");

        leader.update(MOVEMENT_DURATION / 2f);
        processor.processInput();

        assertTrue(follower.isMoving(), "(1,0) is free once the leader has arrived");
    }

    @Test
    void playerAndAiTanksAreProcessedTogether() {
        FakeKeyboard keyboard = new FakeKeyboard();
        Tank player = new Tank(new GridPoint2(0, 0), MOVEMENT_DURATION);
        Tank enemy = new Tank(new GridPoint2(4, 4), MOVEMENT_DURATION);
        GameMap map = new GameMap(new LevelBounds(5, 5), player, enemy);
        ControlsProcessor processor = new ControlsProcessor(List.of(
                new KeyboardControlStrategy(player, map, keyboard, KeyBindings.wasdAndArrows()),
                // the enemy is in the top right corner, so only DOWN and LEFT let it drive
                new RandomControlStrategy(enemy, map, new Random(1))
        ));
        keyboard.press(Input.Keys.W);

        for (int frame = 0; frame < 20 && !enemy.isMoving(); frame++) {
            processor.processInput();
        }

        assertEquals(new GridPoint2(0, 1), player.getDestinationCoordinates());
        assertTrue(enemy.isMoving(), "AI tank must start driving on its own");
    }

    @Test
    void manyRandomTanksNeverShareATileNorLeaveTheLevel() {
        int size = 4;
        List<Tank> tanks = new ArrayList<>();
        List<Obstacle> obstacles = new ArrayList<>();
        obstacles.add(new LevelBounds(size, size));
        for (int i = 0; i < size; i++) {
            Tank tank = new Tank(new GridPoint2(i, i), MOVEMENT_DURATION);
            tanks.add(tank);
            obstacles.add(tank);
        }
        GameMap map = new GameMap(obstacles.toArray(new Obstacle[0]));
        Random random = new Random(3);
        List<ControlStrategy> strategies = new ArrayList<>();
        for (Tank tank : tanks) {
            strategies.add(new RandomControlStrategy(tank, map, random));
        }
        ControlsProcessor processor = new ControlsProcessor(strategies);

        for (int frame = 0; frame < 1000; frame++) {
            processor.processInput();
            for (Tank tank : tanks) {
                // small steps, so tanks are often caught in the middle of a move
                tank.update(MOVEMENT_DURATION / 3f);
            }
            assertTanksAreValid(tanks, size);
        }
    }

    private static void assertTanksAreValid(List<Tank> tanks, int size) {
        for (int i = 0; i < tanks.size(); i++) {
            Tank tank = tanks.get(i);
            GridPoint2 destination = tank.getDestinationCoordinates();
            assertTrue(destination.x >= 0 && destination.x < size && destination.y >= 0 && destination.y < size,
                    "tank " + i + " drives out of the level to " + destination);
            for (int j = i + 1; j < tanks.size(); j++) {
                Tank other = tanks.get(j);
                assertFalse(other.occupies(tank.getCoordinates()) || other.occupies(destination),
                        "tanks " + i + " and " + j + " share a tile");
            }
        }
    }
}
