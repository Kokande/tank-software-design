package ru.mipt.bit.platformer.controls.ai;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import ru.mipt.bit.platformer.controls.commands.Command;
import ru.mipt.bit.platformer.controls.commands.MoveCommand;
import ru.mipt.bit.platformer.mechanics.collision.GameMap;
import ru.mipt.bit.platformer.mechanics.collision.LevelBounds;
import ru.mipt.bit.platformer.mechanics.movement.Direction;
import ru.mipt.bit.platformer.objects.Tank;

import java.util.EnumSet;
import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RandomControlStrategyTest {
    private static final float MOVEMENT_DURATION = 0.4f;
    private static final GridPoint2 START = new GridPoint2(2, 2);

    // random which always picks the same index
    private static class FixedRandom extends Random {
        private final int value;

        FixedRandom(int value) {
            this.value = value;
        }

        @Override
        public int nextInt(int bound) {
            return value;
        }
    }

    private Tank tank;
    private GameMap map;

    @BeforeEach
    void setUp() {
        tank = new Tank(new GridPoint2(START), MOVEMENT_DURATION);
        map = new GameMap();
    }

    @ParameterizedTest
    @EnumSource(Direction.class)
    void movesInTheRandomlyChosenDirection(Direction direction) {
        RandomControlStrategy strategy = new RandomControlStrategy(tank, map, new FixedRandom(direction.ordinal()));

        MoveCommand command = (MoveCommand) strategy.nextCommand().orElseThrow();
        command.execute();
        tank.update(MOVEMENT_DURATION);

        assertEquals(direction, command.getDirection());
        assertEquals(new GridPoint2(START.x + direction.dx(), START.y + direction.dy()), tank.getCoordinates());
    }

    @Test
    void doesNotChangeItsMindWhileTheTankIsMoving() {
        RandomControlStrategy strategy = new RandomControlStrategy(tank, map, new FixedRandom(Direction.UP.ordinal()));
        strategy.nextCommand().orElseThrow().execute();
        tank.update(MOVEMENT_DURATION / 2f);

        assertTrue(tank.isMoving());
        assertFalse(strategy.nextCommand().isPresent());
    }

    @Test
    void picksANewDirectionOnceTheTankArrives() {
        RandomControlStrategy strategy = new RandomControlStrategy(tank, map, new FixedRandom(Direction.UP.ordinal()));
        strategy.nextCommand().orElseThrow().execute();
        tank.update(MOVEMENT_DURATION);

        assertTrue(strategy.nextCommand().isPresent());
    }

    @Test
    void soonerOrLaterChoosesEveryDirection() {
        RandomControlStrategy strategy = new RandomControlStrategy(tank, map, new Random(42));
        Set<Direction> chosen = EnumSet.noneOf(Direction.class);

        for (int i = 0; i < 100; i++) {
            Command command = strategy.nextCommand().orElseThrow();
            if (command instanceof MoveCommand) chosen.add(((MoveCommand) command).getDirection());
        }

        assertEquals(EnumSet.allOf(Direction.class), chosen);
    }

    @Test
    void neverLeavesTheLevel() {
        GameMap boundedMap = new GameMap(new LevelBounds(3, 3));
        tank = new Tank(new GridPoint2(1, 1), MOVEMENT_DURATION);
        RandomControlStrategy strategy = new RandomControlStrategy(tank, boundedMap, new Random(7));

        for (int i = 0; i < 500; i++) {
            strategy.nextCommand().ifPresent(Command::execute);
            tank.update(MOVEMENT_DURATION);

            GridPoint2 position = tank.getCoordinates();
            assertTrue(position.x >= 0 && position.x < 3 && position.y >= 0 && position.y < 3, "tank left the level at " + position);
        }
    }
}
