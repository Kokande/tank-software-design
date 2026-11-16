package ru.mipt.bit.platformer.controls.commands;

import ru.mipt.bit.platformer.mechanics.Shooter;

/**
 * Application: shooting use case implementing Command through the Shooter domain port
 */
public class ShootCommand implements Command {
    private final Shooter shooter;

    public ShootCommand(Shooter shooter) {
        this.shooter = shooter;
    }

    @Override
    public void execute() {
        shooter.shoot();
    }
}
