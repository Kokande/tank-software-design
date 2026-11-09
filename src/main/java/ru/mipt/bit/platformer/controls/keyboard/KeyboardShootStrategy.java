package ru.mipt.bit.platformer.controls.keyboard;

import com.badlogic.gdx.Input;
import ru.mipt.bit.platformer.controls.commands.Command;
import ru.mipt.bit.platformer.controls.commands.ShootCommand;
import ru.mipt.bit.platformer.controls.ControlStrategy;
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
