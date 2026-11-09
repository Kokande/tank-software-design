package ru.mipt.bit.platformer.level;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileLevelGeneratorTest {
    @Test
    void rejectsRowsWiderThanTheMap() {
        for (String row : List.of("X_T", "__X", "X__")) {
            assertThrows(IllegalArgumentException.class,
                    () -> FileLevelGenerator.parse(List.of(row), 2, 1));
        }
    }

    @Test
    void acceptsObjectsAtTheRightEdge() {
        Level level = FileLevelGenerator.parse(List.of("__T", "__X"), 3, 2);

        assertEquals(new GridPoint2(2, 0), level.getPlayerPosition());
        assertEquals(new GridPoint2(2, 1), level.getTrees().get(0).getCoordinates());
    }

    @Test
    void rejectsNonPositiveDimensions(@TempDir Path directory) {
        for (int[] size : new int[][]{{0, 2}, {-1, 2}, {2, 0}, {2, -1}}) {
            assertThrows(IllegalArgumentException.class,
                    () -> new FileLevelGenerator(directory.resolve("level.txt"), size[0], size[1]));
        }
    }

    @Test
    void firstLineIsTheTopRowOfTheLevel() {
        Level level = FileLevelGenerator.parse(List.of(
                "T__",
                "_X_",
                "__T"
        ), 3, 3);

        assertEquals(new GridPoint2(1, 1), level.getPlayerPosition());
        assertEquals(2, level.getTrees().size());
        assertEquals(new GridPoint2(0, 2), level.getTrees().get(0).getCoordinates());
        assertEquals(new GridPoint2(2, 0), level.getTrees().get(1).getCoordinates());
    }

    @Test
    void shortFileIsPlacedAtTheTopOfATallerLevel() {
        Level level = FileLevelGenerator.parse(List.of("X"), 1, 5);

        assertEquals(new GridPoint2(0, 4), level.getPlayerPosition());
    }

    @Test
    void blankLinesAndSurroundingSpacesAreIgnored() {
        Level level = FileLevelGenerator.parse(List.of("", "  _T  ", "   ", "X_"), 2, 2);

        assertEquals(new GridPoint2(0, 0), level.getPlayerPosition());
        assertEquals(new GridPoint2(1, 1), level.getTrees().get(0).getCoordinates());
    }

    @Test
    void fileLevelHasNoAiTanks() {
        Level level = FileLevelGenerator.parse(List.of("X"), 1, 1);

        assertTrue(level.getTankPositions().isEmpty());
    }

    @Test
    void rejectsLevelWithoutPlayer() {
        assertThrows(IllegalArgumentException.class, () -> FileLevelGenerator.parse(List.of("T_"), 2, 1));
    }

    @Test
    void rejectsLevelWithSeveralPlayers() {
        assertThrows(IllegalArgumentException.class, () -> FileLevelGenerator.parse(List.of("XX"), 2, 1));
    }

    @Test
    void rejectsUnknownTiles() {
        assertThrows(IllegalArgumentException.class, () -> FileLevelGenerator.parse(List.of("X?"), 2, 1));
    }

    @Test
    void rejectsLevelWhichDoesNotFit() {
        assertThrows(IllegalArgumentException.class, () -> FileLevelGenerator.parse(List.of("X", "_"), 2, 1));
    }

    @Test
    void readsTheLevelFromAFile(@TempDir Path directory) throws IOException {
        Path file = directory.resolve("level.txt");
        Files.write(file, List.of("_T", "X_"));

        Level level = new FileLevelGenerator(file, 2, 2).generate();

        assertEquals(new GridPoint2(0, 0), level.getPlayerPosition());
        assertEquals(new GridPoint2(1, 1), level.getTrees().get(0).getCoordinates());
    }

    @Test
    void reportsAMissingFile(@TempDir Path directory) {
        FileLevelGenerator generator = new FileLevelGenerator(directory.resolve("missing.txt"), 2, 2);

        assertThrows(UncheckedIOException.class, generator::generate);
    }
}
