package ru.mipt.bit.platformer.util;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.GridPoint2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import ru.mipt.bit.platformer.objects.Tank;
import ru.mipt.bit.platformer.objects.Tree;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class ControlsProcessorTest {
    private static final float MOVEMENT_DURATION = 0.4f;
    private static final GridPoint2 START = new GridPoint2(1, 1);

    // keyboard which reports exactly the keys a test has pressed
    private static class FakeKeyboard implements KeyboardState {
        private final Set<Integer> pressedKeys = new HashSet<>();

        void press(int... keys) {
            pressedKeys.clear();
            for (int key : keys) {
                pressedKeys.add(key);
            }
        }

        @Override
        public boolean isKeyPressed(int key) {
            return pressedKeys.contains(key);
        }
    }

    private FakeKeyboard keyboard;
    private Tank tank;
    private GameMap map;
    private ControlsProcessor controlsProcessor;

    @BeforeEach
    void setUp() {
        keyboard = new FakeKeyboard();
        tank = new Tank(new GridPoint2(START));
        map = new GameMap(new Tree(1, 3));
        controlsProcessor = new ControlsProcessor(tank, map, keyboard);
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

        controlsProcessor.processInput();
        tank.update(MOVEMENT_DURATION);

        assertEquals(expected.rotation(), tank.getRotation());
        assertEquals(new GridPoint2(START.x + expected.dx(), START.y + expected.dy()), tank.getCoordinates());
    }

    @Test
    void tankStaysStillWhileNothingIsPressed() {
        controlsProcessor.processInput();
        tank.update(MOVEMENT_DURATION);

        assertEquals(START, tank.getCoordinates());
        assertFalse(tank.isMoving());
    }

    @Test
    void unrelatedKeysAreIgnored() {
        keyboard.press(Input.Keys.SPACE, Input.Keys.ESCAPE);

        controlsProcessor.processInput();
        tank.update(MOVEMENT_DURATION);

        assertEquals(START, tank.getCoordinates());
    }

    @Test
    void everyMovementIsBoundToTwoEquivalentKeys() {
        for (ControlsProcessor.Movements movement : ControlsProcessor.Movements.values()) {
            assertEquals(2, movement.getEquivalentKeys().length, movement + " must be bound to a pair of keys");
        }
    }

    @Test
    void obstacleStopsTheTankControlledByKeyboard() {
        keyboard.press(Input.Keys.W);

        // (1,1) -> (1,2), then the tree at (1,3) blocks the next step
        controlsProcessor.processInput();
        tank.update(MOVEMENT_DURATION);
        controlsProcessor.processInput();
        tank.update(MOVEMENT_DURATION);

        assertEquals(new GridPoint2(1, 2), tank.getCoordinates());
    }

    @Test
    void keyHeldDownKeepsTheTankMoving() {
        keyboard.press(Input.Keys.RIGHT);

        for (int step = 0; step < 3; step++) {
            controlsProcessor.processInput();
            tank.update(MOVEMENT_DURATION);
        }

        assertEquals(new GridPoint2(START.x + 3, START.y), tank.getCoordinates());
    }
}
