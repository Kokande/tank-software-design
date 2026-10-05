package ru.mipt.bit.platformer.objects;

import com.badlogic.gdx.math.GridPoint2;

import static com.badlogic.gdx.math.MathUtils.isEqual;
import static ru.mipt.bit.platformer.util.GdxGameUtils.*;

public class Tank implements Movable {
    private final float movementSpeed;

    // player current position coordinates on level 10x8 grid (e.g. x=0, y=1)
    private GridPoint2 playerCoordinates;
    // which tile the player want to go next
    private GridPoint2 playerDestinationCoordinates;
    private float playerMovementProgress = 1f;
    private float playerRotation;

    public Tank(GridPoint2 initialCoordinates, float movementSpeed) {
        this.movementSpeed = movementSpeed;
        // set player initial position
        playerDestinationCoordinates = new GridPoint2(initialCoordinates);
        playerCoordinates = new GridPoint2(initialCoordinates);
        playerRotation = 0f;
    }

    public void update(float deltaTime) {
        playerMovementProgress = continueProgress(playerMovementProgress, deltaTime, movementSpeed);
        if (isEqual(playerMovementProgress, 1f)) {
            // record that the player has reached his/her destination
            playerCoordinates.set(playerDestinationCoordinates);
        }
    }

    @Override
    public void tryMovement(int dx, int dy, float newRotation, CollisionChecker obstacles) {
        playerRotation = newRotation;
        if (!isEqual(playerMovementProgress, 1f)) {
            return;
        }
        GridPoint2 target = new GridPoint2(playerCoordinates.x + dx, playerCoordinates.y + dy);
        if (obstacles.collides(target)) {
            return;
        }

        playerDestinationCoordinates.set(target);
        playerMovementProgress = 0f;
    }

    public boolean isMoving() {
        return !isEqual(playerMovementProgress, 1f);
    }

    public GridPoint2 getCoordinates() {
        return playerCoordinates;
    }

    public GridPoint2 getDestinationCoordinates() {
        return playerDestinationCoordinates;
    }

    public float getMovementProgress() {
        return playerMovementProgress;
    }

    public float getRotation() {
        return playerRotation;
    }
}
