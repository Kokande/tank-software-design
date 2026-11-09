package ru.mipt.bit.platformer.controls.ai;

import ru.mipt.bit.platformer.controls.commands.Command;
import ru.mipt.bit.platformer.controls.commands.MoveCommand;
import ru.mipt.bit.platformer.controls.commands.ShootCommand;
import ru.mipt.bit.platformer.controls.ControlStrategy;
import ru.mipt.bit.platformer.mechanics.collision.CollisionChecker;
import ru.mipt.bit.platformer.mechanics.movement.Direction;
import ru.mipt.bit.platformer.mechanics.movement.Movable;
import ru.mipt.bit.platformer.mechanics.Shooter;

import java.util.Optional;
import java.util.Random;

public class RandomControlStrategy implements ControlStrategy {
    private static final Direction[] DIRECTIONS = Direction.values();

    private final Movable movable;
    private final CollisionChecker environment;
    private final Random random;

    public RandomControlStrategy(Movable movable, CollisionChecker environment) {
        this(movable, environment, new Random());
    }

    public RandomControlStrategy(Movable movable, CollisionChecker environment, Random random) {
        this.movable = movable;
        this.environment = environment;
        this.random = random;
    }

    @Override
    public Optional<Command> nextCommand() {
        if (movable.isMoving()) {
            return Optional.empty();
        }
        int choice = random.nextInt(DIRECTIONS.length + (movable instanceof Shooter ? 1 : 0));
        if (choice == DIRECTIONS.length) return Optional.of(new ShootCommand((Shooter) movable));
        Direction direction = DIRECTIONS[choice];
        return Optional.of(new MoveCommand(movable, direction, environment));
    }
}
