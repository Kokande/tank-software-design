package ru.mipt.bit.platformer.controls;

import ru.mipt.bit.platformer.graphics.HealthBarVisibility;

public class ToggleHealthBarsCommand implements Command {
    private final HealthBarVisibility visibility;

    public ToggleHealthBarsCommand(HealthBarVisibility visibility) {
        this.visibility = visibility;
    }

    @Override
    public void execute() {
        visibility.toggle();
    }
}
