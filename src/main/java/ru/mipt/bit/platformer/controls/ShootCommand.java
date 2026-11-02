package ru.mipt.bit.platformer.controls;

import ru.mipt.bit.platformer.mechanics.Shooter;

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
