package ru.mipt.bit.platformer.objects;

import com.badlogic.gdx.math.GridPoint2;
import ru.mipt.bit.platformer.util.GameMap;

import static com.badlogic.gdx.math.MathUtils.isEqual;
import static ru.mipt.bit.platformer.util.GdxGameUtils.*;

public class Tank {
    private static final float MOVEMENT_SPEED = 0.4f;

    // player current position coordinates on level 10x8 grid (e.g. x=0, y=1)
    private GridPoint2 playerCoordinates;
    // which tile the player want to go next
    private GridPoint2 playerDestinationCoordinates;
    private float playerMovementProgress = 1f;
    private float playerRotation;

    public Tank() {
        this(new GridPoint2(1, 1));
    }

    public Tank(GridPoint2 initialCoordinates) {
        // set player initial position
        playerDestinationCoordinates = new GridPoint2(initialCoordinates);
        playerCoordinates = new GridPoint2(initialCoordinates);
        playerRotation = 0f;
    }

    public void update(float deltaTime) {
        playerMovementProgress = continueProgress(playerMovementProgress, deltaTime, MOVEMENT_SPEED);
        if (isEqual(playerMovementProgress, 1f)) {
            // record that the player has reached his/her destination
            playerCoordinates.set(playerDestinationCoordinates);
        }
    }

    public void tryMovement(int dx, int dy, float newRotation, GameMap map) {
        playerRotation = newRotation;
        if (!isEqual(playerMovementProgress, 1f)) {
            return;
        }
        GridPoint2 target = new GridPoint2(playerCoordinates.x + dx, playerCoordinates.y + dy);
        if (map.collides(target)) {
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
