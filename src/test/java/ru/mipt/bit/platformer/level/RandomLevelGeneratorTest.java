package ru.mipt.bit.platformer.level;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.jupiter.api.Test;
import ru.mipt.bit.platformer.objects.Tree;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RandomLevelGeneratorTest {

    @Test
    void placesTheRequestedNumberOfTrees() {
        Level level = new RandomLevelGenerator(10, 8, 12, new Random(1)).generate();

        assertEquals(12, level.getTrees().size());
        assertTrue(level.getTankPositions().isEmpty());
    }

    @Test
    void everythingIsInsideTheLevelOnDistinctTiles() {
        for (int seed = 0; seed < 50; seed++) {
            Level level = new RandomLevelGenerator(4, 3, 11, new Random(seed)).generate();

            Set<GridPoint2> tiles = new HashSet<>();
            tiles.add(level.getPlayerPosition());
            for (Tree tree : level.getTrees()) {
                tiles.add(tree.getCoordinates());
            }
            assertEquals(12, tiles.size());
            for (GridPoint2 tile : tiles) {
                assertTrue(tile.x >= 0 && tile.x < 4 && tile.y >= 0 && tile.y < 3, tile + " is outside the level");
            }
        }
    }

    @Test
    void sameSeedGivesTheSameLevel() {
        Level first = new RandomLevelGenerator(10, 8, 5, new Random(4)).generate();
        Level second = new RandomLevelGenerator(10, 8, 5, new Random(4)).generate();

        assertEquals(first.getPlayerPosition(), second.getPlayerPosition());
        for (int i = 0; i < 5; i++) {
            assertEquals(first.getTrees().get(i).getCoordinates(), second.getTrees().get(i).getCoordinates());
        }
    }

    @Test
    void rejectsInvalidArguments() {
        assertThrows(IllegalArgumentException.class, () -> new RandomLevelGenerator(0, 5, 1));
        assertThrows(IllegalArgumentException.class, () -> new RandomLevelGenerator(5, 0, 1));
        assertThrows(IllegalArgumentException.class, () -> new RandomLevelGenerator(5, 5, -1));
        // one tile must stay free for the player
        assertThrows(IllegalArgumentException.class, () -> new RandomLevelGenerator(2, 2, 4));
    }
}
