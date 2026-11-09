package ru.mipt.bit.platformer.graphics;

import com.badlogic.gdx.graphics.g2d.Batch;
import ru.mipt.bit.platformer.mechanics.GameObject;
import ru.mipt.bit.platformer.mechanics.GameWorld;
import ru.mipt.bit.platformer.mechanics.WorldListener;

import java.util.function.Function;
import java.util.LinkedHashMap;
import java.util.Map;

public class WorldGraphics implements WorldListener {
    private final GameWorld world;
    private final Function<GameObject, Renderable> factory;
    private final Map<GameObject, Renderable> renderables = new LinkedHashMap<>();

    public WorldGraphics(GameWorld world, Function<GameObject, Renderable> factory) {
        this.world = world;
        this.factory = factory;
        world.subscribe(this);
    }

    @Override
    public void objectAdded(GameObject object) {
        renderables.put(object, factory.apply(object));
    }

    @Override
    public void objectRemoved(GameObject object) {
        Renderable renderable = renderables.remove(object);
        if (renderable != null) renderable.dispose();
    }

    public void render(Batch batch) {
        for (Renderable renderable : renderables.values()) renderable.render(batch);
    }

    public void dispose() {
        world.unsubscribe(this);
        for (Renderable renderable : renderables.values()) renderable.dispose();
        renderables.clear();
    }
}
