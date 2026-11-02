package ru.mipt.bit.platformer.objects;

import com.badlogic.gdx.math.GridPoint2;
import ru.mipt.bit.platformer.mechanics.CollisionChecker;
import ru.mipt.bit.platformer.mechanics.Movable;
import ru.mipt.bit.platformer.mechanics.GameObject;
import ru.mipt.bit.platformer.mechanics.GameWorld;
import ru.mipt.bit.platformer.mechanics.Shooter;
import ru.mipt.bit.platformer.mechanics.Direction;

import static com.badlogic.gdx.math.MathUtils.isEqual;
import static ru.mipt.bit.platformer.util.GdxGameUtils.*;

public class Tank implements Movable, GameObject, Shooter {
    private GameWorld world;
    private boolean removed;
    private int health;
    private final int bulletDamage;
    private final float bulletTileDuration;
    private float shotCooldown;
    private static final float SHOT_INTERVAL = 0.4f;
    private final float movementSpeed;

    private GridPoint2 tankCoordinates;
    private GridPoint2 tankDestinationCoordinates;
    private float tankMovementProgress = 1f;
    private float tankRotation;

    public Tank(GridPoint2 initialCoordinates, float movementSpeed) {
        this(initialCoordinates, movementSpeed, 100, 50, 0.1f);
    }

    public Tank(GridPoint2 initialCoordinates, float movementSpeed, int health, int bulletDamage, float bulletTileDuration) {
        if (health < 0 || bulletDamage <= 0 || !Float.isFinite(bulletTileDuration) || bulletTileDuration <= 0) {
            throw new IllegalArgumentException("Invalid tank or bullet settings");
        }
        this.health = health;
        this.bulletDamage = bulletDamage;
        this.bulletTileDuration = bulletTileDuration;
        this.movementSpeed = movementSpeed;
        tankDestinationCoordinates = new GridPoint2(initialCoordinates);
        tankCoordinates = new GridPoint2(initialCoordinates);
        tankRotation = 0f;
    }

    public void update(float deltaTime) {
        if (removed) return;
        shotCooldown = Math.max(0, shotCooldown - deltaTime);
        tankMovementProgress = continueProgress(tankMovementProgress, deltaTime, movementSpeed);
        if (isEqual(tankMovementProgress, 1f)) {
            // record that the tank has reached his/her destination
            tankCoordinates.set(tankDestinationCoordinates);
        }
    }

    @Override
    public void tryMovement(int dx, int dy, float newRotation, CollisionChecker obstacles) {
        if (removed) return;
        tankRotation = newRotation;
        if (!isEqual(tankMovementProgress, 1f)) {
            return;
        }
        GridPoint2 target = new GridPoint2(tankCoordinates.x + dx, tankCoordinates.y + dy);
        if (world != null) {
            world.resolveTankContact(this, target);
            if (removed) return;
        }
        if (obstacles.collides(target)) {
            return;
        }

        tankDestinationCoordinates.set(target);
        tankMovementProgress = 0f;
    }

    @Override
    public boolean isMoving() {
        return !isEqual(tankMovementProgress, 1f);
    }

    // a moving tank takes both the tile it leaves and the tile it is heading to
    @Override
    public boolean occupies(GridPoint2 point) {
        return tankCoordinates.equals(point) || tankDestinationCoordinates.equals(point);
    }

    public GridPoint2 getCoordinates() {
        return tankCoordinates;
    }

    public GridPoint2 getDestinationCoordinates() {
        return tankDestinationCoordinates;
    }

    public float getMovementProgress() {
        return tankMovementProgress;
    }

    public float getRotation() {
        return tankRotation;
    }

    @Override
    public void live(float tick) { update(tick); }

    @Override
    public void addedTo(GameWorld world) {
        if (this.world != null || removed) throw new IllegalStateException("Tank already registered or removed");
        this.world = world;
    }

    @Override
    public void removedFrom(GameWorld world) {
        this.world = null;
        removed = true;
    }

    @Override
    public void shoot() {
        if (world == null || removed || shotCooldown > 0) return;
        Direction direction = getDirection();
        GridPoint2 muzzle = new GridPoint2(tankCoordinates).add(direction.dx(), direction.dy());
        shotCooldown = SHOT_INTERVAL;
        world.add(new Bullet(this, muzzle, direction, bulletDamage, bulletTileDuration));
    }

    public Direction getDirection() {
        for (Direction direction : Direction.values()) {
            if (direction.rotation() == tankRotation) return direction;
        }
        throw new IllegalStateException("Unknown tank orientation: " + tankRotation);
    }

    public int getHealth() { return health; }

    public void takeDamage(int damage) {
        if (damage < 0) throw new IllegalArgumentException("Damage cannot be negative");
        if (removed) return;
        health -= damage;
        // Per the task, zero health is still alive; removal happens below zero.
        if (health < 0 && world != null) world.remove(this);
    }
}
