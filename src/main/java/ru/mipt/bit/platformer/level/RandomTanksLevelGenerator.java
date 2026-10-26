package ru.mipt.bit.platformer.level;

import com.badlogic.gdx.math.GridPoint2;
import ru.mipt.bit.platformer.objects.Tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class RandomTanksLevelGenerator implements LevelGenerator {
    private final LevelGenerator levelGenerator;
    private final int width;
    private final int height;
    private final int tankCount;
    private final Random random;

    public RandomTanksLevelGenerator(LevelGenerator levelGenerator, int width, int height, int tankCount) {
        this(levelGenerator, width, height, tankCount, new Random());
    }

    public RandomTanksLevelGenerator(LevelGenerator levelGenerator, int width, int height, int tankCount, Random random) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Level size must be positive: " + width + "x" + height);
        }
        if (tankCount < 0) {
            throw new IllegalArgumentException("Tank count must not be negative: " + tankCount);
        }
        this.levelGenerator = levelGenerator;
        this.width = width;
        this.height = height;
        this.tankCount = tankCount;
        this.random = random;
    }

    @Override
    public Level generate() {
        Level level = levelGenerator.generate();
        List<GridPoint2> freeTiles = findFreeTiles(level);
        if (freeTiles.size() < tankCount) {
            throw new IllegalStateException("Can not place " + tankCount + " tanks, only " + freeTiles.size() + " tiles are free");
        }
        Collections.shuffle(freeTiles, random);

        List<GridPoint2> tanks = new ArrayList<>(level.getTankPositions());
        tanks.addAll(freeTiles.subList(0, tankCount));
        return new Level(level.getPlayerPosition(), level.getTrees(), tanks);
    }

    private List<GridPoint2> findFreeTiles(Level level) {
        Set<GridPoint2> occupied = new HashSet<>(level.getTankPositions());
        occupied.add(level.getPlayerPosition());
        for (Tree tree : level.getTrees()) {
            occupied.add(tree.getCoordinates());
        }

        List<GridPoint2> freeTiles = new ArrayList<>();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                GridPoint2 tile = new GridPoint2(x, y);
                if (!occupied.contains(tile)) {
                    freeTiles.add(tile);
                }
            }
        }
        return freeTiles;
    }
}
