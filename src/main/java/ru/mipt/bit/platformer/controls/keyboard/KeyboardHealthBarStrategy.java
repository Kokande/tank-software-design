package ru.mipt.bit.platformer.controls.keyboard;

import com.badlogic.gdx.Input;
import ru.mipt.bit.platformer.controls.commands.Command;
import ru.mipt.bit.platformer.controls.commands.ToggleHealthBarsCommand;
import ru.mipt.bit.platformer.controls.ControlStrategy;
import ru.mipt.bit.platformer.graphics.health.HealthBarVisibility;

import java.util.Optional;

public class KeyboardHealthBarStrategy implements ControlStrategy {
    private final KeyboardState keyboard;
    private final Command toggleCommand;
    private boolean wasPressed;

    public KeyboardHealthBarStrategy(KeyboardState keyboard, HealthBarVisibility visibility) {
        this.keyboard = keyboard;
        this.toggleCommand = new ToggleHealthBarsCommand(visibility);
    }

    @Override
    public Optional<Command> nextCommand() {
        boolean pressed = keyboard.isKeyPressed(Input.Keys.L);
        boolean justPressed = pressed && !wasPressed;
        wasPressed = pressed;
        return justPressed ? Optional.of(toggleCommand) : Optional.empty();
    }
}
