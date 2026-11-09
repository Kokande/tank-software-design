package ru.mipt.bit.platformer.objects;

import com.badlogic.gdx.math.GridPoint2;
import ru.mipt.bit.platformer.mechanics.GameObject;
import ru.mipt.bit.platformer.mechanics.GameWorld;
import ru.mipt.bit.platformer.mechanics.movement.Direction;

public class Bullet implements GameObject {
    private final Tank owner;
    private final GridPoint2 coordinates;
    private final Direction direction;
    private final int damage;
    private final float secondsPerTile;
    private float elapsed;
    private boolean clearedOwner;
    private GameWorld world;

    public Bullet(Tank owner, GridPoint2 coordinates, Direction direction, int damage, float secondsPerTile) {
        if (damage <= 0 || !Float.isFinite(secondsPerTile) || secondsPerTile <= 0) {
            throw new IllegalArgumentException("Bullet damage and tile duration must be positive");
        }
        this.owner = owner;
        this.coordinates = new GridPoint2(coordinates);
        this.direction = direction;
        this.damage = damage;
        this.secondsPerTile = secondsPerTile;
    }

    @Override
    public void addedTo(GameWorld world) {
        if (this.world != null) throw new IllegalStateException("Bullet already belongs to a world");
        this.world = world;
    }

    @Override
    public void removedFrom(GameWorld world) {
        this.world = null;
    }

    @Override
    public void live(float tick) {
        if (world == null) return;
        world.resolveBullet(this);
        elapsed += tick;
        while (world != null && elapsed >= secondsPerTile) {
            elapsed -= secondsPerTile;
            coordinates.add(direction.dx(), direction.dy());
            world.resolveBullet(this);
        }
    }

    @Override
    public boolean occupies(GridPoint2 point) {
        return coordinates.equals(point);
    }

    public GridPoint2 getCoordinates() { return new GridPoint2(coordinates); }
    public Direction getDirection() { return direction; }
    public Tank getOwner() { return owner; }
    public int getDamage() { return damage; }

    public void updateOwnerSeparation() {
        if (owner == null || !owner.occupies(coordinates)) clearedOwner = true;
    }

    // A moving tank reserves the muzzle tile. Ignore it only until the bullet
    // leaves its body; afterwards the shooter can be hit like any other tank.
    public boolean canHit(Tank tank) { return tank != owner || clearedOwner; }
}
