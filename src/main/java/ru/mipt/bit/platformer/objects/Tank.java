package ru.mipt.bit.platformer.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Rectangle;
import ru.mipt.bit.platformer.util.GameMap;
import ru.mipt.bit.platformer.util.TileMovement;

import static com.badlogic.gdx.math.MathUtils.isEqual;
import static ru.mipt.bit.platformer.util.GdxGameUtils.*;

public class Tank {
    private static final float MOVEMENT_SPEED = 0.4f;

    private final Texture tankTexture;
    private final TextureRegion playerGraphics;
    private final Rectangle playerRectangle;
    // player current position coordinates on level 10x8 grid (e.g. x=0, y=1)
    private GridPoint2 playerCoordinates;
    // which tile the player want to go next
    private GridPoint2 playerDestinationCoordinates;
    private float playerMovementProgress = 1f;
    private float playerRotation;

    public Tank() {
        tankTexture = new Texture("images/tank_blue.png");
        // TextureRegion represents Texture portion, there may be many TextureRegion instances of the same Texture
        playerGraphics = new TextureRegion(tankTexture);
        playerRectangle = createBoundingRectangle(playerGraphics);
        // set player initial position
        playerDestinationCoordinates = new GridPoint2(1, 1);
        playerCoordinates = new GridPoint2(playerDestinationCoordinates);
        playerRotation = 0f;
    }

    public void render(Batch batch) {
        drawTextureRegionUnscaled(batch, playerGraphics, playerRectangle, playerRotation);
    }

    public void dispose() {
        tankTexture.dispose();
    }

    public void update(float deltaTime, TileMovement tileMovement) {
        tileMovement.moveRectangleBetweenTileCenters(playerRectangle, playerCoordinates, playerDestinationCoordinates, playerMovementProgress);

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
}
