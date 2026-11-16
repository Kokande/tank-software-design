package ru.mipt.bit.platformer.objects.state;

/**
 * Domain: medium damage  with half the normal movement speed
 **/
public final class MediumTankState extends HealthTankState {
    static final MediumTankState INSTANCE = new MediumTankState();

    private MediumTankState() { }

    @Override
    public float movementSpeedMultiplier() { return 0.5f; }
}
