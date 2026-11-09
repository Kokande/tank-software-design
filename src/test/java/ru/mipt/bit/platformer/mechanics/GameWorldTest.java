package ru.mipt.bit.platformer.mechanics;

import com.badlogic.gdx.math.GridPoint2;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import ru.mipt.bit.platformer.mechanics.movement.Direction;
import ru.mipt.bit.platformer.objects.Bullet;
import ru.mipt.bit.platformer.objects.Tank;
import ru.mipt.bit.platformer.objects.Tree;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameWorldTest {
    private final GameWorld world = new GameWorld(8, 8);

    private Tank tank(int x, int y) {
        Tank tank = new Tank(new GridPoint2(x, y), 0.4f);
        world.add(tank);
        return tank;
    }

    private List<Bullet> bullets() {
        List<Bullet> bullets = new ArrayList<>();
        for (GameObject object : world.getObjects()) {
            if (object instanceof Bullet) bullets.add((Bullet) object);
        }
        return bullets;
    }

    @ParameterizedTest
    @EnumSource(Direction.class)
    void shootsFromTheNextTileInTheCurrentOrientation(Direction direction) {
        Tank tank = tank(3, 3);
        tank.tryMovement(direction.dx(), direction.dy(), direction.rotation(), point -> true);

        tank.shoot();

        Bullet bullet = bullets().get(0);
        assertEquals(new GridPoint2(3 + direction.dx(), 3 + direction.dy()), bullet.getCoordinates());
        assertEquals(direction, bullet.getDirection());
        assertEquals(100, tank.getHealth());
        world.live(0.1f);
        assertEquals(new GridPoint2(3 + 2 * direction.dx(), 3 + 2 * direction.dy()), bullet.getCoordinates());
    }

    @Test
    void adjacentEnemyIsHitAtSpawnAndZeroHealthSurvives() {
        Tank shooter = tank(1, 1);
        Tank target = tank(2, 1);
        shooter.shoot();
        assertEquals(50, target.getHealth());
        assertTrue(bullets().isEmpty());
        world.live(0.4f);
        shooter.shoot();
        assertEquals(0, target.getHealth());
        assertTrue(world.contains(target));
        world.live(0.4f);
        shooter.shoot();
        assertEquals(-50, target.getHealth());
        assertFalse(world.contains(target));
        assertFalse(world.collides(new GridPoint2(2, 1)));
    }

    @Test
    void usesConfiguredDamage() {
        Tank shooter = new Tank(new GridPoint2(1, 1), 0.4f, 100, 17, 0.05f);
        world.add(shooter);
        Tank target = tank(2, 1);
        shooter.shoot();
        assertEquals(83, target.getHealth());
    }

    @Test
    void treeStopsBulletWithoutBeingRemovedEvenWithALargeTick() {
        Tank shooter = tank(1, 1);
        Tree tree = new Tree(4, 1);
        world.add(tree);
        Tank behindTree = tank(5, 1);
        shooter.shoot();
        world.live(10);
        assertTrue(bullets().isEmpty());
        assertTrue(world.contains(tree));
        assertEquals(100, behindTree.getHealth());
    }

    @Test
    void adjacentTreeAndMapEdgeStopBulletsImmediately() {
        Tank shooter = tank(1, 1);
        world.add(new Tree(2, 1));
        shooter.shoot();
        tank(7, 2).shoot();
        assertTrue(bullets().isEmpty());
    }

    @Test
    void bulletLeavesTheMapAndIsRemoved() {
        tank(5, 1).shoot();
        world.live(1);
        assertTrue(bullets().isEmpty());
    }

    @Test
    void oppositeBulletsDisappearWhenTheyMeet() {
        Tank left = tank(1, 1);
        Tank right = tank(5, 1);
        right.tryMovement(-1, 0, Direction.LEFT.rotation(), point -> true);
        left.shoot();
        right.shoot();
        world.live(0.1f);
        assertTrue(bullets().isEmpty());
        assertEquals(100, left.getHealth());
        assertEquals(100, right.getHealth());
    }

    @Test
    void bulletsOnTheSameSpawnTileDisappear() {
        Tank left = tank(1, 1);
        Tank right = tank(3, 1);
        right.tryMovement(-1, 0, Direction.LEFT.rotation(), point -> true);
        left.shoot();
        right.shoot();
        assertTrue(bullets().isEmpty());
    }

    @Test
    void tankMovingIntoBulletIsHitImmediately() {
        tank(1, 1).shoot();
        Tank target = tank(2, 2);
        target.tryMovement(0, -1, Direction.DOWN.rotation(), world);
        assertEquals(50, target.getHealth());
        assertTrue(target.isMoving());
        assertTrue(bullets().isEmpty());
    }

    @Test
    void shooterCanBeHitByItsOwnBulletAfterItHasLeftTheMuzzle() {
        Tank shooter = tank(1, 1);
        shooter.shoot();
        shooter.tryMovement(1, 0, Direction.RIGHT.rotation(), world);
        assertEquals(50, shooter.getHealth());
        assertTrue(bullets().isEmpty());
    }

    @Test
    void bulletHitsTheReservedTileOfAMovingTank() {
        Tank target = tank(2, 2);
        target.tryMovement(0, -1, Direction.DOWN.rotation(), world);
        tank(1, 1).shoot();
        assertEquals(50, target.getHealth());
        assertTrue(bullets().isEmpty());
    }

    @Test
    void removedTankCannotShootOrExecuteQueuedMovement() {
        Tank tank = tank(1, 1);
        tank.takeDamage(101);
        tank.shoot();
        tank.tryMovement(1, 0, 0, world);
        tank.live(1);
        assertTrue(bullets().isEmpty());
        assertEquals(new GridPoint2(1, 1), tank.getCoordinates());
    }

    @Test
    void heldFireIsLimitedByCooldown() {
        Tank tank = tank(1, 1);
        tank.shoot();
        tank.shoot();
        assertEquals(1, bullets().size());
        world.live(0.4f);
        tank.shoot();
        assertEquals(2, bullets().size());
    }

    @Test
    void listenerReceivesInitialObjectsThenAdditionsAndRemovalsExactlyOnce() {
        Tree tree = new Tree(1, 1);
        world.add(tree);
        List<GameObject> added = new ArrayList<>();
        List<GameObject> removed = new ArrayList<>();
        WorldListener listener = new WorldListener() {
            public void objectAdded(GameObject object) { added.add(object); }
            public void objectRemoved(GameObject object) { removed.add(object); }
        };
        world.subscribe(listener);
        world.subscribe(listener);
        Tank tank = tank(2, 2);
        world.remove(tree);
        world.remove(tree);
        assertEquals(List.of(tree, tank), added);
        assertEquals(List.of(tree), removed);
        world.unsubscribe(listener);
        world.remove(tank);
        assertEquals(List.of(tree), removed);
    }

    @Test
    void removalsDuringTickAreSafeAndRemovedObjectsAreNotUpdated() {
        int[] updates = {0};
        GameObject victim = new GameObject() {
            public boolean occupies(GridPoint2 point) { return false; }
            public void live(float tick) { updates[0]++; }
        };
        world.add(new GameObject() {
            public boolean occupies(GridPoint2 point) { return false; }
            public void live(float tick) { world.remove(victim); world.remove(this); }
        });
        world.add(victim);
        world.live(1);
        assertEquals(0, updates[0]);
        assertTrue(world.getObjects().isEmpty());
    }

    @Test
    void validatesTickAndBulletSettings() {
        assertThrows(IllegalArgumentException.class, () -> world.live(-1));
        assertThrows(IllegalArgumentException.class, () -> world.live(Float.NaN));
        assertThrows(IllegalArgumentException.class,
                () -> new Bullet(null, new GridPoint2(), Direction.RIGHT, 0, 0.1f));
        assertThrows(IllegalArgumentException.class,
                () -> new Bullet(null, new GridPoint2(), Direction.RIGHT, 1, 0));
    }
}
