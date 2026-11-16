package ru.mipt.bit.platformer.objects.state;

/**
 * Domain: shared transitions between the three health-based tank states
 * */
public abstract class HealthTankState implements TankState {
    public static HealthTankState forHealth(int health, int maxHealth) {
        if (maxHealth <= 0 || 100L * health < 15L * maxHealth) return HeavyTankState.INSTANCE;
        if (100L * health <= 70L * maxHealth) return MediumTankState.INSTANCE;
        return LightTankState.INSTANCE;
    }

    @Override
    public final TankState onHealthChanged(int health, int maxHealth) {
        return forHealth(health, maxHealth);
    }
}
