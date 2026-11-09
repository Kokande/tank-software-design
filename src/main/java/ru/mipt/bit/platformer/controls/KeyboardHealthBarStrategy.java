package ru.mipt.bit.platformer.controls;

import com.badlogic.gdx.Input;
import ru.mipt.bit.platformer.graphics.HealthBarVisibility;

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
