package ru.mipt.bit.platformer.level;

import com.badlogic.gdx.math.GridPoint2;
import ru.mipt.bit.platformer.objects.Tree;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FileLevelGenerator implements LevelGenerator {
    static final char TREE = 'T';
    static final char PLAYER = 'X';
    static final char EMPTY = '_';

    private final Path levelFile;
    private final int levelHeight;

    public FileLevelGenerator(Path levelFile, int levelHeight) {
        this.levelFile = levelFile;
        this.levelHeight = levelHeight;
    }

    @Override
    public Level generate() {
        try {
            return parse(Files.readAllLines(levelFile), levelHeight);
        } catch (IOException e) {
            throw new UncheckedIOException("Can not read level file " + levelFile, e);
        }
    }

    static Level parse(List<String> lines, int levelHeight) {
        List<String> rows = withoutBlankLines(lines);
        if (rows.size() > levelHeight) {
            throw new IllegalArgumentException("Level has " + rows.size() + " rows, but only " + levelHeight + " fit");
        }
        GridPoint2 playerPosition = null;
        List<Tree> trees = new ArrayList<>();

        for (int row = 0; row < rows.size(); row++) {
            String line = rows.get(row);
            // the first line of the file is the top row of the level, y axis points up
            int y = levelHeight - 1 - row;
            for (int x = 0; x < line.length(); x++) {
                char tile = line.charAt(x);
                if (tile == TREE) {
                    trees.add(new Tree(x, y));
                } else if (tile == PLAYER) {
                    if (playerPosition != null) {
                        throw new IllegalArgumentException("Level has more than one player position");
                    }
                    playerPosition = new GridPoint2(x, y);
                } else if (tile != EMPTY) {
                    throw new IllegalArgumentException("Unknown tile '" + tile + "' at line " + (row + 1) + ", column " + (x + 1));
                }
            }
        }

        if (playerPosition == null) {
            throw new IllegalArgumentException("Level has no player position");
        }
        return new Level(playerPosition, trees);
    }

    private static List<String> withoutBlankLines(List<String> lines) {
        List<String> rows = new ArrayList<>();
        for (String line : lines) {
            String row = line.trim();
            if (!row.isEmpty()) {
                rows.add(row);
            }
        }
        return rows;
    }
}
