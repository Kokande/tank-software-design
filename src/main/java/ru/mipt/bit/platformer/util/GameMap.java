package ru.mipt.bit.platformer.util;

import com.badlogic.gdx.math.GridPoint2;
import ru.mipt.bit.platformer.objects.Tree;

public class GameMap {
    private final Tree[] trees;

    public GameMap(Tree... trees) {
        // Tree array
        this.trees = trees;
    }

    public Tree[] getTrees() {
        return trees;
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
