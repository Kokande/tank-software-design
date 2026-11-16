package ru.mipt.bit.platformer.objects.state;

/**
 * Domain: heavy damage with one-third speed and disabled shooting.
 **/
public final class HeavyTankState extends HealthTankState {
    static final HeavyTankState INSTANCE = new HeavyTankState();

    private HeavyTankState() { }

    @Override
    public float movementSpeedMultiplier() { return 1f / 3f; }

    @Override
    public boolean canShoot() { return false; }
}
