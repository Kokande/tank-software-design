package ru.mipt.bit.platformer.controls.commands;

import ru.mipt.bit.platformer.mechanics.collision.CollisionChecker;
import ru.mipt.bit.platformer.mechanics.movement.Direction;
import ru.mipt.bit.platformer.mechanics.movement.Movable;

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
