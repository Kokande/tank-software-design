package ru.mipt.bit.platformer.level;

import com.badlogic.gdx.math.GridPoint2;
import ru.mipt.bit.platformer.objects.Tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class RandomLevelGenerator implements LevelGenerator {
    private final int width;
    private final int height;
    private final int treeCount;
    private final Random random;

    public RandomLevelGenerator(int width, int height, int treeCount) {
        this(width, height, treeCount, new Random());
    }

    public RandomLevelGenerator(int width, int height, int treeCount, Random random) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Level size must be positive: " + width + "x" + height);
        }
        // one tile is always left for the player
        if (treeCount < 0 || treeCount >= width * height) {
            throw new IllegalArgumentException("Can not place " + treeCount + " trees on a " + width + "x" + height + " level");
        }
        this.width = width;
        this.height = height;
        this.treeCount = treeCount;
        this.random = random;
    }

    @Override
    public Level generate() {
        List<GridPoint2> tiles = new ArrayList<>(width * height);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                tiles.add(new GridPoint2(x, y));
            }
        }
        Collections.shuffle(tiles, random);

        GridPoint2 playerPosition = tiles.get(0);
        List<Tree> trees = new ArrayList<>(treeCount);
        for (GridPoint2 tile : tiles.subList(1, treeCount + 1)) {
            trees.add(new Tree(tile.x, tile.y));
        }
        return new Level(playerPosition, trees);
    }
}
