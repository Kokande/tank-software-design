package ru.mipt.bit.platformer.mechanics;

import com.badlogic.gdx.math.GridPoint2;
import ru.mipt.bit.platformer.mechanics.collision.CollisionChecker;
import ru.mipt.bit.platformer.mechanics.collision.LevelBounds;
import ru.mipt.bit.platformer.objects.Bullet;
import ru.mipt.bit.platformer.objects.Tank;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameWorld implements CollisionChecker {
    private final LevelBounds bounds;
    private final List<GameObject> objects = new ArrayList<>();
    private final List<WorldListener> listeners = new ArrayList<>();

    public GameWorld(int width, int height) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("World dimensions must be positive");
        }
        bounds = new LevelBounds(width, height);
    }

    public void subscribe(WorldListener listener) {
        if (listeners.contains(listener)) return;
        listeners.add(listener);
        for (GameObject object : new ArrayList<>(objects)) listener.objectAdded(object);
    }

    public void unsubscribe(WorldListener listener) {
        listeners.remove(listener);
    }

    public void add(GameObject object) {
        if (contains(object)) return;
        object.addedTo(this);
        objects.add(object);
        for (WorldListener listener : new ArrayList<>(listeners)) listener.objectAdded(object);
        if (object instanceof Bullet) resolveBullet((Bullet) object);
    }

    public void remove(GameObject object) {
        if (!objects.remove(object)) return;
        object.removedFrom(this);
        for (WorldListener listener : new ArrayList<>(listeners)) listener.objectRemoved(object);
    }

    public boolean contains(GameObject object) {
        return objects.contains(object);
    }

    public List<GameObject> getObjects() {
        return Collections.unmodifiableList(new ArrayList<>(objects));
    }

    public void live(float tick) {
        if (!Float.isFinite(tick) || tick < 0) throw new IllegalArgumentException("Invalid tick");
        for (GameObject object : new ArrayList<>(objects)) {
            if (contains(object)) object.live(tick);
        }
    }

    @Override
    public boolean collides(GridPoint2 point) {
        if (bounds.occupies(point)) return true;
        for (GameObject object : objects) if (object.occupies(point)) return true;
        return false;
    }

    /** Resolve immediately, including the muzzle tile before the bullet's first tick. */
    public void resolveBullet(Bullet bullet) {
        if (!contains(bullet)) return;
        bullet.updateOwnerSeparation();
        GridPoint2 point = bullet.getCoordinates();
        if (bounds.occupies(point)) {
            remove(bullet);
            return;
        }
        boolean hit = false;
        for (GameObject object : new ArrayList<>(objects)) {
            if (object == bullet || !object.occupies(point)) continue;
            if (object instanceof Tank && !bullet.canHit((Tank) object)) continue;
            hit = true;
            if (object instanceof Tank) ((Tank) object).takeDamage(bullet.getDamage());
            if (object instanceof Bullet) remove(object);
        }
        if (hit) remove(bullet);
    }

    /** A tank entering a bullet tile must be hit even before the bullet updates. */
    public void resolveTankContact(Tank tank, GridPoint2 point) {
        for (GameObject object : new ArrayList<>(objects)) {
            if (!contains(tank)) break;
            if (object instanceof Bullet && object.occupies(point)) {
                Bullet bullet = (Bullet) object;
                if (bullet.canHit(tank)) {
                    remove(bullet);
                    tank.takeDamage(bullet.getDamage());
                }
            }
        }
    }
}
