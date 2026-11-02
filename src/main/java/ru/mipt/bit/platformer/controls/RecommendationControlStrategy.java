package ru.mipt.bit.platformer.controls;

import ru.mipt.bit.platformer.mechanics.CollisionChecker;
import ru.mipt.bit.platformer.mechanics.Direction;
import ru.mipt.bit.platformer.objects.Tank;

import java.util.Optional;
import java.util.function.Supplier;

public class RecommendationControlStrategy implements ControlStrategy {
    private final Tank tank;
    private final CollisionChecker environment;
    private final Supplier<RecommendedAction> recommendations;

    public RecommendationControlStrategy(Tank tank, CollisionChecker environment,
                                         Supplier<RecommendedAction> recommendations) {
        this.tank = tank;
        this.environment = environment;
        this.recommendations = recommendations;
    }

    @Override
    public Optional<Command> nextCommand() {
        RecommendedAction action = recommendations.get();
        if (action == RecommendedAction.WAIT) return Optional.empty();
        if (action == RecommendedAction.SHOOT) return Optional.of(new ShootCommand(tank));
        return Optional.of(new MoveCommand(tank, Direction.valueOf(action.name()), environment));
    }
}
