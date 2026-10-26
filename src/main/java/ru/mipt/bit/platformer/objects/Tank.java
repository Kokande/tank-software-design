package ru.mipt.bit.platformer.objects;

import com.badlogic.gdx.math.GridPoint2;
import ru.mipt.bit.platformer.mechanics.CollisionChecker;
import ru.mipt.bit.platformer.mechanics.Movable;
import ru.mipt.bit.platformer.mechanics.Obstacle;

import static com.badlogic.gdx.math.MathUtils.isEqual;
import static ru.mipt.bit.platformer.util.GdxGameUtils.*;

public class Tank implements Movable, Obstacle {
    private final float movementSpeed;

    private GridPoint2 tankCoordinates;
    private GridPoint2 tankDestinationCoordinates;
    private float tankMovementProgress = 1f;
    private float tankRotation;

    public Tank(GridPoint2 initialCoordinates, float movementSpeed) {
        this.movementSpeed = movementSpeed;
        tankDestinationCoordinates = new GridPoint2(initialCoordinates);
        tankCoordinates = new GridPoint2(initialCoordinates);
        tankRotation = 0f;
    }

    public void update(float deltaTime) {
        tankMovementProgress = continueProgress(tankMovementProgress, deltaTime, movementSpeed);
        if (isEqual(tankMovementProgress, 1f)) {
            // record that the tank has reached his/her destination
            tankCoordinates.set(tankDestinationCoordinates);
        }
    }

    @Override
    public void tryMovement(int dx, int dy, float newRotation, CollisionChecker obstacles) {
        tankRotation = newRotation;
        if (!isEqual(tankMovementProgress, 1f)) {
            return;
        }
        GridPoint2 target = new GridPoint2(tankCoordinates.x + dx, tankCoordinates.y + dy);
        if (obstacles.collides(target)) {
            return;
        }

        tankDestinationCoordinates.set(target);
        tankMovementProgress = 0f;
    }

    @Override
    public boolean isMoving() {
        return !isEqual(tankMovementProgress, 1f);
    }

    // a moving tank takes both the tile it leaves and the tile it is heading to
    @Override
    public boolean occupies(GridPoint2 point) {
        return tankCoordinates.equals(point) || tankDestinationCoordinates.equals(point);
    }

    public GridPoint2 getCoordinates() {
        return tankCoordinates;
    }

    public GridPoint2 getDestinationCoordinates() {
        return tankDestinationCoordinates;
    }

    public float getMovementProgress() {
        return tankMovementProgress;
    }

    public float getRotation() {
        return tankRotation;
    }
}
