package ru.mipt.bit.platformer.controls;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.GridPoint2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import ru.mipt.bit.platformer.mechanics.Direction;
import ru.mipt.bit.platformer.mechanics.GameMap;
import ru.mipt.bit.platformer.objects.Tank;
import ru.mipt.bit.platformer.objects.Tree;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class KeyboardControlStrategyTest {
    @Test
    void callerCannotChangeBindingsOrTheirPriorityAfterConstruction() {
        Map<Integer, Direction> bindings = new LinkedHashMap<>();
        bindings.put(Input.Keys.D, Direction.RIGHT);
        bindings.put(Input.Keys.W, Direction.UP);
        FakeKeyboard keyboard = new FakeKeyboard();
        Tank tank = new Tank(new GridPoint2(1, 1), 0.4f);
        ControlStrategy strategy = new KeyboardControlStrategy(tank, new GameMap(), keyboard, bindings);
        bindings.clear();
        bindings.put(Input.Keys.D, Direction.LEFT);
        keyboard.press(Input.Keys.D, Input.Keys.W);

        strategy.nextCommand().orElseThrow().execute();
        tank.update(0.4f);

        assertEquals(new GridPoint2(2, 1), tank.getCoordinates());
    }

    private static final float MOVEMENT_DURATION = 0.4f;
    private static final GridPoint2 START = new GridPoint2(1, 1);

    private FakeKeyboard keyboard;
    private Tank tank;
    private GameMap map;
    private KeyboardControlStrategy strategy;

    @BeforeEach
    void setUp() {
        keyboard = new FakeKeyboard();
        tank = new Tank(new GridPoint2(START), MOVEMENT_DURATION);
        map = new GameMap(new Tree(1, 3));
        strategy = new KeyboardControlStrategy(tank, map, keyboard, KeyBindings.wasdAndArrows());
    }

    private void step() {
        strategy.nextCommand().ifPresent(Command::execute);
        tank.update(MOVEMENT_DURATION);
    }

    @ParameterizedTest
    @CsvSource({
            "W, UP", "Up, UP",
            "S, DOWN", "Down, DOWN",
            "A, LEFT", "Left, LEFT",
            "D, RIGHT", "Right, RIGHT"
    })
    void eachKeyOfAPairMovesTheTankInItsDirection(String keyName, Direction expected) {
        keyboard.press(Input.Keys.valueOf(keyName));

        step();

        assertEquals(expected.rotation(), tank.getRotation());
        assertEquals(new GridPoint2(START.x + expected.dx(), START.y + expected.dy()), tank.getCoordinates());
    }

    @Test
    void producesAMoveCommandForThePressedKey() {
        keyboard.press(Input.Keys.D);

        Optional<Command> command = strategy.nextCommand();

        assertTrue(command.isPresent());
        assertEquals(Direction.RIGHT, ((MoveCommand) command.get()).getDirection());
    }

    @Test
    void producesNoCommandWhileNothingIsPressed() {
        assertFalse(strategy.nextCommand().isPresent());
    }

    @Test
    void tankStaysStillWhileNothingIsPressed() {
        step();

        assertEquals(START, tank.getCoordinates());
        assertFalse(tank.isMoving());
    }

    @Test
    void commandIsNotExecutedByTheStrategyItself() {
        keyboard.press(Input.Keys.W);

        strategy.nextCommand();
        tank.update(MOVEMENT_DURATION);

        assertEquals(START, tank.getCoordinates());
        assertFalse(tank.isMoving());
    }

    @Test
    void unrelatedKeysAreIgnored() {
        keyboard.press(Input.Keys.SPACE, Input.Keys.ESCAPE);

        assertFalse(strategy.nextCommand().isPresent());
    }

    @Test
    void theFirstBoundKeyWinsWhenSeveralArePressed() {
        keyboard.press(Input.Keys.D, Input.Keys.W);

        MoveCommand command = (MoveCommand) strategy.nextCommand().orElseThrow();

        assertEquals(Direction.UP, command.getDirection());
    }

    @Test
    void everyMovementIsBoundToTwoEquivalentKeys() {
        Map<Integer, Direction> bindings = KeyBindings.wasdAndArrows();
        for (Direction direction : Direction.values()) {
            assertEquals(2, Collections.frequency(bindings.values(), direction), direction + " must be bound to a pair of keys");
        }
    }

    @Test
    void keyBindingsAreConfigurable() {
        strategy = new KeyboardControlStrategy(tank, map, keyboard, Map.of(Input.Keys.SPACE, Direction.RIGHT));
        keyboard.press(Input.Keys.SPACE);

        step();

        assertEquals(new GridPoint2(START.x + 1, START.y), tank.getCoordinates());
    }

    @Test
    void obstacleStopsTheTankControlledByKeyboard() {
        keyboard.press(Input.Keys.W);

        // (1,1) -> (1,2), then the tree at (1,3) blocks the next step
        step();
        step();

        assertEquals(new GridPoint2(1, 2), tank.getCoordinates());
    }

    @Test
    void keyHeldDownKeepsTheTankMoving() {
        keyboard.press(Input.Keys.RIGHT);

        for (int i = 0; i < 3; i++) {
            step();
        }

        assertEquals(new GridPoint2(START.x + 3, START.y), tank.getCoordinates());
    }
}
