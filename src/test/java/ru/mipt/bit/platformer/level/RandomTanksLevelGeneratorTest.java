package ru.mipt.bit.platformer.level;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.jupiter.api.Test;
import ru.mipt.bit.platformer.objects.Tree;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RandomTanksLevelGeneratorTest {
    private static final GridPoint2 PLAYER = new GridPoint2(0, 0);
    private static final List<Tree> TREES = List.of(new Tree(1, 0), new Tree(0, 1));

    private static final LevelGenerator BASE = () -> new Level(PLAYER, TREES);

    @Test
    void addsTheRequestedNumberOfTanks() {
        Level level = new RandomTanksLevelGenerator(BASE, 5, 5, 4, new Random(1)).generate();

        assertEquals(4, level.getTankPositions().size());
    }

    @Test
    void keepsThePlayerAndTheTreesOfTheBaseLevel() {
        Level level = new RandomTanksLevelGenerator(BASE, 5, 5, 4, new Random(1)).generate();

        assertEquals(PLAYER, level.getPlayerPosition());
        assertEquals(2, level.getTrees().size());
        assertSame(TREES.get(0), level.getTrees().get(0));
        assertSame(TREES.get(1), level.getTrees().get(1));
    }

    @Test
    void placesTanksOnlyOnFreeTilesInsideTheLevel() {
        for (int seed = 0; seed < 50; seed++) {
            Level level = new RandomTanksLevelGenerator(BASE, 3, 3, 6, new Random(seed)).generate();

            Set<GridPoint2> taken = new HashSet<>();
            taken.add(level.getPlayerPosition());
            for (Tree tree : level.getTrees()) {
                taken.add(tree.getCoordinates());
            }
            for (GridPoint2 tank : level.getTankPositions()) {
                assertTrue(tank.x >= 0 && tank.x < 3 && tank.y >= 0 && tank.y < 3, "tank outside the level at " + tank);
                assertTrue(taken.add(tank), "tile " + tank + " is used twice");
            }
        }
    }

    @Test
    void canFillEveryFreeTile() {
        Level level = new RandomTanksLevelGenerator(BASE, 3, 3, 6, new Random(1)).generate();

        assertEquals(6, new HashSet<>(level.getTankPositions()).size());
    }

    @Test
    void failsWhenThereIsNotEnoughFreeTiles() {
        RandomTanksLevelGenerator generator = new RandomTanksLevelGenerator(BASE, 3, 3, 7, new Random(1));

        assertThrows(IllegalStateException.class, generator::generate);
    }

    @Test
    void keepsTanksOfTheBaseLevelAndDoesNotPutNewOnesOnThem() {
        GridPoint2 existing = new GridPoint2(2, 2);
        LevelGenerator base = () -> new Level(PLAYER, TREES, List.of(existing));

        Level level = new RandomTanksLevelGenerator(base, 3, 3, 5, new Random(1)).generate();

        assertEquals(6, level.getTankPositions().size());
        assertEquals(existing, level.getTankPositions().get(0));
        assertEquals(6, new HashSet<>(level.getTankPositions()).size());
    }

    @Test
    void zeroTanksLeaveTheLevelUnchanged() {
        Level level = new RandomTanksLevelGenerator(BASE, 5, 5, 0, new Random(1)).generate();

        assertTrue(level.getTankPositions().isEmpty());
    }

    @Test
    void sameSeedGivesTheSameTanks() {
        Level first = new RandomTanksLevelGenerator(BASE, 10, 10, 5, new Random(9)).generate();
        Level second = new RandomTanksLevelGenerator(BASE, 10, 10, 5, new Random(9)).generate();

        assertEquals(first.getTankPositions(), second.getTankPositions());
    }

    @Test
    void differentSeedsGiveDifferentTanks() {
        Level first = new RandomTanksLevelGenerator(BASE, 10, 10, 5, new Random(1)).generate();
        Level second = new RandomTanksLevelGenerator(BASE, 10, 10, 5, new Random(2)).generate();

        assertNotEquals(first.getTankPositions(), second.getTankPositions());
    }

    @Test
    void rejectsInvalidArguments() {
        assertThrows(IllegalArgumentException.class, () -> new RandomTanksLevelGenerator(BASE, 0, 5, 1));
        assertThrows(IllegalArgumentException.class, () -> new RandomTanksLevelGenerator(BASE, 5, -1, 1));
        assertThrows(IllegalArgumentException.class, () -> new RandomTanksLevelGenerator(BASE, 5, 5, -1));
    }

    @Test
    void worksOnTopOfTheRandomLevelGenerator() {
        LevelGenerator generator = new RandomTanksLevelGenerator(new RandomLevelGenerator(4, 4, 5, new Random(3)), 4, 4, 10, new Random(3));

        Level level = generator.generate();

        Set<GridPoint2> tiles = new HashSet<>(level.getTankPositions());
        tiles.add(level.getPlayerPosition());
        for (Tree tree : level.getTrees()) {
            tiles.add(tree.getCoordinates());
        }
        assertEquals(16, tiles.size(), "every tile of a 4x4 level is used exactly once");
        assertFalse(level.getTankPositions().contains(level.getPlayerPosition()));
    }
}
