package ru.mipt.bit.platformer.controls.keyboard;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.GridPoint2;
import org.junit.jupiter.api.Test;
import ru.mipt.bit.platformer.controls.commands.Command;
import ru.mipt.bit.platformer.controls.commands.ToggleHealthBarsCommand;
import ru.mipt.bit.platformer.controls.ControlsProcessor;
import ru.mipt.bit.platformer.controls.support.FakeKeyboard;
import ru.mipt.bit.platformer.graphics.health.HealthBarVisibility;
import ru.mipt.bit.platformer.mechanics.GameWorld;
import ru.mipt.bit.platformer.objects.Tank;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HealthBarControlsTest {
    @Test
    void togglesOnlyOnExecutionAndOncePerPress() {
        HealthBarVisibility visibility = new HealthBarVisibility();
        FakeKeyboard keyboard = new FakeKeyboard();
        KeyboardHealthBarStrategy strategy = new KeyboardHealthBarStrategy(keyboard, visibility);
        assertFalse(visibility.isVisible());
        assertTrue(strategy.nextCommand().isEmpty());

        keyboard.press(Input.Keys.L);
        Command command = strategy.nextCommand().orElseThrow();
        assertTrue(command instanceof ToggleHealthBarsCommand);
        assertFalse(visibility.isVisible());
        command.execute();
        assertTrue(visibility.isVisible());
        for (int frame = 0; frame < 10; frame++) assertTrue(strategy.nextCommand().isEmpty());

        keyboard.press();
        assertTrue(strategy.nextCommand().isEmpty());
        assertTrue(visibility.isVisible());
        keyboard.press(Input.Keys.L);
        strategy.nextCommand().orElseThrow().execute();
        assertFalse(visibility.isVisible());
    }

    @Test
    void toggleWorksAlongsideMovementAndShootingAndAfterPlayerRemoval() {
        GameWorld world = new GameWorld(8, 8);
        Tank player = new Tank(new GridPoint2(2, 2), 0.4f);
        world.add(player);
        HealthBarVisibility visibility = new HealthBarVisibility();
        FakeKeyboard keyboard = new FakeKeyboard();
        KeyboardHealthBarStrategy toggle = new KeyboardHealthBarStrategy(keyboard, visibility);
        ControlsProcessor processor = new ControlsProcessor(List.of(toggle,
                new KeyboardControlStrategy(player, world, keyboard, KeyBindings.wasdAndArrows()),
                new KeyboardShootStrategy(player, keyboard)));
        keyboard.press(Input.Keys.L, Input.Keys.W, Input.Keys.SPACE);
        List<Command> commands = processor.createCommands();
        assertEquals(3, commands.size());
        processor.executeCommands(commands);
        assertTrue(visibility.isVisible());
        assertTrue(player.isMoving());
        assertEquals(2, world.getObjects().size());

        world.remove(player);
        keyboard.press();
        processor.processInput();
        keyboard.press(Input.Keys.L);
        processor.processInput();
        assertFalse(visibility.isVisible());
    }
}
