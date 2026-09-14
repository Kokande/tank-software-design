package ru.mipt.bit.platformer.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Rectangle;

import static ru.mipt.bit.platformer.util.GdxGameUtils.*;

public class Tree {
    private final Texture treeTexture;
    private final TextureRegion treeObstacleGraphics;
    private final GridPoint2 treeObstacleCoordinates;
    private final Rectangle treeObstacleRectangle;

    public Tree(TiledMapTileLayer groundLayer, int x, int y) {
        this.treeTexture = new Texture("images/greenTree.png");
        this.treeObstacleGraphics = new TextureRegion(treeTexture);
        this.treeObstacleCoordinates = new GridPoint2(x, y);
        this.treeObstacleRectangle = createBoundingRectangle(treeObstacleGraphics);
        moveRectangleAtTileCenter(groundLayer, treeObstacleRectangle, treeObstacleCoordinates);
    }

    public void render(Batch batch) {
        drawTextureRegionUnscaled(batch, treeObstacleGraphics, treeObstacleRectangle, 0f);
    }

    public void dispose() {
        treeTexture.dispose();
    }

    public GridPoint2 getCoordinates() {
        return treeObstacleCoordinates;
    }
}
