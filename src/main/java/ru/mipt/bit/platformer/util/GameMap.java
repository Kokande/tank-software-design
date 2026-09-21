package ru.mipt.bit.platformer.util;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.GridPoint2;
import ru.mipt.bit.platformer.objects.Tree;

public class GameMap {
    private final Tree[] trees;

    public GameMap(TiledMapTileLayer groundLayer) {
        // Tree array
        trees = new Tree[1];
        trees[0] = new Tree(groundLayer, 1, 3);
    }

    public void render(Batch batch) {
        for (Tree tree : trees) {
            tree.render(batch);
        }
    }

    public void dispose() {
        for (Tree tree : trees) {
            tree.dispose();
        }
    }

    public boolean collides(GridPoint2 point) {
        for (Tree tree : trees) {
            if (point.equals(tree.getCoordinates())) {
                return true;
            }
        }
        return false;
    }
}
