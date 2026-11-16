package ru.mipt.bit.platformer.objects.state;

/**
 * Domain: State-pattern behavior of a tank
 */
public interface TankState {
    default float movementSpeedMultiplier() { return 1f; }

    default boolean canShoot() { return true; }

    default float shotInterval(float baseInterval) { return baseInterval; }

    default float advanceMovement(float progress, float deltaTime, float baseTileDuration) {
        return Math.min(1f, Math.max(0f,
                progress + deltaTime * movementSpeedMultiplier() / baseTileDuration));
    }

    default TankState onHealthChanged(int health, int maxHealth) { return this; }
}
