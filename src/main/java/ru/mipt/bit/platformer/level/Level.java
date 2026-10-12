package ru.mipt.bit.platformer.level;

import com.badlogic.gdx.math.GridPoint2;
import ru.mipt.bit.platformer.objects.Tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Level {
    private final GridPoint2 playerPosition;
    private final List<Tree> trees;

    public Level(GridPoint2 playerPosition, List<Tree> trees) {
        this.playerPosition = new GridPoint2(playerPosition);
        this.trees = Collections.unmodifiableList(new ArrayList<>(trees));
    }

    public GridPoint2 getPlayerPosition() {
        return playerPosition;
    }

    public List<Tree> getTrees() {
        return trees;
    }
}
