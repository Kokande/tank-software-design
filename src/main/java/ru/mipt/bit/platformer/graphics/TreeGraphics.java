package ru.mipt.bit.platformer.graphics;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import ru.mipt.bit.platformer.objects.Tree;

import static ru.mipt.bit.platformer.util.GdxGameUtils.*;

public class TreeGraphics implements Renderable {
    private final Texture treeTexture;
    private final TextureRegion treeObstacleGraphics;
    private final Rectangle treeObstacleRectangle;

    public TreeGraphics(TiledMapTileLayer groundLayer, Tree tree, String texturePath) {
        this.treeTexture = new Texture(texturePath);
        this.treeObstacleGraphics = new TextureRegion(treeTexture);
        this.treeObstacleRectangle = createBoundingRectangle(treeObstacleGraphics);
        moveRectangleAtTileCenter(groundLayer, treeObstacleRectangle, tree.getCoordinates());
    }

    @Override
    public void render(Batch batch) {
        drawTextureRegionUnscaled(batch, treeObstacleGraphics, treeObstacleRectangle, 0f);
    }

    @Override
    public void dispose() {
        treeTexture.dispose();
    }
}
