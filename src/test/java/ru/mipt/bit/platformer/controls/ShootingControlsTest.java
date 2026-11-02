package ru.mipt.bit.platformer.controls;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.GridPoint2;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import ru.mipt.bit.platformer.mechanics.Direction;
import ru.mipt.bit.platformer.mechanics.GameWorld;
import ru.mipt.bit.platformer.objects.Bullet;
import ru.mipt.bit.platformer.objects.Tank;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class ShootingControlsTest {
    @Test
    void shootCommandDelegatesOnlyWhenExecuted() {
        int[] shots = {0};
        Command command = new ShootCommand(() -> shots[0]++);
        assertEquals(0, shots[0]);
        command.execute();
        assertEquals(1, shots[0]);
    }

    @Test
    void spaceAndMovementCanProduceCommandsInTheSameFrame() {
        GameWorld world = new GameWorld(8, 8);
        Tank tank = new Tank(new GridPoint2(2, 2), 0.4f);
        world.add(tank);
        FakeKeyboard keyboard = new FakeKeyboard();
        KeyboardShootStrategy shooting = new KeyboardShootStrategy(tank, keyboard);
        assertTrue(shooting.nextCommand().isEmpty());
        keyboard.press(Input.Keys.SPACE, Input.Keys.W);
        ControlsProcessor processor = new ControlsProcessor(List.of(
                new KeyboardControlStrategy(tank, world, keyboard, KeyBindings.wasdAndArrows()), shooting));
        List<Command> commands = processor.createCommands();
        assertEquals(2, commands.size());
        assertFalse(tank.isMoving());
        assertEquals(1, world.getObjects().size());
        processor.executeCommands(commands);
        assertTrue(tank.isMoving());
        Bullet bullet = (Bullet) world.getObjects().get(1);
        assertEquals(Direction.UP, bullet.getDirection());
        assertEquals(100, tank.getHealth());
    }

    @Test
    void randomStrategyCanSelectShooting() {
        Tank tank = new Tank(new GridPoint2(1, 1), 0.4f);
        GameWorld world = new GameWorld(8, 8);
        world.add(tank);
        Random random = new Random() {
            public int nextInt(int bound) {
                assertEquals(Direction.values().length + 1, bound);
                return bound - 1;
            }
        };
        Command command = new RandomControlStrategy(tank, world, random).nextCommand().orElseThrow();
        assertTrue(command instanceof ShootCommand);
        command.execute();
        assertEquals(2, world.getObjects().size());
    }

    @ParameterizedTest
    @EnumSource(RecommendedAction.class)
    void adaptsEveryRecommendedAction(RecommendedAction action) {
        Tank tank = new Tank(new GridPoint2(3, 3), 0.4f);
        GameWorld world = new GameWorld(8, 8);
        world.add(tank);
        ControlStrategy adapter = new RecommendationControlStrategy(tank, world, () -> action);
        Optional<Command> command = adapter.nextCommand();
        if (action == RecommendedAction.WAIT) {
            assertTrue(command.isEmpty());
        } else if (action == RecommendedAction.SHOOT) {
            assertTrue(command.orElseThrow() instanceof ShootCommand);
            command.get().execute();
            assertEquals(2, world.getObjects().size());
        } else {
            assertEquals(Direction.valueOf(action.name()), ((MoveCommand) command.orElseThrow()).getDirection());
        }
    }

    @Test
    void everyStrategyCreatesItsCommandBeforeAnyCommandExecutes() {
        List<String> events = new ArrayList<>();
        ControlsProcessor processor = new ControlsProcessor(List.of(
                () -> { events.add("create first"); return Optional.of(() -> events.add("execute first")); },
                () -> { events.add("create second"); return Optional.of(() -> events.add("execute second")); }
        ));
        processor.processInput();
        assertEquals(List.of("create first", "create second", "execute first", "execute second"), events);
    }
}
