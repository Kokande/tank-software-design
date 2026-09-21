package ru.mipt.bit.platformer.graphics;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import ru.mipt.bit.platformer.objects.Tree;
import ru.mipt.bit.platformer.util.GameMap;

public class GameMapGraphics {
    private final TreeGraphics[] treesGraphics;

    public GameMapGraphics(TiledMapTileLayer groundLayer, GameMap map) {
        Tree[] trees = map.getTrees();
        treesGraphics = new TreeGraphics[trees.length];
        for (int i = 0; i < trees.length; i++) {
            treesGraphics[i] = new TreeGraphics(groundLayer, trees[i]);
        }
    }

    public void render(Batch batch) {
        for (TreeGraphics treeGraphics : treesGraphics) {
            treeGraphics.render(batch);
        }
    }

    public void dispose() {
        for (TreeGraphics treeGraphics : treesGraphics) {
            treeGraphics.dispose();
        }
    }
}
