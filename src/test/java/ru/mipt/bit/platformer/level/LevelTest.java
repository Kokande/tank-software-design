package ru.mipt.bit.platformer.level;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.jupiter.api.Test;
import ru.mipt.bit.platformer.objects.Tree;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LevelTest {

    @Test
    void hasNoTanksExceptThePlayerByDefault() {
        Level level = new Level(new GridPoint2(1, 1), List.of(new Tree(2, 2)));

        assertTrue(level.getTankPositions().isEmpty());
    }

    @Test
    void keepsEverythingItWasCreatedWith() {
        Level level = new Level(new GridPoint2(1, 1), List.of(new Tree(2, 2)), List.of(new GridPoint2(3, 3)));

        assertEquals(new GridPoint2(1, 1), level.getPlayerPosition());
        assertEquals(new GridPoint2(2, 2), level.getTrees().get(0).getCoordinates());
        assertEquals(List.of(new GridPoint2(3, 3)), level.getTankPositions());
    }

    @Test
    void doesNotShareStateWithItsArguments() {
        GridPoint2 player = new GridPoint2(1, 1);
        GridPoint2 tank = new GridPoint2(3, 3);
        List<Tree> trees = new ArrayList<>(List.of(new Tree(2, 2)));
        List<GridPoint2> tanks = new ArrayList<>(List.of(tank));
        Level level = new Level(player, trees, tanks);

        player.set(5, 5);
        tank.set(6, 6);
        trees.clear();
        tanks.clear();

        assertEquals(new GridPoint2(1, 1), level.getPlayerPosition());
        assertEquals(1, level.getTrees().size());
        assertEquals(List.of(new GridPoint2(3, 3)), level.getTankPositions());
    }

    @Test
    void contentCanNotBeModified() {
        Level level = new Level(new GridPoint2(1, 1), List.of(new Tree(2, 2)), List.of(new GridPoint2(3, 3)));

        assertThrows(UnsupportedOperationException.class, () -> level.getTrees().clear());
        assertThrows(UnsupportedOperationException.class, () -> level.getTankPositions().clear());
    }
}
