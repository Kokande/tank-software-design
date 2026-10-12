package ru.mipt.bit.platformer.objects;

import com.badlogic.gdx.math.GridPoint2;
import ru.mipt.bit.platformer.mechanics.CollisionChecker;
import ru.mipt.bit.platformer.mechanics.Movable;

import static com.badlogic.gdx.math.MathUtils.isEqual;
import static ru.mipt.bit.platformer.util.GdxGameUtils.*;

public class Tank implements Movable {
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

    public boolean isMoving() {
        return !isEqual(tankMovementProgress, 1f);
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
