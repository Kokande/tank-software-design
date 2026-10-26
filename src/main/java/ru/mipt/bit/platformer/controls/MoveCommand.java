package ru.mipt.bit.platformer.controls;

import ru.mipt.bit.platformer.mechanics.CollisionChecker;
import ru.mipt.bit.platformer.mechanics.Direction;
import ru.mipt.bit.platformer.mechanics.Movable;

public class MoveCommand implements Command {
    private final Movable movable;
    private final Direction direction;
    private final CollisionChecker environment;

    public MoveCommand(Movable movable, Direction direction, CollisionChecker environment) {
        this.movable = movable;
        this.direction = direction;
        this.environment = environment;
    }

    @Override
    public void execute() {
        movable.tryMovement(direction.dx(), direction.dy(), direction.rotation(), environment);
    }

    public Direction getDirection() {
        return direction;
    }
}
