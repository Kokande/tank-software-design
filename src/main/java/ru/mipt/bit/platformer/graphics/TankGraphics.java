package ru.mipt.bit.platformer.graphics;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import ru.mipt.bit.platformer.objects.Tank;
import ru.mipt.bit.platformer.util.TileMovement;

import static ru.mipt.bit.platformer.util.GdxGameUtils.*;

public class TankGraphics implements Renderable {
    private final Texture tankTexture;
    private final TextureRegion tankGraphics;
    private final Rectangle tankRectangle;
    private final TileMovement tileMovement;
    private final Tank tank;

    public TankGraphics(TileMovement tileMovement, Tank tank, String texturePath) {
        this.tankTexture = new Texture(texturePath);
        // TextureRegion represents Texture portion, there may be many TextureRegion instances of the same Texture
        this.tankGraphics = new TextureRegion(tankTexture);
        this.tankRectangle = createBoundingRectangle(tankGraphics);
        this.tileMovement = tileMovement;
        this.tank = tank;
    }

    @Override
    public void render(Batch batch) {
        // calculate interpolated tank screen coordinates
        tileMovement.moveRectangleBetweenTileCenters(tankRectangle, tank.getCoordinates(), tank.getDestinationCoordinates(), tank.getMovementProgress());
        drawTextureRegionUnscaled(batch, tankGraphics, tankRectangle, tank.getRotation());
    }

    @Override
    public void dispose() {
        tankTexture.dispose();
    }
}
