package ru.mipt.bit.platformer.graphics;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import ru.mipt.bit.platformer.objects.Tank;
import ru.mipt.bit.platformer.util.TileMovement;

import static ru.mipt.bit.platformer.util.GdxGameUtils.*;

public class TankGraphics {
    private final Texture tankTexture;
    private final TextureRegion playerGraphics;
    private final Rectangle playerRectangle;
    private final TileMovement tileMovement;
    private final Tank player;

    public TankGraphics(TileMovement tileMovement, Tank player) {
        this.tankTexture = new Texture("images/tank_blue.png");
        // TextureRegion represents Texture portion, there may be many TextureRegion instances of the same Texture
        this.playerGraphics = new TextureRegion(tankTexture);
        this.playerRectangle = createBoundingRectangle(playerGraphics);
        this.tileMovement = tileMovement;
        this.player = player;
    }

    public void render(Batch batch) {
        // calculate interpolated player screen coordinates
        tileMovement.moveRectangleBetweenTileCenters(playerRectangle, player.getCoordinates(), player.getDestinationCoordinates(), player.getMovementProgress());
        drawTextureRegionUnscaled(batch, playerGraphics, playerRectangle, player.getRotation());
    }

    public void dispose() {
        tankTexture.dispose();
    }
}
