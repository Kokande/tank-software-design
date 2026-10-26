package ru.mipt.bit.platformer.level;

import com.badlogic.gdx.math.GridPoint2;
import ru.mipt.bit.platformer.objects.Tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Level {
    private final GridPoint2 playerPosition;
    private final List<Tree> trees;
    // tanks which are not controlled by the player
    private final List<GridPoint2> tankPositions;

    public Level(GridPoint2 playerPosition, List<Tree> trees) {
        this(playerPosition, trees, Collections.emptyList());
    }

    public Level(GridPoint2 playerPosition, List<Tree> trees, List<GridPoint2> tankPositions) {
        this.playerPosition = new GridPoint2(playerPosition);
        this.trees = Collections.unmodifiableList(new ArrayList<>(trees));
        List<GridPoint2> positions = new ArrayList<>(tankPositions.size());
        for (GridPoint2 position : tankPositions) {
            positions.add(new GridPoint2(position));
        }
        this.tankPositions = Collections.unmodifiableList(positions);
    }

    public GridPoint2 getPlayerPosition() {
        return playerPosition;
    }

    public List<Tree> getTrees() {
        return trees;
    }

    public List<GridPoint2> getTankPositions() {
        return tankPositions;
    }
}
