package ru.mipt.bit.platformer.objects.state;

/**
 * Domain: light damage with normal movement and shooting
 **/
public final class LightTankState extends HealthTankState {
    static final LightTankState INSTANCE = new LightTankState();

    private LightTankState() { }
}
