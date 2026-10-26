package ru.mipt.bit.platformer.controls;

import ru.mipt.bit.platformer.mechanics.CollisionChecker;
import ru.mipt.bit.platformer.mechanics.Direction;
import ru.mipt.bit.platformer.mechanics.Movable;

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
        Direction direction = DIRECTIONS[random.nextInt(DIRECTIONS.length)];
        return Optional.of(new MoveCommand(movable, direction, environment));
    }
}
