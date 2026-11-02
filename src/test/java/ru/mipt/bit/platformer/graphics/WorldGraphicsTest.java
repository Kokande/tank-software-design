package ru.mipt.bit.platformer.graphics;

import com.badlogic.gdx.graphics.g2d.Batch;
import org.junit.jupiter.api.Test;
import ru.mipt.bit.platformer.mechanics.GameWorld;
import ru.mipt.bit.platformer.objects.Tree;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WorldGraphicsTest {
    private static class FakeGraphics implements Renderable {
        int draws;
        int disposals;
        public void render(Batch batch) { draws++; }
        public void dispose() { disposals++; }
    }

    @Test
    void initialAndDynamicObjectsUseTheSameObserverAndDisposeOnRemoval() {
        GameWorld world = new GameWorld(4, 4);
        Tree initial = new Tree(1, 1);
        world.add(initial);
        List<FakeGraphics> created = new ArrayList<>();
        WorldGraphics graphics = new WorldGraphics(world, object -> {
            FakeGraphics renderable = new FakeGraphics();
            created.add(renderable);
            return renderable;
        });
        world.add(new Tree(2, 2));
        graphics.render(null);
        assertEquals(2, created.size());
        assertEquals(1, created.get(0).draws);
        assertEquals(1, created.get(1).draws);
        world.remove(initial);
        graphics.render(null);
        assertEquals(1, created.get(0).disposals);
        assertEquals(1, created.get(0).draws);
        assertEquals(2, created.get(1).draws);
        graphics.dispose();
        graphics.dispose();
        world.add(new Tree(3, 3));
        assertEquals(2, created.size());
        assertEquals(1, created.get(1).disposals);
    }
}
