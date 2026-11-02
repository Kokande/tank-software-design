package ru.mipt.bit.platformer.controls;

import com.badlogic.gdx.Input;
import ru.mipt.bit.platformer.mechanics.Shooter;

import java.util.Optional;

public class KeyboardShootStrategy implements ControlStrategy {
    private final Shooter shooter;
    private final KeyboardState keyboard;

    public KeyboardShootStrategy(Shooter shooter, KeyboardState keyboard) {
        this.shooter = shooter;
        this.keyboard = keyboard;
    }

    @Override
    public Optional<Command> nextCommand() {
        return keyboard.isKeyPressed(Input.Keys.SPACE)
                ? Optional.of(new ShootCommand(shooter)) : Optional.empty();
    }
}
